package com.masjid.takmir.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.masjid.core.domain.AdzanTime
import com.masjid.core.domain.DailyAdzanSchedule

@Entity(tableName = "prayer_schedules")
data class PrayerEntity(
    @PrimaryKey
    val date: String, // YYYY-MM-DD
    val dayLabel: String,
    val hijriDate: String?,
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String
) {
    fun toDomain() = DailyAdzanSchedule(
        date = date,
        dayLabel = dayLabel,
        hijriDate = hijriDate,
        times = listOf(
            AdzanTime("Subuh", fajr),
            AdzanTime("Terbit", sunrise),
            AdzanTime("Dzuhur", dhuhr),
            AdzanTime("Ashar", asr),
            AdzanTime("Maghrib", maghrib),
            AdzanTime("Isya", isha)
        )
    )

    companion object {
        fun fromDomain(domain: DailyAdzanSchedule) = PrayerEntity(
            date = domain.date,
            dayLabel = domain.dayLabel,
            hijriDate = domain.hijriDate,
            fajr = domain.times.find { it.name == "Subuh" }?.time ?: "",
            sunrise = domain.times.find { it.name == "Terbit" }?.time ?: "",
            dhuhr = domain.times.find { it.name == "Dzuhur" }?.time ?: "",
            asr = domain.times.find { it.name == "Ashar" }?.time ?: "",
            maghrib = domain.times.find { it.name == "Maghrib" }?.time ?: "",
            isha = domain.times.find { it.name == "Isya" }?.time ?: ""
        )
    }
}
