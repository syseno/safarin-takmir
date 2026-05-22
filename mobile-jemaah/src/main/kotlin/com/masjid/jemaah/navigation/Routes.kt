package com.masjid.jemaah.navigation

sealed class JemaahRoutes(val route: String) {
    object Login : JemaahRoutes("login")
    object Register : JemaahRoutes("register")
    object Home : JemaahRoutes("home")
    object Detail : JemaahRoutes("detail/{id}") {
        fun createRoute(id: String) = "detail/$id"
    }
    object Kas : JemaahRoutes("kas/{id}") {
        fun createRoute(id: String) = "kas/$id"
    }
    object Events : JemaahRoutes("events/{id}") {
        fun createRoute(id: String) = "events/$id"
    }
    object Profile : JemaahRoutes("profile")
    object Kiblat : JemaahRoutes("kiblat")
    object CityMasjids : JemaahRoutes("city_masjids/{cityId}") {
        fun createRoute(cityId: String) = "city_masjids/$cityId"
    }
}
