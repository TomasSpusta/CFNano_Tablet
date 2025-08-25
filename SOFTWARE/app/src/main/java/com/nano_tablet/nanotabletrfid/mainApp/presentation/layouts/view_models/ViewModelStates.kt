package com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models


import com.nano_tablet.nanotabletrfid.mainApp.domain.models.Contact
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.Instrument
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.Operation
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.Project
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.Reservation
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.ReservationResponse
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.Sample
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.Token
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.User

import retrofit2.Response
/**
 * UI state for operations (per instrument).
 *
 * Typical flow:
 *  - Idle → Loading → Success | Error
 */

sealed interface OperationsViewState {
    object Idle : OperationsViewState
    object Loading : OperationsViewState
    data class Success(
        val fetchedOperations: List<Operation>,
        val selectedOperation: Operation?,
        val selectedOperationDetailsMap: Map<String, List<String>> = emptyMap(),
        val selectedPairs: Map<String, String>,
        val selectedPairsId: Map<String, String>,

        val otherField: String?,

        ) : OperationsViewState

    data class Error(val error: String) : OperationsViewState
}

sealed interface UserViewState {
    object Idle : UserViewState
    object Loading : UserViewState
    data class Success(
        val user: User,
    ) : UserViewState

    data class Error(val error: String) : UserViewState
}

sealed interface SharedViewState {
    object Idle : SharedViewState
    object Loading : SharedViewState
    data class Saved(
        val user: User?,
        val contact: Contact?,
        val selectedTime: Int = 15,
        val selectedInstrument: Instrument?,
        val selectedProject: Project?,
        val selectedSample: Sample?,
        val selectedPairs: Map<String, String>,
        val selectedPairsId: Map<String, String>,

        val otherField: String?,
        val selectedOperation: Operation?,

        val startTime: String?,
        val endTime: String?,

        val defaultProject: Project?,

        val reservationName:String?

    ) : SharedViewState

    data class Error(val error: String) : SharedViewState
}


sealed interface ProjectsViewState {
    object Idle : ProjectsViewState
    object Loading : ProjectsViewState
    data class Success(
        val projects: List<Project>,
        val defaultProject: Project?,
        val selectedProject: Project?,
    ) : ProjectsViewState

    data class Error(val error: String) : ProjectsViewState
}

sealed interface DefaultProjectViewState {
    object Idle : DefaultProjectViewState
    object Loading : DefaultProjectViewState
    data class Success(
        val defaultProject: Project?,
    ) : DefaultProjectViewState

    data class Error(val error: String) : DefaultProjectViewState
}


sealed interface InstrumentsViewState {
    data class Loaded(
        val selectedInstrument: Instrument?
    ) : InstrumentsViewState
}


sealed interface TimeViewState {
    data class Set(
        val selectedTime: Int = 15,
        val startTime: String?,
        val endTime: String?
    ) : TimeViewState

}

sealed interface SamplesViewState {
    object Idle : SamplesViewState
    object Loading : SamplesViewState
    data class Success(
        val samples: List<Sample>,
        val selectedSample: Sample?,
        //val selectedSampleIndex: Int = -1
    ) : SamplesViewState

    data class Error(val error: String) : SamplesViewState
}


sealed interface TokenViewState {
    object Idle : TokenViewState
    object Loading : TokenViewState
    data class Success(
        val token: Token?,
        val isValid: Boolean
    ) : TokenViewState

    data class Error(val error: String) : TokenViewState
}


sealed interface PrepareReservationState {
    object Idle : PrepareReservationState
    object Loading : PrepareReservationState
    data class Success(
        val reservationResponse: ReservationResponse,
    ) : PrepareReservationState

    data class Error(val error: String) : PrepareReservationState
}

sealed interface MyReservationsState {
    data object Idle : MyReservationsState
    data object Loading : MyReservationsState
    data class Success(
        val reservations: List<Reservation>,
        val selectedReservation: Reservation?,
        val selectedReservationIndex: Int = -1
    ) : MyReservationsState

    data object Erased : MyReservationsState
    data class Error(val error: String) : MyReservationsState
}

sealed interface LogState {
    object Idle : LogState
    object Loading : LogState
    data class Success(
        val logResponse: Response<Any>,
    ) : LogState

    data class Error(val error: String) : LogState
}

sealed interface ContactViewState {
    object Idle : ContactViewState
    object Loading : ContactViewState
    data class Success(
        val contact: Contact
    ) : ContactViewState

    data class Error(val error: String) : ContactViewState
}