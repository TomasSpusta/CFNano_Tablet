package com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.components

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.navigation.Screen
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.InactivityViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.InstrumentsViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.SharedViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.SharedViewState
import com.nano_tablet.nanotabletrfid.mainApp.presentation.util.components.ButtonNormal
import com.nano_tablet.nanotabletrfid.ui.theme.Instrument
import com.nano_tablet.nanotabletrfid.util.UiConstants.BUTTON_HEIGHT

@Composable
fun SelectedInstrumentBtn(
    inactivityViewModel: InactivityViewModel,
    sharedViewModel: SharedViewModel,
    navController: NavController,
    sharedViewState: SharedViewState.Saved,

    instrumentsViewModel: InstrumentsViewModel,

) {
    ButtonNormal(
        onClick = {
            instrumentsViewModel.resetState()
            sharedViewModel.resetSharedState()
            navController.navigate(Screen.Instruments.route)
        },
        inactivityViewModel = inactivityViewModel,
        text = sharedViewState.selectedInstrument?.name ?: "Select Instrument",
        color = Instrument,
        modifier = Modifier
            .height(BUTTON_HEIGHT)
    )
}