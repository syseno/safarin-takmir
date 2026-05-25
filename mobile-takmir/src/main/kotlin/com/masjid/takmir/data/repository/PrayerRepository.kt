package com.masjid.takmir.data.repository

import com.masjid.core.domain.DailyAdzanSchedule
import kotlinx.coroutines.flow.Flow

interface PrayerRepository {
    fun getPrayerSchedule(): Flow<List<DailyAdzanSchedule>>
    suspend fun refreshPrayerSchedule(lat: Double, lng: Double)
    suspend fun getPrayerMethods(): Map<String, com.masjid.core.domain.PrayerMethodItem>
}
