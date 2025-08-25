package com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.Instrument

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Holds the current instrument selection for the Instruments feature.
 *
 * State:
 * - Starts as [InstrumentsViewState.Loaded] with `selectedInstrument = null`.
 * - [selectInstrument] publishes a new selection.
 * - [resetState] clears the selection back to `null`.
 *
 * UI should collect [stateFlow] with lifecycle awareness.
 */
@HiltViewModel
class InstrumentsViewModel @Inject constructor(
) : ViewModel() {

    private val _stateFlow = MutableStateFlow<InstrumentsViewState>(InstrumentsViewState.Loaded(selectedInstrument = null))
    val stateFlow: StateFlow<InstrumentsViewState> = _stateFlow


    /**
     * Selects [instrument]. If it's already selected, does nothing to avoid redundant updates.
     */
    fun selectInstrument(instrument: Instrument) {
        viewModelScope.launch {
            _stateFlow.value = InstrumentsViewState.Loaded(
                selectedInstrument = instrument
            )
            Log.d("Instruments", _stateFlow.value.toString())
        }
    }

    fun resetState() {
        viewModelScope.launch {
            _stateFlow.value = InstrumentsViewState.Loaded (selectedInstrument = null)
           // _stateFlow.value = InstrumentsViewState.Idle
            Log.d("Instruments", _stateFlow.value.toString())
        }
    }
}