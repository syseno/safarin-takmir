package com.masjid.jemaah.feature.detail

import com.masjid.core.domain.Masjid
import com.masjid.core.mvi.BaseIntent
import com.masjid.core.mvi.BaseState

sealed class DetailIntent : BaseIntent {
    data class LoadDetail(val masjidId: String) : DetailIntent()
}

sealed class DetailState : BaseState {
    object Loading : DetailState()
    data class Success(val masjid: Masjid) : DetailState()
    data class Error(val message: String) : DetailState()
}
