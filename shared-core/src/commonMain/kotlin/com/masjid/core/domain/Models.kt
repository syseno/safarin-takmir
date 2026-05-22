package com.masjid.core.domain

import kotlinx.serialization.Serializable

// ============================================================
// Core Domain Models
// ============================================================

@Serializable
data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val masjidId: String? = null  // Returned by /api/auth/login
)

@Serializable
data class LocationItem(
    val id: String,
    val name: String
)

@Serializable
data class Masjid(
    val id: String,
    val name: String,
    val addressDetail: String? = null,
    val verified: Boolean = false,
    val phone: String? = null,
    val description: String? = null,
    val imageUrl: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val city: LocationItem? = null,
    val country: LocationItem? = null,
    val district: LocationItem? = null,
    val subDistrict: LocationItem? = null,
    val distance: Double? = null
)

@Serializable
data class Finance(
    val id: String,
    val title: String,
    val amount: Double,   // BE uses Float/number not Int — use Double
    val type: String,     // DEBIT = income, CREDIT = expense
    val description: String? = null,
    val createdAt: String
)

@Serializable
data class MasjidEvent(
    val id: String,
    val title: String,
    val description: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val location: String? = null
)

@Serializable
data class Donation(
    val id: String,
    val type: String,       // SADAQAH, INFAQ, ZAKAT
    val amount: Double,
    val description: String,
    val createdAt: String
)

@Serializable
data class DonationSummaryItem(
    val type: String,
    val total: Double,
    val count: Int
)

@Serializable
data class DonationSummary(
    val byType: List<DonationSummaryItem> = emptyList(),
    val totalAmount: Double = 0.0
)

@Serializable
data class InventoryItem(
    val id: String,
    val name: String,
    val quantity: Int,
    val condition: String,  // GOOD, DAMAGED, LOST
    val createdAt: String
)

// ============================================================
// Adzan / Prayer Schedule
// ============================================================

@Serializable
data class AdzanTime(
    val name: String,
    val time: String,
    val isNext: Boolean = false
)

@Serializable
data class DailyAdzanSchedule(
    val date: String,
    val dayLabel: String, // e.g. "Hari Ini", "Besok"
    val hijriDate: String? = null,
    val times: List<AdzanTime>
)

@Serializable
data class PrayerMethodItem(
    val id: Int? = null,
    val name: String = ""
)

@Serializable
data class PrayerMethodsResponse(
    val code: Int,
    val status: String,
    val data: Map<String, PrayerMethodItem>
)

@Serializable
data class PrayerScheduleResponse(
    val code: Int,
    val status: String,
    val data: PrayerData
)

@Serializable
data class PrayerData(
    val timings: Map<String, String>,
    val date: PrayerDate
)

@Serializable
data class PrayerDate(
    val readable: String,
    val timestamp: String,
    val hijri: HijriDate
)

@Serializable
data class HijriDate(
    val date: String,
    val day: String,
    val month: HijriMonth,
    val year: String
)

@Serializable
data class HijriMonth(
    val number: Int,
    val en: String,
    val ar: String
)

// ============================================================
// Dashboard — matches BE response: { finance, donations, inventory, recentFinance, upcomingEvents }
// ============================================================

@Serializable
data class FinanceSummary(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,
    val incomeCount: Int = 0,
    val expenseCount: Int = 0
)

@Serializable
data class InventorySummary(
    val totalItems: Int = 0,
    val byCondition: List<ConditionSummaryItem> = emptyList()
)

@Serializable
data class ConditionSummaryItem(
    val condition: String,
    val count: Int,
    val totalQuantity: Int
)

@Serializable
data class DashboardData(
    val finance: FinanceSummary = FinanceSummary(),
    val donations: DonationSummary = DonationSummary(),
    val inventory: InventorySummary = InventorySummary(),
    val recentFinance: List<Finance> = emptyList(),
    val upcomingEvents: List<MasjidEvent> = emptyList()
)

