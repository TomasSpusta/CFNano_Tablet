package com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.components.LogOutBtn
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.components.MyReservationsBtn
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.components.SelectedInstrumentBtn
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.ContactViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.ContactViewState
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.PrepareReservationViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.DefaultProjectViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.DefaultProjectViewState
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.InactivityViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.InstrumentsViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.OperationsViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.MyReservationsViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.SharedViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.SharedViewState
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.TimeViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.TokenViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.util.components.LoadingDialog


/**
 * Thin wrapper that renders [ContactScreenContent] only when the shared state is available.
 */
@Composable
internal fun ContactScreen(
    sharedState: SharedViewState,
    sharedViewModel: SharedViewModel,
    contactViewModel: ContactViewModel,
    inactivityViewModel: InactivityViewModel,
    tokenViewModel: TokenViewModel,
    myReservationsViewModel: MyReservationsViewModel,
    onNavigationFinished: () -> Unit,
    innerNavController: NavController,
    defaultProjectViewModel: DefaultProjectViewModel,
    instrumentsViewModel: InstrumentsViewModel,
    timeViewModel: TimeViewModel,
    operationsViewModel: OperationsViewModel,
    reservationViewModel: PrepareReservationViewModel


) {

    when (sharedState) {
        is SharedViewState.Saved -> {
            ContactScreenContent(
                sharedViewModel = sharedViewModel,
                sharedState = sharedState,
                onNavigationFinished = onNavigationFinished,
                contactViewModel = contactViewModel,
                inactivityViewModel = inactivityViewModel,
                tokenViewModel = tokenViewModel,
                myReservationsViewModel = myReservationsViewModel,
                navController = innerNavController,
                defaultProjectViewModel = defaultProjectViewModel,
                instrumentsViewModel = instrumentsViewModel,
                timeViewModel = timeViewModel,
                operationsViewModel = operationsViewModel,
                reservationViewModel = reservationViewModel,
            )
        }

        else -> {}
    }

}

/**
 * Top bar with:
 * - button to "My reservations",
 * - currently selected instrument button,
 * - "Make reservation" CTA,
 * - and logout.
 *
 * Side-effects (one-shot & lifecycle-aware):
 * - Fetches contact on first entry (when state is Idle and a user GUID is present).
 * - When contact loads, mirrors it into [SharedViewModel] and fetches default project.
 * - When default project loads, mirrors it into [SharedViewModel].
 */
@Composable
fun ContactScreenContent(
    sharedViewModel: SharedViewModel,
    sharedState: SharedViewState.Saved,
    onNavigationFinished: () -> Unit,

    inactivityViewModel: InactivityViewModel,
    contactViewModel: ContactViewModel,
    tokenViewModel: TokenViewModel,
    myReservationsViewModel: MyReservationsViewModel,
    navController: NavController,
    defaultProjectViewModel: DefaultProjectViewModel,
    instrumentsViewModel: InstrumentsViewModel,
    timeViewModel: TimeViewModel,
    operationsViewModel: OperationsViewModel,
    reservationViewModel: PrepareReservationViewModel,

    ) {


    val contactState by contactViewModel.stateFlow.collectAsStateWithLifecycle()
    val defaultProjectState by defaultProjectViewModel.stateFlow.collectAsStateWithLifecycle()
    val user = sharedState.user
    val contact = sharedState.contact

    when (val currentContactState = contactState) {
        is ContactViewState.Idle -> contactViewModel.fetchContact(userId = user!!.guid)
        is ContactViewState.Loading -> LoadingDialog(
            isLoading = true,
            message = "Loading contact information"
        )

        is ContactViewState.Success -> {
            LaunchedEffect(key1 = contactState) {
                sharedViewModel.updateState(
                    contact = currentContactState.contact,
                )
                Log.i("DefaultProject", "Fetching default project")
                defaultProjectViewModel.fetchDefaultProject(currentContactState.contact.defaultProjectId)
            }
            LaunchedEffect(key1 = defaultProjectState) {
                if (defaultProjectState is DefaultProjectViewState.Success) {
                    sharedViewModel.updateState(defaultProject = (defaultProjectState as DefaultProjectViewState.Success).defaultProject)
                }
            }
        }

        else -> {}
    }

    Row(
        modifier = Modifier
            .fillMaxHeight(0.1f)
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MyReservationsBtn(
            viewModel = inactivityViewModel,
            myReservationsViewModel = myReservationsViewModel,
            navController = navController,
            sharedViewState = sharedState
        )

        SelectedInstrumentBtn(
            inactivityViewModel = inactivityViewModel,
            sharedViewModel = sharedViewModel,
            navController = navController,
            sharedViewState = sharedState,
            instrumentsViewModel = instrumentsViewModel
        )

        PrepareReservationScreen(
            sharedState = sharedState,
            prepareReservationViewModel = reservationViewModel,
            inactivityViewModel = inactivityViewModel,
            sharedViewModel = sharedViewModel,
            instrumentsViewModel = instrumentsViewModel,
            operationsViewModel = operationsViewModel,
            timeViewModel = timeViewModel,
            navController = navController
        )


        LogOutBtn(
            onNavigationFinished,
            viewModel = inactivityViewModel,
            contact = contact
        )
    }
}
