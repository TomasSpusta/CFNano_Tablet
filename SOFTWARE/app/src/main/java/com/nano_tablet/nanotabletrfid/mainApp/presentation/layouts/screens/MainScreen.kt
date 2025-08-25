package com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.screens

import SelectionScreen
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.navigation.Screen
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.ContactViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.PrepareReservationViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.DefaultProjectViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.InactivityViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.InstrumentsViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.LogViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.OperationsViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.ProjectsViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.MyReservationsViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.SamplesViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.SharedViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.SharedViewState
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.TimeViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.TimeViewState
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.TokenViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.util.components.LoadingDialog

/**
 * Root of the main app flow shown after successful login.
 *
 * Responsibilities:
 * - Hosts a nested NavController for feature screens (Instruments, Selection, MyReservations).
 * - Observes inactivity and triggers [onNavigationFinished] to log the user out.
 * - Delegates to [MainScreenContent] when [sharedState] is resolved.
 */
@Composable
internal fun MainScreen(
    onNavigationFinished: () -> Unit,
    instrumentsViewModel: InstrumentsViewModel = hiltViewModel(),
    operationsViewModel: OperationsViewModel = hiltViewModel(),
    projectsViewModel: ProjectsViewModel = hiltViewModel(),
    samplesViewModel: SamplesViewModel = hiltViewModel(),
    timeViewModel: TimeViewModel = hiltViewModel(),
    prepareReservationViewModel: PrepareReservationViewModel = hiltViewModel(),
    inactivityViewModel: InactivityViewModel = hiltViewModel(),
    logViewModel: LogViewModel = hiltViewModel(),
    contactViewModel: ContactViewModel = hiltViewModel(),
    tokenViewModel: TokenViewModel = hiltViewModel(),
    myReservationsViewModel: MyReservationsViewModel = hiltViewModel(),
    defaultProjectViewModel: DefaultProjectViewModel = hiltViewModel(),
    sharedState: SharedViewState,
    sharedViewModel: SharedViewModel,


    ) {


    val innerNavController = rememberNavController()
// If screen is inactive (no user input for N minutes), exit to login.
    val isInactive by inactivityViewModel.isInactive.collectAsStateWithLifecycle()

    LaunchedEffect(isInactive) {
        if (isInactive) {
            onNavigationFinished()
        }
    }

    when (sharedState) {
        is SharedViewState.Saved -> {
            MainScreenContent(
                innerNavController = innerNavController,
                prepareReservationViewModel = prepareReservationViewModel,
                myReservationsViewModel = myReservationsViewModel,
                onNavigationFinished = onNavigationFinished,
                instrumentsViewModel = instrumentsViewModel,
                operationsViewModel = operationsViewModel,
                projectsViewModel = projectsViewModel,
                samplesViewModel = samplesViewModel,
                timeViewModel = timeViewModel,
                inactivityViewModel = inactivityViewModel,
                sharedState = sharedState,
                sharedViewModel = sharedViewModel,
                logViewModel = logViewModel,
                contactViewModel = contactViewModel,
                tokenViewModel = tokenViewModel,
                defaultProjectViewModel = defaultProjectViewModel,

                )
        }

        else -> {
            LoadingDialog(isLoading = true, message = "Main Screen Loading")
        }
    }
}

/**
 * Main content layout with top [ContactScreen] and a right-side column hosting the feature NavHost.
 *
 * Side-effects:
 * - Mirrors [TimeViewState.Set] into [SharedViewModel] to keep global selection consistent.
 * - Resets inactivity timer on any pointer tap.
 */
@Composable
fun MainScreenContent(
    innerNavController: NavHostController,
    onNavigationFinished: () -> Unit,
    prepareReservationViewModel: PrepareReservationViewModel,
    myReservationsViewModel: MyReservationsViewModel,
    instrumentsViewModel: InstrumentsViewModel,
    operationsViewModel: OperationsViewModel,
    projectsViewModel: ProjectsViewModel,
    samplesViewModel: SamplesViewModel,
    timeViewModel: TimeViewModel,
    sharedState: SharedViewState.Saved,
    sharedViewModel: SharedViewModel,
    inactivityViewModel: InactivityViewModel,
    logViewModel: LogViewModel,
    contactViewModel: ContactViewModel,
    tokenViewModel: TokenViewModel,
    defaultProjectViewModel: DefaultProjectViewModel,

    ) {

    val timeState by timeViewModel.stateFlow.collectAsStateWithLifecycle()

    when (val currentTimeState = timeState) {
        is TimeViewState.Set -> {
            LaunchedEffect(key1 = timeState) {
                sharedViewModel.updateState(
                    selectedTime = currentTimeState.selectedTime,
                    startTime = currentTimeState.startTime,
                    endTime = currentTimeState.endTime
                )

            }
        }
    }



//main column
    Column(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTapGestures(onPress = { inactivityViewModel.resetTimer() })
        }) {

        ContactScreen(
            sharedState = sharedState,
            sharedViewModel = sharedViewModel,
            contactViewModel = contactViewModel,
            inactivityViewModel = inactivityViewModel,
            //logViewModel = logViewModel,
            tokenViewModel = tokenViewModel,
            myReservationsViewModel = myReservationsViewModel,
            onNavigationFinished = onNavigationFinished,
            innerNavController = innerNavController,
            defaultProjectViewModel = defaultProjectViewModel,
            instrumentsViewModel = instrumentsViewModel,
            timeViewModel = timeViewModel,
            operationsViewModel = operationsViewModel,
            reservationViewModel = prepareReservationViewModel
        )


        Row(
            modifier = Modifier.fillMaxSize(1f)
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                NavHost(
                    navController = innerNavController,
                    startDestination = Screen.Instruments.route
                ) {
                    composable(Screen.Instruments.route) {
                        InstrumentsScreen(
                            instrumentsViewModel = instrumentsViewModel,
                            timeViewModel = timeViewModel,
                            operationsViewModel = operationsViewModel,
                            sharedViewModel = sharedViewModel,
                            inactivityViewModel = inactivityViewModel,
                            navController = innerNavController,
                            samplesViewModel = samplesViewModel,
                            projectsViewModel = projectsViewModel,
                            sharedState = sharedState
                        )
                    }

                    composable(Screen.Selection.route) {
                        SelectionScreen(
                            navController = innerNavController,
                            operationsViewModel =operationsViewModel,
                            samplesViewModel = samplesViewModel,
                            projectsViewModel = projectsViewModel,
                            sharedViewModel = sharedViewModel,
                            inactivityViewModel = inactivityViewModel,
                            sharedState = sharedState,
                            reservationViewModel = prepareReservationViewModel,
                            instrumentsViewModel= instrumentsViewModel,
                            timeViewModel=timeViewModel,

                        )
                    }

                    composable(Screen.MyReservations.route) {
                        MyReservationsScreen(
                            myReservationsViewModel = myReservationsViewModel,
                            inactivityViewModel = inactivityViewModel,
                            timeViewModel = timeViewModel,
                        )
                    }
                }
            }
        }
    }
}