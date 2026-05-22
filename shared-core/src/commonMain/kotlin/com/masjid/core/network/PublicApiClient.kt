package com.masjid.core.network

import com.masjid.core.domain.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

/**
 * API client for public-facing endpoints.
 * No authentication required.
 * Base paths: /api/public/...
 */
class PublicApiClient(private val client: HttpClient) {

    /**
     * GET /api/public/masjid?q=&page=&limit=
     * BE returns: { success, message, data: { masjids: [...], pagination: {...} } }
     */
    suspend fun searchMasjid(query: String? = null, page: Int = 1, limit: Int = 20): SearchMasjidResponse {
        val response = client.get("/api/public/masjid") {
            if (!query.isNullOrBlank()) parameter("q", query)
            parameter("page", page)
            parameter("limit", limit)
        }
        val result: ApiResponse<SearchMasjidResponse> = response.body()
        return result.data ?: SearchMasjidResponse()
    }

    /**
     * GET /api/public/masjid/nearest?latitude=&longitude=&radius=&limit=&cityId=
     * BE returns: { success: Boolean, message: String, data: List<Masjid> }
     */
    suspend fun getNearestMasjid(
        latitude: Double,
        longitude: Double,
        radius: Double? = null,
        limit: Int? = null,
        cityId: String? = null
    ): List<Masjid> {
        val response = client.get("/api/public/masjid/nearest") {
            parameter("latitude", latitude)
            parameter("longitude", longitude)
            if (radius != null) parameter("radius", radius)
            if (limit != null) parameter("limit", limit)
            if (cityId != null) parameter("cityId", cityId)
        }
        val result: ApiResponse<List<Masjid>> = response.body()
        return result.data ?: emptyList()
    }

    /**
     * GET /api/public/masjid/:masjidId
     * BE returns: { success, message, data: <Masjid> }
     */
    suspend fun getMasjidDetail(masjidId: String): Masjid {
        val response = client.get("/api/public/masjid/$masjidId")
        val result: ApiResponse<Masjid> = response.body()
        return result.data ?: throw Exception(result.message)
    }

    /**
     * GET /api/public/masjid/:masjidId/finance?page=&limit=
     * BE returns: { success, message, data: { masjid, summary, records, pagination } }
     */
    suspend fun getMasjidFinance(masjidId: String, page: Int = 1, limit: Int = 20): PublicFinanceResponse {
        val response = client.get("/api/public/masjid/$masjidId/finance") {
            parameter("page", page)
            parameter("limit", limit)
        }
        val result: ApiResponse<PublicFinanceResponse> = response.body()
        return result.data ?: PublicFinanceResponse()
    }

    /**
     * GET /api/public/masjid/:masjidId/events
     * BE returns: { success, message, data: { masjid, events: [...] } }
     */
    suspend fun getMasjidEvents(masjidId: String): PublicEventsResponse {
        val response = client.get("/api/public/masjid/$masjidId/events")
        val result: ApiResponse<PublicEventsResponse> = response.body()
        return result.data ?: PublicEventsResponse()
    }
}
