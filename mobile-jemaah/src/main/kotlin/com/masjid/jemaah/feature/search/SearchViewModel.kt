package com.masjid.jemaah.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masjid.core.domain.AppResult
import com.masjid.jemaah.domain.usecase.SearchMasjidUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMasjidUseCase: SearchMasjidUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<SearchState>(SearchState.Idle)
    val state: StateFlow<SearchState> = _state.asStateFlow()

    init {
        handleIntent(SearchIntent.LoadInitial)
    }

    fun handleIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.SubmitSearch -> performSearch(intent.query)
            is SearchIntent.LoadInitial -> performSearch(null)
        }
    }

    private fun performSearch(query: String?) {
        viewModelScope.launch {
            _state.value = SearchState.Loading
            when (val result = searchMasjidUseCase(query)) {
                is AppResult.Success -> {
                    _state.value = SearchState.Success(result.data)
                }
                is AppResult.Error -> {
                    _state.value = SearchState.Error(result.message)
                }
            }
        }
    }
}
