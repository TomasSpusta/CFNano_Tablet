package com.nano_tablet.nanotabletrfid.util

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * App-wide event bus.
 *
 * Uses SharedFlow (replay=0) so events are delivered to all active collectors,
 * are not replayed to new collectors, and won't crash if emitted off the main thread.
 */
object EventBus {
    private val _events = Channel<Any>()
    val events = _events.receiveAsFlow()

    suspend fun sendEvent(event: Any) {
        _events.send(event)
    }
}

sealed interface Event {
    data class Toast(val message: String) : Event
    data class Dialog(val title: String, val text: String) : Event

    /**
     * Confirm-style dialog. Callbacks run on the UI thread.
     * Keep callbacks lightweight (ideally just trigger VM methods).
     */
    data class AlertDialog(
        val title: String,
        val message: String,
        val onOkClicked: (() -> Unit)? = null,
        val onDismiss: (() -> Unit)? = null
    ) : Event
}