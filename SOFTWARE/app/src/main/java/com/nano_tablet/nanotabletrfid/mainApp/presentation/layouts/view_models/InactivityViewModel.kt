package com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nano_tablet.nanotabletrfid.util.Event
import com.nano_tablet.nanotabletrfid.util.EventBus.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InactivityViewModel @Inject constructor() : ViewModel() {
    /**
     * Emits `true` when the user has been inactive for [inactivityTimeoutMs].
     * Collect with lifecycle awareness to trigger a logout/navigation.
     */
    private val _isInactive = MutableStateFlow(false)
    val isInactive = _isInactive.asStateFlow()

    private val inactivityTimeout = 10 * 60 * 1000L // 10 minutes
    private var inactivityJob: Job? = null // Job to hold the timer coroutine

    init {
        resetTimer()
    }

    /**
     * Resets the inactivity timer and marks the user as active.
     *
     * Cancels any previously running timer and starts a new one.
     * When the timeout elapses, sets [_isInactive] to `true` and emits an alert.
     */
    fun resetTimer() {//delete this
        _isInactive.value = false
        inactivityJob?.cancel() // cancel any previous timer
        Log.i("inactivityTimeout", "Timer reset")
        inactivityJob = viewModelScope.launch {
            delay(inactivityTimeout)
            _isInactive.value = true

            sendEvent(Event.AlertDialog(
                "User inactivity",
                "User inactive for 10 minutes. User logged off."
            ))
        }
    }
    /**
     * Stops the inactivity timer and marks the user as active.
     * Use when leaving the screen or pausing tracking.
     */
    fun stopTimer() {
        inactivityJob?.cancel()
        inactivityJob = null
        _isInactive.value = false
        Log.i("inactivityTimeout", "Timer stopped")
    }
}