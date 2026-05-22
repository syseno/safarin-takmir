package com.masjid.takmir.feature.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masjid.core.domain.AppResult
import com.masjid.takmir.domain.usecase.DeleteEventUseCase
import com.masjid.takmir.domain.usecase.GetEventsUseCase
import com.masjid.takmir.security.EncryptedTokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val getEventsUseCase: GetEventsUseCase,
    private val deleteEventUseCase: DeleteEventUseCase,
    private val tokenManager: EncryptedTokenManager
) : ViewModel() {

    private val _state = MutableStateFlow<EventState>(EventState.Loading)
    val state: StateFlow<EventState> = _state.asStateFlow()

    init {
        fetchEvents()
    }

    fun handleIntent(intent: EventIntent) {
        when (intent) {
            is EventIntent.LoadEvents -> fetchEvents()
            is EventIntent.DeleteEvent -> deleteEvent(intent.eventId, intent.deleteType)
            is EventIntent.Refresh -> fetchEvents()
        }
    }

    private fun fetchEvents() {
        viewModelScope.launch {
            _state.value = EventState.Loading
            val masjidId = tokenManager.getMasjidId() ?: run {
                _state.value = EventState.Error("Masjid ID tidak ditemukan")
                return@launch
            }
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

    private fun deleteEvent(eventId: String, deleteType: String) {
        viewModelScope.launch {
            val masjidId = tokenManager.getMasjidId() ?: return@launch
            when (val result = deleteEventUseCase(masjidId, eventId, deleteType)) {
                is AppResult.Success -> {
                    fetchEvents() // Refresh list
                }
                is AppResult.Error -> {
                    fetchEvents()
                }
            }
        }
    }
}
