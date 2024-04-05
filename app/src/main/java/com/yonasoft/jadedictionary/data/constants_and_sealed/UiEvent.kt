package com.yonasoft.jadedictionary.data.constants_and_sealed

import android.widget.Toast
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed class UiEvent {
    data class ShowToast(val message: String, val duration: Int = Toast.LENGTH_SHORT) : UiEvent()
}

private val _uiEvent = MutableSharedFlow<UiEvent>(extraBufferCapacity = 1)
val uiEvent = _uiEvent.asSharedFlow()

// Function to emit events
suspend fun emitUiEvent(event: UiEvent) {
    _uiEvent.emit(event)
}
