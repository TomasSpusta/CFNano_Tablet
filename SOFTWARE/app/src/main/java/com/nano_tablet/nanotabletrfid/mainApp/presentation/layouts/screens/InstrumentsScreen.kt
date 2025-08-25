package com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.nano_tablet.nanotabletrfid.BuildConfig
import com.nano_tablet.nanotabletrfid.instruments.Instruments
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.navigation.Screen
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.InactivityViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.InstrumentsViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.InstrumentsViewState
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.OperationsViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.ProjectsViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.SamplesViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.SharedViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.SharedViewState
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.TimeViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.TokenViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.util.components.ButtonNormal
import com.nano_tablet.nanotabletrfid.ui.theme.Instrument

/**
 * Entry point for the Instruments feature.
 *
 * Observes [InstrumentsViewModel.stateFlow] and renders the menu when data is loaded.
 * Navigation + data prefetch for the selected instrument happens in the child content.
 */
@Composable
internal fun InstrumentsScreen(
    navController : NavController,
    inactivityViewModel: InactivityViewModel,
    instrumentsViewModel: InstrumentsViewModel,
    timeViewModel: TimeViewModel,
    operationsViewModel: OperationsViewModel,
    tokenViewModel: TokenViewModel = hiltViewModel(),
    sharedViewModel: SharedViewModel,
    samplesViewModel: SamplesViewModel,
    projectsViewModel: ProjectsViewModel,
    sharedState: SharedViewState.Saved


    ) {

    val instrumentsState by instrumentsViewModel.stateFlow.collectAsStateWithLifecycle()

    when (instrumentsState) {
        is InstrumentsViewState.Loaded -> {
            InstrumentsMenuContent(
                state = instrumentsState as InstrumentsViewState.Loaded,
                navController=navController,
                instrumentsViewModel = instrumentsViewModel,
                operationsViewModel = operationsViewModel,
                tokenViewModel = tokenViewModel,
                sharedViewModel = sharedViewModel,
                inactivityViewModel = inactivityViewModel,
                timeViewModel = timeViewModel,
                samplesViewModel= samplesViewModel,
                projectsViewModel= projectsViewModel,
                sharedState = sharedState,


            )
        }
    }
}

/**
 * Displays a grid of instruments. When an instrument is tapped:
 * - selects it in [InstrumentsViewModel]
 * - ensures a valid token
 * - prefetches operations, samples, and projects
 * - navigates to the selection screen
 */
@Composable
fun InstrumentsMenuContent(
    state: InstrumentsViewState.Loaded,
    instrumentsViewModel: InstrumentsViewModel,
    operationsViewModel: OperationsViewModel,
    sharedViewModel: SharedViewModel,
    tokenViewModel: TokenViewModel,
    inactivityViewModel: InactivityViewModel,
    timeViewModel: TimeViewModel,
    navController: NavController,
    samplesViewModel: SamplesViewModel,
    projectsViewModel: ProjectsViewModel,
    sharedState: SharedViewState.Saved
) {

    //val instrumentsList = Instruments.NanoInstruments
    //val instrumentsList = Instruments.StanInstruments
    //val instrumentsList = Instruments.EWInstruments
    //val instrumentsList = Instruments.TestInstruments

    // Choose instrument list based on build-time flag.
    val instrumentsList = when (BuildConfig.INSTRUMENT_LIST){
        "CLEAN100" -> Instruments.Clean100Instruments
        "STAN" -> Instruments.StanInstruments
        "EW" -> Instruments.EWInstruments
        "CERAM" -> Instruments.CeramInstruments
        else -> Instruments.TestInstruments
    }

    val currentSelectedInstrument = state.selectedInstrument

    LaunchedEffect(currentSelectedInstrument) {
        timeViewModel.prepareTime()
        sharedViewModel.updateState(selectedInstrument = currentSelectedInstrument)
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(instrumentsList) { instrument ->
            ButtonNormal(
                modifier = Modifier.height(100.dp),
                inactivityViewModel = inactivityViewModel,

                onClick = {
                    instrumentsViewModel.selectInstrument(instrument)
                    tokenViewModel.verifyAndRefreshTokenIfNeeded()
                    operationsViewModel.fetchOperations(instrument.guid)
                    sharedState.user?.let { samplesViewModel.fetchSamples(userId = it.guid) }
                    sharedState.user?.let { projectsViewModel.fetchProjects(userId = it.guid) }
                    navController.navigate(Screen.Selection.route)
                },
                text = instrument.name,
                color = Instrument,
                )
        }
    }
}
