package com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.screens

import android.util.Log
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.navigation.Screen
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.PrepareReservationViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.InactivityViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.InstrumentsViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.OperationsViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.PrepareReservationState
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.SharedViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.SharedViewState
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.TimeViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.util.components.ButtonNormal
import com.nano_tablet.nanotabletrfid.mainApp.presentation.util.components.LoadingDialog
import com.nano_tablet.nanotabletrfid.ui.theme.Reservation
import com.nano_tablet.nanotabletrfid.util.UiConstants.BUTTON_HEIGHT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Screen that prepares and submits a reservation for the current selection.
 *
 * Observes [PrepareReservationViewModel.stateFlow] and:
 * - Shows a loading dialog while preparing.
 * - On success: resets shared/viewmodel states and navigates back to Instruments.
 */
@Composable
internal fun PrepareReservationScreen(
    navController: NavController,
    sharedState: SharedViewState.Saved,
    prepareReservationViewModel: PrepareReservationViewModel,
    inactivityViewModel: InactivityViewModel,
    sharedViewModel: SharedViewModel,
    instrumentsViewModel: InstrumentsViewModel,
    operationsViewModel: OperationsViewModel,
    timeViewModel: TimeViewModel
) {

    val reservationState by prepareReservationViewModel.stateFlow.collectAsStateWithLifecycle()
    // React to reservation completion: reset state and navigate away
    LaunchedEffect(reservationState) {
        if (reservationState is PrepareReservationState.Success) {
            sharedViewModel.resetSharedState()
            instrumentsViewModel.resetState()
            operationsViewModel.resetState()
            navController.navigate(Screen.Instruments.route)
            prepareReservationViewModel.resetReservationState()
        }
    }

    if (reservationState is PrepareReservationState.Loading) {
        LoadingDialog(isLoading = true, message = "Preparing reservation")
    }

    PrepareReservationScreenContent(
        reservationViewModel = prepareReservationViewModel,
        sharedState = sharedState,
        sharedViewModel = sharedViewModel,
        inactivityViewModel = inactivityViewModel,
        timeViewModel = timeViewModel

    )

}

/**
 * Mirrors selected operation pairs from [sharedState] into [sharedViewModel] (allowing empty),
 * then renders the "Make reservation" button.
 */
@Composable
fun PrepareReservationScreenContent(
    reservationViewModel: PrepareReservationViewModel,
    sharedState: SharedViewState.Saved,
    sharedViewModel: SharedViewModel,
    inactivityViewModel: InactivityViewModel,
    timeViewModel: TimeViewModel
) {
    LaunchedEffect(sharedState.selectedPairsId) {

        if (sharedState.selectedPairsId.isNotEmpty()) {
            Log.d(
                "ReservationScreen",
                "Updating selectedPairsId in SharedViewModel: ${sharedState.selectedPairsId}"
            )

            sharedViewModel.updateSelectedPairsId(sharedState.selectedPairsId)
        } else {
            Log.w("ReservationScreen", "selectedPairsId is empty, updating with empty map...")
            sharedViewModel.updateSelectedPairsId(emptyMap()) // Allow empty fields
        }

    }

    PrepareReservationButton(
        reservationViewModel = reservationViewModel,
        sharedState = sharedState,
        inactivityViewModel = inactivityViewModel,
        timeViewModel = timeViewModel
    )
}

/**
 * Primary CTA that triggers reservation preparation.
 * Guarded against double clicks and disabled until required inputs are present.
 */
@Composable
fun PrepareReservationButton(
    reservationViewModel: PrepareReservationViewModel,
    sharedState: SharedViewState.Saved,
    inactivityViewModel: InactivityViewModel,
    timeViewModel: TimeViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val isReservationPreparing = remember { mutableStateOf(false) }

    ButtonNormal(
        text = "Make reservation",
        modifier = Modifier
            .width(250.dp)
            .height(BUTTON_HEIGHT),
        color = Reservation,
        inactivityViewModel = inactivityViewModel,
        enabled = sharedState.selectedProject != null && sharedState.selectedInstrument != null,
        onClick = {
            prepareReservation(
                sharedState,
                timeViewModel,
                reservationViewModel,
                isReservationPreparing,
                coroutineScope
            )
        })
}

/**
 * Gathers inputs from [sharedState], ensures time is prepared, and asks the ViewModel
 * to create the reservation. Calls [onFinished] when the click cycle can be re-enabled.
 *
 * This function is intentionally small and side-effect light; consider moving all of
 * this into the ViewModel (see alternative below) for even tighter separation.
 */
private fun prepareReservation(
    sharedState: SharedViewState.Saved,
    timeViewModel: TimeViewModel,
    prepareReservationViewModel: PrepareReservationViewModel,
    isReservationPreparing: MutableState<Boolean>,
    coroutineScope: CoroutineScope
) {

    timeViewModel.prepareTime()

    val contact = sharedState.contact
    val instrumentId = sharedState.selectedInstrument!!.guid
    val selectedPairsId = sharedState.selectedPairsId
    val startTime = sharedState.startTime!!
    val endTime = sharedState.endTime!!
    val description = sharedState.reservationName ?: ""
    val selectedProject = sharedState.selectedProject?.id ?: ""
    val selectedSample = sharedState.selectedSample?.id
    val selectedSamples =
        if (selectedSample != null) listOf(selectedSample) else emptyList()

    // 🔍 Log before making request
    Log.d("prepareReservation", "selectedPairsId BEFORE request: $selectedPairsId")
    if (selectedPairsId.isEmpty()) {
        Log.e(
            "prepareReservation",
            "ERROR: selectedPairsId is EMPTY! Fields will be empty in request."
        )
    }

    val fields =
        mapOf(instrumentId to selectedPairsId.mapValues { it.value }) // This might be empty

    Log.d("prepareReservation", "Fields BEFORE request: $fields")

    if (!isReservationPreparing.value) {
        prepareReservationViewModel.prepareReservation(
            equipment = listOf(instrumentId),
            description = description,
            fields = fields,
            from = startTime,
            project = selectedProject,
            realised_for = contact!!.id,
            research_group = contact.researchGroup,
            samples = selectedSamples,
            status_code = 4,
            time_requirement = 1,
            to = endTime
        )
        coroutineScope.launch {
            delay(500)
            isReservationPreparing.value = false
        }
    }
}

