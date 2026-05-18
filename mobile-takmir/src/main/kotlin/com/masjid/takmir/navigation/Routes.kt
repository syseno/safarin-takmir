package com.masjid.takmir.navigation

/**
 * All route definitions for Takmir app navigation.
 */
sealed class TakmirRoutes(val route: String) {
    object Login : TakmirRoutes("login")
    object Dashboard : TakmirRoutes("dashboard")
    object Transactions : TakmirRoutes("transactions")
    object TransactionForm : TakmirRoutes("transaction-form?id={id}") {
        fun createRoute(id: String? = null) =
            if (id != null) "transaction-form?id=$id" else "transaction-form"
    }
    object Events : TakmirRoutes("events")
    object EventForm : TakmirRoutes("event-form?id={id}") {
        fun createRoute(id: String? = null) =
            if (id != null) "event-form?id=$id" else "event-form"
    }
    object Donation : TakmirRoutes("donation")
    object DonationForm : TakmirRoutes("donation-form")
    object Inventory : TakmirRoutes("inventory")
    object InventoryForm : TakmirRoutes("inventory-form")
    object Profile : TakmirRoutes("profile")
}
