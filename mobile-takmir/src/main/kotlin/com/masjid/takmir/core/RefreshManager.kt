package com.masjid.takmir.core

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class RefreshType {
    FINANCE, EVENT, DONATION, INVENTORY, PROFILE, DASHBOARD
}

@Singleton
class RefreshManager @Inject constructor() {
    private val _refreshEvent = MutableSharedFlow<RefreshType>(extraBufferCapacity = 1)
    val refreshEvent = _refreshEvent.asSharedFlow()

    fun triggerRefresh(type: RefreshType) {
        _refreshEvent.tryEmit(type)
    }
}
