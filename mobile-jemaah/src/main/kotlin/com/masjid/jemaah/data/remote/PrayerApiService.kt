package com.masjid.jemaah.data.remote

import com.masjid.core.domain.PrayerScheduleResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject

class PrayerApiService @Inject constructor(
    private val client: HttpClient
) {
    /**
     * GET https://api.aladhan.com/v1/timings?latitude={lat}&longitude={lng}&method=20
     */
    suspend fun getPrayerTimes(
        lat: Double,
        lng: Double,
        date: String? = null,
        method: Int = 20
    ): PrayerScheduleResponse {
        val url = if (date != null) "https://api.aladhan.com/v1/timings/$date" 
                  else "https://api.aladhan.com/v1/timings"
        return client.get(url) {
            parameter("latitude", lat)
            parameter("longitude", lng)
            parameter("method", method)
        }.body()
    }

    /**
     * GET https://api.aladhan.com/v1/methods
     */
    suspend fun getMethods(): com.masjid.core.domain.PrayerMethodsResponse {
        return client.get("https://api.aladhan.com/v1/methods").body()
    }
}
