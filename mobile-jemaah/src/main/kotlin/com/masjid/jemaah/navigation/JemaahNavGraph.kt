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
import com.masjid.jemaah.feature.home.HomeScreen
import com.masjid.jemaah.feature.home.CityMasjidsScreen
import com.masjid.jemaah.feature.profile.ProfileScreen
import com.masjid.jemaah.feature.kiblat.KiblatScreen

@Composable
fun JemaahNavGraph(
    navController: NavHostController,
    isLoggedIn: Boolean
) {
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) JemaahRoutes.Home.route else JemaahRoutes.Login.route
    ) {
        // ── Auth ──────────────────────────────────────────────────────
        composable(JemaahRoutes.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(JemaahRoutes.Home.route) {
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
                    navController.navigate(JemaahRoutes.Home.route) {
                        popUpTo(JemaahRoutes.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Main ──────────────────────────────────────────────────────
        composable(JemaahRoutes.Home.route) {
            HomeScreen(
                onNavigateToDetail = { id ->
                    navController.navigate(JemaahRoutes.Detail.createRoute(id))
                },
                onNavigateToProfile = {
                    navController.navigate(JemaahRoutes.Profile.route)
                },
                onNavigateToKiblat = {
                    navController.navigate(JemaahRoutes.Kiblat.route)
                },
                onNavigateToCityMasjids = { cityId ->
                    navController.navigate(JemaahRoutes.CityMasjids.createRoute(cityId))
                }
            )
        }

        composable(
            route = JemaahRoutes.CityMasjids.route,
            arguments = listOf(navArgument("cityId") { type = NavType.StringType })
        ) { backStackEntry ->
            val cityId = backStackEntry.arguments?.getString("cityId") ?: ""
            CityMasjidsScreen(
                cityId = cityId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { id ->
                    navController.navigate(JemaahRoutes.Detail.createRoute(id))
                }
            )
        }

        composable(JemaahRoutes.Kiblat.route) {
            KiblatScreen(
                onNavigateBack = { navController.popBackStack() }
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

        composable(JemaahRoutes.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(JemaahRoutes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
