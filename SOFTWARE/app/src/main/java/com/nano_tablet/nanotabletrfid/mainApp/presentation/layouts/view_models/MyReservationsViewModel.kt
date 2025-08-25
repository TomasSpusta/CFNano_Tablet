package com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.Reservation
import com.nano_tablet.nanotabletrfid.mainApp.domain.repository.MyReservationsRepository
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
 * Exposes the user's reservations as a state machine via [MyReservationsState].
 *
 * Flow:
 * - Starts in [MyReservationsState.Idle]
 * - [fetchReservations] -> [Loading] -> [Success]/[Error]
 * - [onReservationClicked] selects a reservation and asks for confirmation to stop it
 * - [stopReservation] calls backend and shows a confirmation alert
 *
 * UI should collect [stateFlow] with lifecycle awareness.
 */
@HiltViewModel
class MyReservationsViewModel @Inject constructor(
    private val myReservationsRepository: MyReservationsRepository,
) : ViewModel() {

    private val _stateFlow = MutableStateFlow<MyReservationsState>(MyReservationsState.Idle)
    val stateFlow: StateFlow<MyReservationsState> = _stateFlow.asStateFlow()

    private val tag = "MyReservations"
    private var reservations: List<Reservation> = emptyList()
    /**
     * Loads reservations for the given [owner] within [start]..[end].
     * If a fetch is already in progress, ignores re-entrancy to avoid duplicate calls.
     */
    fun fetchReservations(owner: String, start: String, end: String) = viewModelScope.launch {
        _stateFlow.update { return@update MyReservationsState.Loading }
        myReservationsRepository.fetchReservations(owner, start, end)
            .onRight { reservationSchemas ->
                reservations = reservationSchemas.map { reservationSchema ->
                    Reservation(
                        name = reservationSchema.subject,
                        instrument = reservationSchema.activity_parties[0].partyid.name,
                        id = reservationSchema.id,
                        start = reservationSchema.scheduledstart,
                        end = reservationSchema.scheduledend
                    )
                }

                // Log.i(tag, reservations.toString())
                _stateFlow.update {
                    return@update MyReservationsState.Success(
                        reservations = reservations,
                        selectedReservation = null,
                        selectedReservationIndex = -1
                    )
                }

            }.onLeft { error ->
                _stateFlow.update {
                    return@update MyReservationsState.Error(error = error.error.message)
                }
                sendEvent(Event.AlertDialog("$tag Error", "${error.error.code.name}\n${error.error.message}"))
                //sendEvent(Event.Toast(error.toString()))
                Log.e("$tag Fetch Error", error.toString())
                //Log.e("SamplesError", error.error.toString())
            }
    }


    /**
     * Handles a tap on a reservation:
     * - updates the selected reservation in state
     * - shows a confirmation dialog with a callback to [stopReservation].
     */
    fun onReservationClicked(reservation: Reservation) = viewModelScope.launch {
        _stateFlow.update { currentState ->
            when (currentState) {
                is MyReservationsState.Success -> {
                    sendEvent(
                        Event.AlertDialog(
                            "Stop Reservation",
                            //"CurrentReservation ${currentState.selectedReservation?.name} was picked at ${currentState.selectedReservationIndex} do you want to stop it?\n" +
                            "Reservation ${reservation.id} started at ${reservation.start} will be stopped, OK?"
                        ){stopReservation(reservationId = reservation.id)}
                    )
                    MyReservationsState.Success(
                        reservations = currentState.reservations,
                        selectedReservation = reservation,
                        selectedReservationIndex = currentState.reservations.indexOf(reservation)
                    )
                }

                else -> {
                    currentState
                }
            }
        }
    }


    /**
     * Requests the backend to stop a reservation by [reservationId].
     * Emits [Loading] during the call, then resets to [Idle] and shows a confirmation dialog.
     * (Consider reloading the list afterwards to reflect the new state.)
     */
    fun updateSelectedReservationIndex(index: Int) = viewModelScope.launch {
        _stateFlow.update { currentState ->
            when (currentState) {
                is MyReservationsState.Success -> {
                    MyReservationsState.Success(
                        reservations = currentState.reservations,
                        selectedReservation = currentState.selectedReservation,
                        selectedReservationIndex = index
                    )
                }

                else -> {
                    currentState
                }
            }
        }
    }

    private fun stopReservation(reservationId: String) = viewModelScope.launch {
        _stateFlow.update { MyReservationsState.Loading }
        //val reservationId = "Reservation GUID"

        myReservationsRepository.stopReservation(reservationId).onRight { response ->
            _stateFlow.update { MyReservationsState.Idle }
          //  _stateFlow.update { MyReservationsState.Success }
            sendEvent(
                Event.AlertDialog(
                    "Stop Reservation",
                    "Reservation stopped. Please refresh My reservations page"
                )
            )
        }.onLeft { error ->
            _stateFlow.update {  MyReservationsState.Error(error.error.message) }
            sendEvent(Event.AlertDialog("$tag Stop Error", "${error.error}\n${error.t?.message}"))
            Log.e("$tag Error", error.toString())
        }
    }

}

