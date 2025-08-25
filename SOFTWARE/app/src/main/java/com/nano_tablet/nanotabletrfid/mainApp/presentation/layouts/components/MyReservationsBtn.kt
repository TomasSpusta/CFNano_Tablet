package com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.components

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.navigation.Screen
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.InactivityViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.MyReservationsViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.SharedViewState
import com.nano_tablet.nanotabletrfid.mainApp.presentation.util.components.ButtonNormal
import com.nano_tablet.nanotabletrfid.ui.theme.Reservation
import com.nano_tablet.nanotabletrfid.util.UiConstants.BUTTON_HEIGHT
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun MyReservationsBtn(
    viewModel: InactivityViewModel,
    myReservationsViewModel:MyReservationsViewModel,
    navController: NavController,
    sharedViewState: SharedViewState.Saved
) {


    val timeFormat = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    val owner = "ownerid.systemuserid:eq:${sharedViewState.contact?.systemUserId}"
    val now = LocalDateTime.now().plusMinutes(1).format(timeFormat)
    val todayStart = LocalDateTime.now().with(LocalTime.MIDNIGHT).format(timeFormat)

    LocalDateTime.now().plusDays(1).with(LocalTime.MIDNIGHT).format(timeFormat)
    val start = "scheduledstart:gt:$todayStart"
    val end = "scheduledend:gt:$now"

    ButtonNormal(
        onClick = {
            myReservationsViewModel.fetchReservations(owner, start, end)
            navController.navigate(Screen.MyReservations.route)
        },
        text = "My reservations",
        color = Reservation,
        modifier = Modifier
            .height(BUTTON_HEIGHT),
        inactivityViewModel = viewModel
    )
}
