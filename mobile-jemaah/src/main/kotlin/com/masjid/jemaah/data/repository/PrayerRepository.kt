package com.masjid.jemaah.data.repository

import com.masjid.core.domain.AdzanTime
import com.masjid.core.domain.DailyAdzanSchedule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

interface PrayerRepository {
    fun getPrayerSchedule(): Flow<List<DailyAdzanSchedule>>
    suspend fun refreshPrayerSchedule(lat: Double, lng: Double)
    suspend fun getPrayerMethods(): Map<String, com.masjid.core.domain.PrayerMethodItem>
}
