package com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.Contact
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.Instrument
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.Operation
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.Project
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.Sample
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * App-wide ephemeral UI state holder for selections used across screens.
 *
 * Starts with a Saved state and exposes a single [StateFlow] that screens can collect.
 * Use [updateState] for bulk updates, or the smaller helpers for focused changes.
 *
 * Notes:
 * - All updates are atomic via `MutableStateFlow.update`.
 * - Avoid long-lived business data here; this is for screen/session selections.
 */
@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {
    private val _sharedState = MutableStateFlow<SharedViewState>(
        value = SharedViewState.Saved(
            user = null,
            contact = null,
            selectedTime = 15,
            selectedInstrument = null,
            selectedProject = null,
            selectedSample = null,
            selectedPairs = emptyMap(),
            selectedPairsId = emptyMap(),
            otherField = null,
            selectedOperation = null,
            startTime = null,
            endTime = null,
            defaultProject = null,
            reservationName = null


        )
    )
    val sharedState = _sharedState.asStateFlow()
    private val tag = "SharedState"

    /**
     * Bulk update for the shared state. Any non-null parameter replaces the current value.
     * Null parameters leave the existing value unchanged.
     */
    fun updateState(
        user: User? = null,
        contact: Contact? = null,
        selectedTime: Int? = null,
        selectedInstrument: Instrument? = null,
        selectedProject: Project? = null,
        selectedSample: Sample? = null,
        selectedPairs: Map<String, String>? = null,
        selectedPairsId: Map<String, String>? = null,
        otherField: String? = null,
        selectedOperation: Operation? = null,
        startTime: String? = null,
        endTime: String? = null,
        defaultProject: Project? = null,
        reservationName: String? = null


    ) = viewModelScope.launch {
        _sharedState.update { currentState ->
            when (currentState) {
                is SharedViewState.Saved -> {
                    SharedViewState.Saved(
                        user = user ?: currentState.user,
                        contact = contact ?: currentState.contact,
                        selectedTime = selectedTime ?: currentState.selectedTime,
                        selectedInstrument = selectedInstrument ?: currentState.selectedInstrument,
                        selectedProject = selectedProject ?: currentState.selectedProject,
                        selectedSample = selectedSample ?: currentState.selectedSample,
                        selectedPairs = selectedPairs ?: currentState.selectedPairs,
                        selectedPairsId = selectedPairsId ?: currentState.selectedPairsId,
                        otherField = otherField ?: currentState.otherField,
                        selectedOperation = selectedOperation ?: currentState.selectedOperation,
                        startTime = startTime ?: currentState.startTime,
                        endTime = endTime ?: currentState.endTime,
                        defaultProject = defaultProject ?: currentState.defaultProject,
                        reservationName = reservationName ?: currentState.reservationName
                        )
                }

                else -> currentState
            }
        }
    }
    /** Resets selection-related fields while keeping user/contact/time/default project. */
    fun resetSharedState() {
        _sharedState.update { currentState ->
            when (currentState) {
                is SharedViewState.Saved -> {
                    //Log.i("SharedStateViewModel", currentState.user.toString())
                    SharedViewState.Saved(
                        user = currentState.user,
                        contact = currentState.contact,
                        selectedTime = currentState.selectedTime,
                        selectedInstrument = null,
                        //selectedProject = null,
                        selectedProject = currentState.selectedProject,
                        selectedSample = null,
                        selectedPairs = emptyMap(),
                        selectedPairsId = emptyMap(),
                        otherField = null,
                        selectedOperation = null,
                        startTime = null,
                        endTime = null,
                        defaultProject = currentState.defaultProject,
                        reservationName = null,
                    )
                }

                else -> currentState
            }
        }
    }


    /**
     * Updates reservation name.
     */
    fun updateReservationName(reservationName: String) {
        _sharedState.update { currentState ->
            when (currentState) {

                is SharedViewState.Saved -> {
                    Log.i(tag, "Reservation name - function:${currentState.reservationName}")
                    currentState.copy(reservationName = reservationName)}
                else -> currentState
            }
        }
    }

    /**
     * Updates the shared state with a new selected operation detail pair.
     */
    fun updateSelectedOperationDetails(operationId: String, operationDetail: String) {
        _sharedState.update { currentState ->
            when (currentState) {
                is SharedViewState.Saved -> {
                    val updatedPairsId = currentState.selectedPairsId.toMutableMap().apply {
                        put(operationId, operationDetail) // Update operation-detail pair
                    }

                    Log.i(tag, "Pairs - function:${updatedPairsId}")
                    currentState.copy(selectedPairsId = updatedPairsId)
                }
                else -> currentState
            }
        }
    }

    fun updateSelectedPairsId(selectedPairsId: Map<String, String>) {
        _sharedState.update { currentState ->
            when (currentState) {
                is SharedViewState.Saved -> {
                    val updatedState = currentState.copy(selectedPairsId = selectedPairsId)
                    Log.d("SharedStateViewModel", "Updated selectedPairsId: $selectedPairsId")
                    updatedState
                }
                else -> currentState
            }
        }
    }







}
