package com.masjid.jemaah.feature.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masjid.core.domain.AppResult
import com.masjid.jemaah.domain.usecase.GetEventsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val getEventsUseCase: GetEventsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<EventState>(EventState.Loading)
    val state: StateFlow<EventState> = _state.asStateFlow()

    fun handleIntent(intent: EventIntent) {
        when (intent) {
            is EventIntent.LoadEvents -> loadEvents(intent.masjidId)
        }
    }

    private fun loadEvents(masjidId: String) {
        viewModelScope.launch {
            _state.value = EventState.Loading
            when (val result = getEventsUseCase(masjidId)) {
                is AppResult.Success -> {
                    _state.value = EventState.Success(result.data)
                }
                is AppResult.Error -> {
                    _state.value = EventState.Error(result.message)
                }
            }
        }
    }
}
