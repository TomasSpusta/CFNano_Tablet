package com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.ReservationRequestModel
import com.nano_tablet.nanotabletrfid.mainApp.domain.repository.ReservationRepository
import com.nano_tablet.nanotabletrfid.util.Event
import com.nano_tablet.nanotabletrfid.util.EventBus.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Creates reservations and exposes a simple state machine via [PrepareReservationState].
 *
 * Lifecycle:
 * - Starts in [PrepareReservationState.Idle]
 * - [prepareReservation] -> emits [Loading] then [Success] / [Error]
 * - [resetReservationState] returns to [Idle]
 *
 * UI pattern:
 * - Collect [stateFlow] with lifecycle awareness.
 * - On [Success], navigate / reset other VMs as needed (as you already do).
 */
@HiltViewModel
class PrepareReservationViewModel @Inject constructor(
    private val reservationRepository: ReservationRepository,

    ) : ViewModel() {
    private val _reservationStateFlow =
        MutableStateFlow<PrepareReservationState>(PrepareReservationState.Idle)
    val stateFlow: StateFlow<PrepareReservationState> = _reservationStateFlow.asStateFlow()

    private val tag = "Reservation"
    /**
     * Builds and submits a reservation request.
     *
     * @param equipment      list of instrument ids (at least one required)
     * @param description    human-friendly description (optional)
     * @param fields         map<instrumentId, map<operationId, value>>
     * @param from           ISO-8601 start time (e.g., "2025-08-25T09:00:00Z")
     * @param project        project id (required)
     * @param realisedFor    contact/person id
     * @param researchGroup  research group id
     * @param samples        optional sample ids
     * @param statusCode     backend status code (e.g., 4)
     * @param timeRequirement backend-specific int field
     * @param to             ISO-8601 end time
     */
    fun prepareReservation(
        equipment: List<String>,
        description: String,
        fields: Map<String, Map<String, Any>>,
        from: String,
        project: String,
        realised_for: String,
        research_group: String,
        samples: List<String>? = emptyList(),
        status_code: Int,
        time_requirement: Int,
        to: String
    ) =
        viewModelScope.launch {
            _reservationStateFlow.update { return@update PrepareReservationState.Loading }
            val request = ReservationRequestModel(
                from = from,
                to = to,
                equipment = equipment,
                fields = fields,
                description = description,
                project = project,
                realised_for = realised_for,
                research_group = research_group,
                samples = samples,
                status_code = status_code,
                time_requirement = time_requirement
            )
            reservationRepository.makeReservation(request).onRight { response ->

                _reservationStateFlow.update {
                    return@update PrepareReservationState.Success(response)
                }
                sendEvent(
                    Event.AlertDialog(
                        "Reservation Created",
                        "Reservation created successfully."
                    ) { _reservationStateFlow.update { return@update PrepareReservationState.Idle } }
                )


            }.onLeft { error ->
                _reservationStateFlow.update { return@update PrepareReservationState.Error(error.error.message) }
                sendEvent(
                    Event.AlertDialog(
                        "$tag Error",
                        "${error.error.code.name}\n${error.error.message}"
                    )
                )
                Log.e("$tag Error", error.toString())
            }
        }

    fun resetReservationState() {
        _reservationStateFlow.value = PrepareReservationState.Idle
    }
}