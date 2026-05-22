package com.masjid.jemaah.feature.kas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masjid.core.domain.AppResult
import com.masjid.core.domain.Finance
import com.masjid.jemaah.domain.usecase.GetKasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KasViewModel @Inject constructor(
    private val getKasUseCase: GetKasUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(KasState())
    val state: StateFlow<KasState> = _state.asStateFlow()

    private var currentMasjidId: String = ""
    private var currentPage = 1
    private val allTransactions = mutableListOf<Finance>()

    fun handleIntent(intent: KasIntent) {
        when (intent) {
            is KasIntent.LoadKas -> {
                currentMasjidId = intent.masjidId
                loadKas(intent.masjidId, page = 1)
            }
            is KasIntent.LoadMore -> {
                loadKas(currentMasjidId, page = currentPage + 1)
            }
        }
    }

    private fun loadKas(masjidId: String, page: Int) {
        viewModelScope.launch {
            if (page == 1) {
                _state.update { it.copy(isLoading = true, error = null) }
            } else {
                _state.update { it.copy(isMoreLoading = true) }
            }
            
            when (val result = getKasUseCase(masjidId, page)) {
                is AppResult.Success -> {
                    val response = result.data
                    if (page == 1) {
                        allTransactions.clear()
                        currentPage = 1
                    }
                    allTransactions.addAll(response.records)
                    currentPage = response.pagination.page
                    val hasMore = response.pagination.page < response.pagination.totalPages
                    _state.update { it.copy(
                        isLoading = false,
                        isMoreLoading = false,
                        transactions = allTransactions.toList(),
                        summary = response.summary,
                        currentPage = currentPage,
                        hasMore = hasMore
                    ) }
                }
                is AppResult.Error -> {
                    _state.update { it.copy(
                        isLoading = false, 
                        isMoreLoading = false,
                        error = result.message
                    ) }
                }
            }
        }
    }
}
