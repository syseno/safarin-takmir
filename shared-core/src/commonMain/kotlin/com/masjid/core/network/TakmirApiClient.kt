package com.masjid.core.network

import com.masjid.core.domain.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

/**
 * API client for Takmir (admin) endpoints.
 * All paths: /api/takmir/:masjidId/<module>
 * Requires authenticated MASJID_ADMIN user (JWT Bearer token).
 */
class TakmirApiClient(private val client: HttpClient) {

    // ── Dashboard ──────────────────────────────────────────────────────
    /** GET /api/takmir/:masjidId/dashboard */
    suspend fun getDashboard(masjidId: String): DashboardData {
        val response = client.get("/api/takmir/$masjidId/dashboard")
        val result: ApiResponse<DashboardData> = response.body()
        return result.data ?: DashboardData()
    }

    // ── Finance / Transactions ─────────────────────────────────────────
    /** GET /api/takmir/:masjidId/finance?page=&limit= */
    suspend fun getFinances(masjidId: String, page: Int = 1, limit: Int = 20): List<Finance> {
        val response = client.get("/api/takmir/$masjidId/finance") {
            url { parameters.append("page", page.toString()); parameters.append("limit", limit.toString()) }
        }
        val result: ApiResponse<FinanceListResponse> = response.body()
        return result.data?.records ?: emptyList()
    }

    /** GET /api/takmir/:masjidId/finance/:id */
    suspend fun getFinanceDetail(masjidId: String, financeId: String): Finance {
        val response = client.get("/api/takmir/$masjidId/finance/$financeId")
        val result: ApiResponse<Finance> = response.body()
        return result.data ?: throw Exception(result.message)
    }

    /** POST /api/takmir/:masjidId/finance */
    suspend fun createFinance(masjidId: String, request: CreateFinanceRequest): Finance {
        val response = client.post("/api/takmir/$masjidId/finance") {
            setBody(request)
        }
        val result: ApiResponse<Finance> = response.body()
        return result.data ?: throw Exception(result.message)
    }

    /** DELETE /api/takmir/:masjidId/finance/:id (BE has no PUT for finance) */
    suspend fun deleteFinance(masjidId: String, financeId: String) {
        client.delete("/api/takmir/$masjidId/finance/$financeId")
    }

    // ── Events ─────────────────────────────────────────────────────────
    /** GET /api/takmir/:masjidId/event?page=&limit= */
    suspend fun getEvents(masjidId: String, page: Int = 1, limit: Int = 20): List<MasjidEvent> {
        val response = client.get("/api/takmir/$masjidId/event") {
            url { parameters.append("page", page.toString()); parameters.append("limit", limit.toString()) }
        }
        val result: ApiResponse<EventListResponse> = response.body()
        return result.data?.records ?: emptyList()
    }

    /** GET /api/takmir/:masjidId/event/:id */
    suspend fun getEventDetail(masjidId: String, eventId: String): MasjidEvent {
        val response = client.get("/api/takmir/$masjidId/event/$eventId")
        val result: ApiResponse<MasjidEvent> = response.body()
        return result.data ?: throw Exception(result.message)
    }

    /** POST /api/takmir/:masjidId/event */
    suspend fun createEvent(masjidId: String, request: CreateEventRequest): MasjidEvent {
        val response = client.post("/api/takmir/$masjidId/event") {
            setBody(request)
        }
        val result: ApiResponse<MasjidEvent> = response.body()
        return result.data ?: throw Exception(result.message)
    }

    /** PUT /api/takmir/:masjidId/event/:id */
    suspend fun updateEvent(masjidId: String, eventId: String, request: UpdateEventRequest): MasjidEvent {
        val response = client.put("/api/takmir/$masjidId/event/$eventId") {
            setBody(request)
        }
        val result: ApiResponse<MasjidEvent> = response.body()
        return result.data ?: throw Exception(result.message)
    }

    /** DELETE /api/takmir/:masjidId/event/:id?deleteType= */
    suspend fun deleteEvent(masjidId: String, eventId: String, deleteType: String = "SINGLE") {
        client.delete("/api/takmir/$masjidId/event/$eventId") {
            url { parameters.append("deleteType", deleteType) }
        }
    }

