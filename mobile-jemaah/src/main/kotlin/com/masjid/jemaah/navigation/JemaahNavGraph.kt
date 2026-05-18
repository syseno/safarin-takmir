package com.masjid.jemaah.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.masjid.jemaah.feature.auth.LoginScreen
import com.masjid.jemaah.feature.auth.RegisterScreen
import com.masjid.jemaah.feature.detail.DetailScreen
import com.masjid.jemaah.feature.event.EventScreen
import com.masjid.jemaah.feature.kas.KasScreen
import com.masjid.jemaah.feature.search.SearchScreen

@Composable
fun JemaahNavGraph(
    navController: NavHostController,
    isLoggedIn: Boolean
) {
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) JemaahRoutes.Search.route else JemaahRoutes.Login.route
    ) {
        // ── Auth ──────────────────────────────────────────────────────
        composable(JemaahRoutes.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(JemaahRoutes.Search.route) {
                        popUpTo(JemaahRoutes.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(JemaahRoutes.Register.route)
                }
            )
        }

        composable(JemaahRoutes.Register.route) {
            RegisterScreen(
                onNavigateBack = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(JemaahRoutes.Search.route) {
                        popUpTo(JemaahRoutes.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Main ──────────────────────────────────────────────────────
        composable(JemaahRoutes.Search.route) {
            SearchScreen(
                onNavigateToDetail = { id ->
                    navController.navigate(JemaahRoutes.Detail.createRoute(id))
                },
                onNavigateToProfile = {
                    // Profile placeholder — navigates to login (logout)
                    navController.navigate(JemaahRoutes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = JemaahRoutes.Detail.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            DetailScreen(
                masjidId = id,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToKas = { masjidId ->
                    navController.navigate(JemaahRoutes.Kas.createRoute(masjidId))
                },
                onNavigateToEvents = { masjidId ->
                    navController.navigate(JemaahRoutes.Events.createRoute(masjidId))
                }
            )
        }

        composable(
            route = JemaahRoutes.Kas.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            KasScreen(
                masjidId = id,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = JemaahRoutes.Events.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            EventScreen(
                masjidId = id,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
