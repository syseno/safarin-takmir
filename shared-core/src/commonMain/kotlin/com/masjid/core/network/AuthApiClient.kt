package com.masjid.core.network

import com.masjid.core.domain.ApiResponse
import com.masjid.core.domain.LoginRequest
import com.masjid.core.domain.LoginResponse
import com.masjid.core.domain.RegisterRequest
import com.masjid.core.domain.User
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

/**
 * API client for authentication endpoints.
 * Used by both Takmir and Jemaah apps.
 */
class AuthApiClient(private val client: HttpClient) {

    suspend fun login(request: LoginRequest): LoginResponse {
        val response = client.post("/api/auth/login") {
            setBody(request)
        }
        val result: ApiResponse<LoginResponse> = response.body()
        return result.data ?: throw Exception(result.message)
    }

    suspend fun register(request: RegisterRequest): LoginResponse {
        val response = client.post("/api/auth/register") {
            setBody(request)
        }
        val result: ApiResponse<LoginResponse> = response.body()
        return result.data ?: throw Exception(result.message)
    }

    /** GET /api/auth/me — requires Bearer token in header */
    suspend fun getProfile(): User {
        val response = client.get("/api/auth/me")
        val result: ApiResponse<User> = response.body()
        return result.data ?: throw Exception(result.message)
    }
}
