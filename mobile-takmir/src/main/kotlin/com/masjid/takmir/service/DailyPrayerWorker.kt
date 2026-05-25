package com.masjid.takmir.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.masjid.takmir.data.repository.PrayerRepository
import com.masjid.takmir.location.LocationProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull

@HiltWorker
class DailyPrayerWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val prayerRepository: PrayerRepository,
    private val locationProvider: LocationProvider,
    private val adzanScheduler: AdzanScheduler
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val location = locationProvider.getCurrentLocation() ?: return Result.retry()
        
        prayerRepository.refreshPrayerSchedule(location.latitude, location.longitude)
        
        val schedules = prayerRepository.getPrayerSchedule().firstOrNull()
        val today = schedules?.find { it.dayLabel == "Hari Ini" }
        
        if (today != null) {
            adzanScheduler.scheduleAdzan(today)
        }
        
        return Result.success()
    }
}
