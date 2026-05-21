package com.masjid.jemaah.feature.kiblat

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masjid.jemaah.location.LocationProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.*

data class KiblatState(
    val qiblaDirection: Float = 0f,
    val currentHeading: Float = 0f,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class KiblatViewModel @Inject constructor(
    private val locationProvider: LocationProvider,
    private val sensorManager: SensorManager
) : ViewModel(), SensorEventListener {

    private val _state = MutableStateFlow(KiblatState())
    val state = _state.asStateFlow()

    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    init {
        startTracking()
    }

    private fun startTracking() {
        viewModelScope.launch {
            try {
                val location = locationProvider.getCurrentLocation()
                if (location != null) {
                    val qibla = calculateQibla(location.latitude, location.longitude)
                    _state.value = _state.value.copy(qiblaDirection = qibla, isLoading = false)
                    registerSensors()
                } else {
                    _state.value = _state.value.copy(error = "Gagal mendapatkan lokasi", isLoading = false)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    private fun registerSensors() {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also { magneticField ->
            sensorManager.registerListener(this, magneticField, SensorManager.SENSOR_DELAY_UI)
        }
    }

    private fun calculateQibla(lat: Double, lng: Double): Float {
        val phi1 = Math.toRadians(lat)
        val lambda1 = Math.toRadians(lng)
        val phi2 = Math.toRadians(21.422487) // Mecca Lat
        val lambda2 = Math.toRadians(39.826206) // Mecca Long

        val deltaLambda = lambda2 - lambda1
        val y = sin(deltaLambda)
        val x = cos(phi1) * tan(phi2) - sin(phi1) * cos(deltaLambda)
        
        var qibla = Math.toDegrees(atan2(y, x)).toFloat()
        if (qibla < 0) qibla += 360f
        return qibla
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
        }

        updateOrientationAngles()
    }

    private fun updateOrientationAngles() {
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading)
        SensorManager.getOrientation(rotationMatrix, orientationAngles)
        
        // azimuth is orientationAngles[0]
        var azimuth = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
        if (azimuth < 0) azimuth += 360f
        
        _state.value = _state.value.copy(currentHeading = azimuth)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onCleared() {
        super.onCleared()
        sensorManager.unregisterListener(this)
    }
}
