package com.masjid.takmir.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.masjid.takmir.feature.auth.LoginScreen
import com.masjid.takmir.feature.dashboard.DashboardScreen
import com.masjid.takmir.feature.donation.DonationFormScreen
import com.masjid.takmir.feature.donation.DonationScreen
import com.masjid.takmir.feature.event.EventFormScreen
import com.masjid.takmir.feature.event.EventScreen
import com.masjid.takmir.feature.inventory.InventoryFormScreen
import com.masjid.takmir.feature.inventory.InventoryScreen
import com.masjid.takmir.feature.profile.ProfileScreen
import com.masjid.takmir.feature.transaction.TransactionFormScreen
import com.masjid.takmir.feature.transaction.TransactionScreen

@Composable
fun TakmirNavGraph(
    navController: NavHostController,
    isLoggedIn: Boolean
) {
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) TakmirRoutes.Dashboard.route else TakmirRoutes.Login.route
    ) {
        composable(TakmirRoutes.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(TakmirRoutes.Dashboard.route) {
                        popUpTo(TakmirRoutes.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(TakmirRoutes.Dashboard.route) {
            DashboardScreen(
                onNavigateToTransactions = { navController.navigate(TakmirRoutes.Transactions.route) },
                onNavigateToEvents = { navController.navigate(TakmirRoutes.Events.route) },
                onNavigateToProfile = { navController.navigate(TakmirRoutes.Profile.route) },
                onNavigateToDonation = { navController.navigate(TakmirRoutes.Donation.route) },
                onNavigateToInventory = { navController.navigate(TakmirRoutes.Inventory.route) }
            )
        }

        // ── Finance (Kas) ────────────────────────────────────────────
        composable(TakmirRoutes.Transactions.route) {
            TransactionScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToForm = { id ->
                    navController.navigate(TakmirRoutes.TransactionForm.createRoute(id))
                }
            )
        }
        composable(
            route = "transaction-form?id={id}",
            arguments = listOf(navArgument("id") {
                type = NavType.StringType; nullable = true; defaultValue = null
            })
        ) { backStackEntry ->
            TransactionFormScreen(
                financeId = backStackEntry.arguments?.getString("id"),
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── Events ───────────────────────────────────────────────────
        composable(TakmirRoutes.Events.route) {
            EventScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToForm = { id ->
                    navController.navigate(TakmirRoutes.EventForm.createRoute(id))
                }
            )
        }
        composable(
            route = "event-form?id={id}",
            arguments = listOf(navArgument("id") {
                type = NavType.StringType; nullable = true; defaultValue = null
            })
        ) { backStackEntry ->
            EventFormScreen(
                eventId = backStackEntry.arguments?.getString("id"),
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── Donation ─────────────────────────────────────────────────
        composable(TakmirRoutes.Donation.route) {
            DonationScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToForm = { navController.navigate(TakmirRoutes.DonationForm.route) }
            )
        }
        composable(TakmirRoutes.DonationForm.route) {
            DonationFormScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── Inventory ────────────────────────────────────────────────
        composable(TakmirRoutes.Inventory.route) {
            InventoryScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToForm = { navController.navigate(TakmirRoutes.InventoryForm.route) }
            )
        }
        composable(TakmirRoutes.InventoryForm.route) {
            InventoryFormScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── Profile ──────────────────────────────────────────────────
        composable(TakmirRoutes.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(TakmirRoutes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