    /** POST /api/takmir/:masjidId/event/upload */
    suspend fun uploadEventPoster(masjidId: String, imageBytes: ByteArray, filename: String): String {
        val response = client.post("/api/takmir/$masjidId/event/upload") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("image", imageBytes, Headers.build {
                            append(HttpHeaders.ContentType, "image/jpeg")
                            append(HttpHeaders.ContentDisposition, "filename=\"$filename\"")
                        })
                    }
                )
            )
            // Clear default content type to let Ktor configure multipart boundary
            headers.remove(HttpHeaders.ContentType)
        }
        val result: ApiResponse<ImageUploadData> = response.body()
        return result.data?.imageUrl ?: throw Exception(result.message)
    }

    // ── Profile ────────────────────────────────────────────────────────
    /** GET /api/takmir/:masjidId/profile */
    suspend fun getProfile(masjidId: String): Masjid {
        val response = client.get("/api/takmir/$masjidId/profile")
        val result: ApiResponse<Masjid> = response.body()
        return result.data ?: throw Exception(result.message)
    }

    /** PUT /api/takmir/:masjidId/profile */
    suspend fun updateProfile(masjidId: String, request: UpdateProfileRequest): Masjid {
        val response = client.put("/api/takmir/$masjidId/profile") {
            setBody(request)
        }
        val result: ApiResponse<Masjid> = response.body()
        return result.data ?: throw Exception(result.message)
    }

    // ── Donation ───────────────────────────────────────────────────────
    /** POST /api/takmir/:masjidId/donation */
    suspend fun createDonation(masjidId: String, request: CreateDonationRequest): Donation {
        val response = client.post("/api/takmir/$masjidId/donation") {
            setBody(request)
        }
        val result: ApiResponse<Donation> = response.body()
        return result.data ?: throw Exception(result.message)
    }

    /** GET /api/takmir/:masjidId/donation */
    suspend fun getDonations(masjidId: String): List<Donation> {
        val response = client.get("/api/takmir/$masjidId/donation")
        val result: ApiResponse<List<Donation>> = response.body()
        return result.data ?: emptyList()
    }

    /** GET /api/takmir/:masjidId/donation/summary */
    suspend fun getDonationSummary(masjidId: String): DonationSummary {
        val response = client.get("/api/takmir/$masjidId/donation/summary")
        val result: ApiResponse<DonationSummary> = response.body()
        return result.data ?: DonationSummary()
    }

    // ── Inventory ──────────────────────────────────────────────────────
    /** POST /api/takmir/:masjidId/inventory */
    suspend fun createInventory(masjidId: String, request: CreateInventoryRequest): InventoryItem {
        val response = client.post("/api/takmir/$masjidId/inventory") {
            setBody(request)
        }
        val result: ApiResponse<InventoryItem> = response.body()
        return result.data ?: throw Exception(result.message)
    }

    /** GET /api/takmir/:masjidId/inventory */
    suspend fun getInventoryList(masjidId: String): List<InventoryItem> {
        val response = client.get("/api/takmir/$masjidId/inventory")
        val result: ApiResponse<List<InventoryItem>> = response.body()
        return result.data ?: emptyList()
    }

    /** GET /api/takmir/:masjidId/inventory/:id */
    suspend fun getInventoryDetail(masjidId: String, itemId: String): InventoryItem {
        val response = client.get("/api/takmir/$masjidId/inventory/$itemId")
        val result: ApiResponse<InventoryItem> = response.body()
        return result.data ?: throw Exception(result.message)
    }

    /** PATCH /api/takmir/:masjidId/inventory/:id/quantity */
    suspend fun updateInventoryQuantity(masjidId: String, itemId: String, request: UpdateQuantityRequest): InventoryItem {
        val response = client.patch("/api/takmir/$masjidId/inventory/$itemId/quantity") {
            setBody(request)
        }
        val result: ApiResponse<InventoryItem> = response.body()
        return result.data ?: throw Exception(result.message)
    }

    /** PATCH /api/takmir/:masjidId/inventory/:id/condition */
    suspend fun updateInventoryCondition(masjidId: String, itemId: String, request: UpdateConditionRequest): InventoryItem {
        val response = client.patch("/api/takmir/$masjidId/inventory/$itemId/condition") {
            setBody(request)
        }
        val result: ApiResponse<InventoryItem> = response.body()
        return result.data ?: throw Exception(result.message)
    }
}