// ============================================================
// API Response Wrappers
// ============================================================

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
)

@Serializable
data class PaginatedResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val total: Int = 0,
    val page: Int = 1,
    val limit: Int = 10
)

/** Wraps paginated finance list from BE: { records, pagination } */
@Serializable
data class FinanceListResponse(
    val records: List<Finance> = emptyList(),
    val pagination: PaginationMeta = PaginationMeta()
)

/** Wraps paginated event list from BE: { records, pagination } */
@Serializable
data class EventListResponse(
    val records: List<MasjidEvent> = emptyList(),
    val pagination: PaginationMeta = PaginationMeta()
)

/** Wraps paginated donation list from BE: no wrapper — direct list */
@Serializable
data class DonationListResponse(
    val donations: List<Donation> = emptyList()
)

/** Wraps inventory list from BE — direct list */
@Serializable
data class InventoryListResponse(
    val items: List<InventoryItem> = emptyList()
)

/** Public search response: { masjids, pagination } */
@Serializable
data class SearchMasjidResponse(
    val masjids: List<Masjid> = emptyList(),
    val pagination: PaginationMeta = PaginationMeta()
)

/** Public finance response: { masjid, summary, records, pagination } */
@Serializable
data class PublicFinanceResponse(
    val masjid: Masjid? = null,
    val summary: PublicFinanceSummary = PublicFinanceSummary(),
    val records: List<Finance> = emptyList(),
    val pagination: PaginationMeta = PaginationMeta()
)

@Serializable
data class PublicFinanceSummary(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0
)

/** Public events response: { masjid, events } */
@Serializable
data class PublicEventsResponse(
    val masjid: Masjid? = null,
    val events: List<MasjidEvent> = emptyList()
)

@Serializable
data class PaginationMeta(
    val page: Int = 1,
    val limit: Int = 20,
    val total: Int = 0,
    val totalPages: Int = 1
)

// ============================================================
// Auth DTOs
// ============================================================

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

/** BE login/register returns: { user, token } */
@Serializable
data class LoginResponse(
    val token: String,         // was: accessToken — now matches BE
    val user: User
)

// ============================================================
// Takmir Request DTOs
// ============================================================

@Serializable
data class CreateFinanceRequest(
    val title: String,
    val amount: Double,
    val type: String,
    val description: String,       // required by BE (min 1 char)
    val inventoryId: String? = null,
    val donationId: String? = null
)

@Serializable
data class UpdateFinanceRequest(
    val title: String? = null,
    val amount: Double? = null,
    val type: String? = null,
    val description: String? = null
)

@Serializable
data class CreateEventRequest(
    val title: String,
    val description: String,
    val date: String,       // ISO 8601 date e.g. "2025-06-15"
    val startTime: String,  // HH:mm e.g. "08:00"
    val endTime: String,    // HH:mm e.g. "12:00"
    val location: String? = null
)

@Serializable
data class UpdateEventRequest(
    val title: String? = null,
    val description: String? = null,
    val date: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val location: String? = null
)

@Serializable
data class CreateDonationRequest(
    val type: String,          // SADAQAH, INFAQ, ZAKAT
    val amount: Double,
    val description: String
)

@Serializable
data class CreateInventoryRequest(
    val name: String,
    val quantity: Int = 0,
    val condition: String = "GOOD"   // GOOD, DAMAGED, LOST
)

@Serializable
data class UpdateQuantityRequest(
    val quantity: Int
)

@Serializable
data class UpdateConditionRequest(
    val condition: String   // GOOD, DAMAGED, LOST
)

@Serializable
data class UpdateProfileRequest(
    val name: String? = null,
    val phone: String? = null,
    val description: String? = null,
    val addressDetail: String? = null,
    val countryId: String? = null,
    val cityId: String? = null,
    val districtId: String? = null,
    val subDistrictId: String? = null,
    val imageUrl: String? = null
)
