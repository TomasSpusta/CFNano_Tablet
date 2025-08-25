package com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nano_tablet.nanotabletrfid.di.TabletLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Coordinates device telemetry:
 * - Starts a periodic heartbeat (via [TabletLogger.startHeartbeatLoop]) on init.
 * - Provides one-shot reporting on first screen entry ([reportOnce]).
 * - Allows a manual "force" report ([forceReport]) for debugging.
 *
 * Notes:
 * - Current [TabletLogger] launches its own IO coroutines; these calls return immediately.
 * - Consider converting logger methods to `suspend` and calling them from [viewModelScope]
 *   for structured concurrency (see suggestions below).
 */
@HiltViewModel
class DeviceReporterViewModel @Inject constructor(
    private val deviceReporter: TabletLogger

) : ViewModel() {

    private val _hasReported = MutableStateFlow(false)
    //val hasReported: StateFlow<Boolean> = _hasReported

    init {
        deviceReporter.startHeartbeatLoop()
    }

    fun reportOnce() {
        if (_hasReported.value) return

        _hasReported.value = true
        deviceReporter.reportToGoogleSheet("Initial log", "Initial log after app start")
    }

    fun forceReport() {
        deviceReporter.reportToGoogleSheet("Forced log", "Forced log via button")
        deviceReporter.sendHeartbeat()

    }
}