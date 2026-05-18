package com.masjid.jemaah.feature.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masjid.core.domain.AppResult
import com.masjid.jemaah.domain.usecase.GetMasjidDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getMasjidDetailUseCase: GetMasjidDetailUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<DetailState>(DetailState.Loading)
    val state: StateFlow<DetailState> = _state.asStateFlow()

    fun handleIntent(intent: DetailIntent) {
        when (intent) {
            is DetailIntent.LoadDetail -> loadDetail(intent.masjidId)
        }
    }

    private fun loadDetail(id: String) {
        viewModelScope.launch {
            _state.value = DetailState.Loading
            when (val result = getMasjidDetailUseCase(id)) {
                is AppResult.Success -> {
                    _state.value = DetailState.Success(result.data)
                }
                is AppResult.Error -> {
                    _state.value = DetailState.Error(result.message)
                }
            }
        }
    }
}
