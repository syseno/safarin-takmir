package com.masjid.jemaah.feature.search

import com.masjid.core.domain.Masjid
import com.masjid.core.mvi.BaseIntent
import com.masjid.core.mvi.BaseState

sealed class SearchIntent : BaseIntent {
    data class SubmitSearch(val query: String) : SearchIntent()
    object LoadInitial : SearchIntent()
}

sealed class SearchState : BaseState {
    object Idle : SearchState()
    object Loading : SearchState()
    data class Success(val masjids: List<Masjid>) : SearchState()
    data class Error(val message: String) : SearchState()
}
