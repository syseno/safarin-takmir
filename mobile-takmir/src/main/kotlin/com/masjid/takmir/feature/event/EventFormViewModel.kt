package com.masjid.takmir.feature.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masjid.core.domain.AppResult
import com.masjid.core.domain.CreateEventRequest
import com.masjid.core.domain.MasjidEvent
import com.masjid.core.domain.UpdateEventRequest
import com.masjid.core.mvi.BaseState
import com.masjid.takmir.data.repository.EventRepository
import com.masjid.takmir.domain.usecase.CreateEventUseCase
import com.masjid.takmir.domain.usecase.UpdateEventUseCase
import com.masjid.takmir.security.EncryptedTokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class EventFormState : BaseState {
    object Idle : EventFormState()
    object Loading : EventFormState()
    data class Editing(val event: MasjidEvent) : EventFormState()
    object Saving : EventFormState()
    object Success : EventFormState()
    data class Error(val message: String) : EventFormState()
}

@HiltViewModel
class EventFormViewModel @Inject constructor(
    private val createEventUseCase: CreateEventUseCase,
    private val updateEventUseCase: UpdateEventUseCase,
    private val eventRepository: EventRepository,
    private val tokenManager: EncryptedTokenManager
) : ViewModel() {

    private val _state = MutableStateFlow<EventFormState>(EventFormState.Idle)
    val state: StateFlow<EventFormState> = _state.asStateFlow()

    fun initForm(eventId: String?) {
        if (eventId == null) {
            _state.value = EventFormState.Idle
            return
        }

        viewModelScope.launch {
            _state.value = EventFormState.Loading
            val masjidId = tokenManager.getMasjidId() ?: return@launch
            
            when (val result = eventRepository.getEvents(masjidId)) {
                is AppResult.Success -> {
                    val ev = result.data.find { it.id == eventId }
                    if (ev != null) {
                        _state.value = EventFormState.Editing(ev)
                    } else {
                        _state.value = EventFormState.Error("Event tidak ditemukan")
                    }
                }
                is AppResult.Error -> {
                    _state.value = EventFormState.Error(result.message)
                }
            }
        }
    }

    fun saveEvent(
        eventId: String?,
        title: String,
        description: String,
        date: String,
        startTime: String,
        endTime: String,
        location: String
    ) {
        viewModelScope.launch {
            _state.value = EventFormState.Saving
            val masjidId = tokenManager.getMasjidId() ?: run {
                _state.value = EventFormState.Error("Masjid ID tidak ditemukan")
                return@launch
            }

            val loc = location.ifBlank { null }

            val result = if (eventId == null) {
                createEventUseCase(masjidId, CreateEventRequest(title, description, date, startTime, endTime, loc))
            } else {
                updateEventUseCase(masjidId, eventId, UpdateEventRequest(title, description, date, startTime, endTime, loc))
            }

            when (result) {
                is AppResult.Success -> _state.value = EventFormState.Success
                is AppResult.Error -> _state.value = EventFormState.Error(result.message)
            }
        }
    }
}
