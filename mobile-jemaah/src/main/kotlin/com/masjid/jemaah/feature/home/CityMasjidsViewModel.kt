package com.masjid.jemaah.feature.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masjid.core.domain.AppResult
import com.masjid.core.domain.Masjid
import com.masjid.jemaah.domain.usecase.GetNearestMasjidsUseCase
import com.masjid.jemaah.location.LocationProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CityMasjidsState(
    val masjids: List<Masjid> = emptyList(),
    val cityName: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CityMasjidsViewModel @Inject constructor(
    private val getNearestMasjidsUseCase: GetNearestMasjidsUseCase,
    private val locationProvider: LocationProvider,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val cityId: String = savedStateHandle.get<String>("cityId") ?: ""

    private val _state = MutableStateFlow(CityMasjidsState())
    val state: StateFlow<CityMasjidsState> = _state.asStateFlow()

    init {
        loadCityMasjids()
    }

    fun loadCityMasjids() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            val location = locationProvider.getCurrentLocation()
            val lat = location?.latitude ?: -6.200000
            val lng = location?.longitude ?: 106.816666

            // Try to resolve city name if available, otherwise we use a generic placeholder or fetch from the first result
            var resolvedCityName: String? = null
            if (location != null) {
                resolvedCityName = locationProvider.getLocationNames(lat, lng).first
            }

            when (val result = getNearestMasjidsUseCase(latitude = lat, longitude = lng, cityId = cityId)) {
                is AppResult.Success -> {
                    val masjidsList = result.data
                    // If geocoder didn't return a city name, use the city name from the first masjid found in that city
                    val finalCityName = resolvedCityName ?: masjidsList.firstOrNull()?.city?.name ?: "Kota"
                    _state.value = _state.value.copy(
                        isLoading = false,
                        masjids = masjidsList,
                        cityName = finalCityName
                    )
                }
                is AppResult.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }
}
