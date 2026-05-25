package com.masjid.takmir.data.repository

import com.masjid.core.domain.DailyAdzanSchedule
import com.masjid.core.domain.PrayerScheduleResponse
import com.masjid.takmir.data.local.PrayerDao
import com.masjid.takmir.data.local.SettingsManager
import com.masjid.takmir.data.local.entity.PrayerEntity
import com.masjid.takmir.data.remote.PrayerApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrayerRepositoryImpl @Inject constructor(
    private val apiService: PrayerApiService,
    private val prayerDao: PrayerDao,
    private val settingsManager: SettingsManager
) : PrayerRepository {

    private val fallbackMethods = mapOf(
        "KEMENAG" to com.masjid.core.domain.PrayerMethodItem(20, "Kementerian Agama Republik Indonesia"),
        "JAKIM" to com.masjid.core.domain.PrayerMethodItem(17, "Jabatan Kemajuan Islam Malaysia (JAKIM)"),
        "SINGAPORE" to com.masjid.core.domain.PrayerMethodItem(11, "Majlis Ugama Islam Singapura, Singapore"),
        "MWL" to com.masjid.core.domain.PrayerMethodItem(3, "Muslim World League"),
        "ISNA" to com.masjid.core.domain.PrayerMethodItem(2, "Islamic Society of North America (ISNA)"),
        "EGYPT" to com.masjid.core.domain.PrayerMethodItem(5, "Egyptian General Authority of Survey"),
        "MAKKAH" to com.masjid.core.domain.PrayerMethodItem(4, "Umm Al-Qura University, Makkah"),
        "KARACHI" to com.masjid.core.domain.PrayerMethodItem(1, "University of Islamic Sciences, Karachi"),
        "TEHRAN" to com.masjid.core.domain.PrayerMethodItem(7, "Institute of Geophysics, University of Tehran"),
        "JAFARI" to com.masjid.core.domain.PrayerMethodItem(0, "Shia Ithna-Ashari, Leva Institute, Qum"),
        "GULF" to com.masjid.core.domain.PrayerMethodItem(8, "Gulf Region"),
        "KUWAIT" to com.masjid.core.domain.PrayerMethodItem(9, "Kuwait"),
        "QATAR" to com.masjid.core.domain.PrayerMethodItem(10, "Qatar"),
        "FRANCE" to com.masjid.core.domain.PrayerMethodItem(12, "Union Organization Islamic de France"),
        "TURKEY" to com.masjid.core.domain.PrayerMethodItem(13, "Diyanet İşleri Başkanlığı, Turkey (experimental)"),
        "RUSSIA" to com.masjid.core.domain.PrayerMethodItem(14, "Spiritual Administration of Muslims of Russia"),
        "MOONSIGHTING" to com.masjid.core.domain.PrayerMethodItem(15, "Moonsighting Committee Worldwide"),
        "DUBAI" to com.masjid.core.domain.PrayerMethodItem(16, "Dubai (experimental)"),
        "TUNISIA" to com.masjid.core.domain.PrayerMethodItem(18, "Tunisia"),
        "ALGERIA" to com.masjid.core.domain.PrayerMethodItem(19, "Algeria"),
        "MOROCCO" to com.masjid.core.domain.PrayerMethodItem(21, "Morocco"),
        "PORTUGAL" to com.masjid.core.domain.PrayerMethodItem(22, "Comunidade Islamica de Lisboa"),
        "JORDAN" to com.masjid.core.domain.PrayerMethodItem(23, "Ministry of Awqaf, Islamic Affairs and Holy Places, Jordan")
    )

    override fun getPrayerSchedule(): Flow<List<DailyAdzanSchedule>> {
        return prayerDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun refreshPrayerSchedule(lat: Double, lng: Double) {
        val method = settingsManager.prayerMethod.first()
        val sdfDb = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayStr = sdfDb.format(Date())

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val tomorrowStr = sdfDb.format(calendar.time)

        // 1. Clean up old data
        prayerDao.deleteOldSchedules(todayStr)

        // 2. Fix labels for existing data if they drifted
        val cachedToday = prayerDao.getByDate(todayStr)
        val cachedTomorrow = prayerDao.getByDate(tomorrowStr)

        if (cachedToday != null && cachedToday.dayLabel != "Hari Ini") {
            prayerDao.insert(cachedToday.copy(dayLabel = "Hari Ini"))
        }
        if (cachedTomorrow != null && cachedTomorrow.dayLabel != "Besok") {
            prayerDao.insert(cachedTomorrow.copy(dayLabel = "Besok"))
        }

        if (cachedToday != null && cachedTomorrow != null) {
            println("PRAYER: Both days cached and labels updated. Skipping network.")
            return
        }

        println("PRAYER: Refreshing for $lat, $lng using method $method")
        try {
            // Fetch Today if not cached
            if (cachedToday == null) {
                val todayResponse = apiService.getPrayerTimes(lat, lng, method = method)
                println("PRAYER: Today response code: ${todayResponse.code}")
                if (todayResponse.code == 200) {
                    saveResponse(todayResponse, "Hari Ini")
                }
            }

            // Fetch Tomorrow if not cached
            if (cachedTomorrow == null) {
                val sdfApi = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val tomorrowApiStr = sdfApi.format(calendar.time)
                
                val tomorrowResponse = apiService.getPrayerTimes(lat, lng, tomorrowApiStr, method = method)
                if (tomorrowResponse.code == 200) {
                    saveResponse(tomorrowResponse, "Besok")
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun getPrayerMethods(): Map<String, com.masjid.core.domain.PrayerMethodItem> {
        return try {
            val response = apiService.getMethods()
            if (response.code == 200) {
                response.data
            } else {
                fallbackMethods
            }
        } catch (e: Exception) {
            e.printStackTrace()
            fallbackMethods
        }
    }

    private suspend fun saveResponse(response: PrayerScheduleResponse, label: String) {
        val data = response.data
        val timings = data.timings
        val date = data.date
        
        // Parse dd-MM-yyyy from API to yyyy-MM-dd for DB
        val sdfApi = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val sdfDb = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        
        val dateObj = try {
            sdfApi.parse(date.readable) ?: Date(date.timestamp.toLong() * 1000)
        } catch (e: Exception) {
            Date(date.timestamp.toLong() * 1000)
        }
        val dateStr = sdfDb.format(dateObj)
        
        val domain = DailyAdzanSchedule(
            date = dateStr,
            dayLabel = label,
            hijriDate = "${date.hijri.day} ${date.hijri.month.en} ${date.hijri.year}",
            times = timings.filter { it.key in listOf("Fajr", "Sunrise", "Dhuhr", "Asr", "Maghrib", "Isha") }
                .map { (name, time) ->
                    val translatedName = when(name) {
                        "Fajr" -> "Subuh"
                        "Sunrise" -> "Terbit"
                        "Dhuhr" -> "Dzuhur"
                        "Asr" -> "Ashar"
                        "Maghrib" -> "Maghrib"
                        "Isha" -> "Isya"
                        else -> name
                    }
                    com.masjid.core.domain.AdzanTime(translatedName, time)
                }
                .sortedBy { 
                    // Ensure correct order: Subuh, Terbit, Dzuhur, Ashar, Maghrib, Isya
                    when(it.name) {
                        "Subuh" -> 1
                        "Terbit" -> 2
                        "Dzuhur" -> 3
                        "Ashar" -> 4
                        "Maghrib" -> 5
                        "Isya" -> 6
                        else -> 7
                    }
                }
        )
        
        prayerDao.insert(PrayerEntity.fromDomain(domain))
    }
}
