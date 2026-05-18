package com.masjid.jemaah.feature.kas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masjid.core.domain.AppResult
import com.masjid.core.domain.Finance
import com.masjid.core.domain.PublicFinanceSummary
import com.masjid.jemaah.domain.usecase.GetKasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KasViewModel @Inject constructor(
    private val getKasUseCase: GetKasUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<KasState>(KasState.Loading)
    val state: StateFlow<KasState> = _state.asStateFlow()

    private var currentMasjidId: String = ""
    private var currentPage = 1
    private val allTransactions = mutableListOf<Finance>()

    fun handleIntent(intent: KasIntent) {
        when (intent) {
            is KasIntent.LoadKas -> {
                currentMasjidId = intent.masjidId
                currentPage = 1
                allTransactions.clear()
                loadKas(intent.masjidId, page = 1)
            }
            is KasIntent.LoadMore -> {
                loadKas(intent.masjidId, page = currentPage + 1)
            }
        }
    }

    private fun loadKas(masjidId: String, page: Int) {
        viewModelScope.launch {
            if (page == 1) _state.value = KasState.Loading
            when (val result = getKasUseCase(masjidId, page)) {
                is AppResult.Success -> {
                    val response = result.data
                    if (page == 1) allTransactions.clear()
                    allTransactions.addAll(response.records)
                    currentPage = response.pagination.page
                    val hasMore = response.pagination.page < response.pagination.totalPages
                    _state.value = KasState.Success(
                        transactions = allTransactions.toList(),
                        summary = response.summary,
                        currentPage = currentPage,
                        hasMore = hasMore
                    )
                }
                is AppResult.Error -> {
                    _state.value = KasState.Error(result.message)
                }
            }
        }
    }
}
