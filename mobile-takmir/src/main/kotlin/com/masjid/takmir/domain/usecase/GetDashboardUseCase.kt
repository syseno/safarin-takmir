package com.masjid.takmir.domain.usecase

import com.masjid.core.domain.AppResult
import com.masjid.core.domain.DashboardData
import com.masjid.core.network.TakmirApiClient
import javax.inject.Inject

class GetDashboardUseCase @Inject constructor(
    private val apiClient: TakmirApiClient
) {
    suspend operator fun invoke(masjidId: String): AppResult<DashboardData> {
        return AppResult.runCatching {
            apiClient.getDashboard(masjidId)
        }
    }
}
