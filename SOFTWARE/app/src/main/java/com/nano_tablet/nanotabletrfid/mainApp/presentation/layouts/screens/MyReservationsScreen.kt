package com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.Reservation
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.InactivityViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.MyReservationsState
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.MyReservationsViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.TimeViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.util.components.ButtonToggle
import com.nano_tablet.nanotabletrfid.mainApp.presentation.util.components.LoadingDialog
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Screen that shows the current user's reservations.
 *
 * Observes [MyReservationsViewModel.stateFlow] and renders:
 * - Loading dialog while fetching,
 * - Grid of reservations on success,
 * - Simple text for error/erased states.
 */
@Composable
internal fun MyReservationsScreen(
    myReservationsViewModel: MyReservationsViewModel,
    inactivityViewModel: InactivityViewModel,
    timeViewModel: TimeViewModel
) {

    val reservationsState by myReservationsViewModel.stateFlow.collectAsStateWithLifecycle()

    when (reservationsState) {
        is MyReservationsState.Idle -> {}
        is MyReservationsState.Loading -> {
            LoadingDialog(isLoading = true, message = "Loading reservations")
        }

        is MyReservationsState.Success -> {
            ReservationsScreenContent(
                myReservationsViewModel = myReservationsViewModel,
                myReservationsState = reservationsState as MyReservationsState.Success,
                onReservationClicked = { reservation ->
                    myReservationsViewModel.onReservationClicked(reservation)
                },
                inactivityViewModel = inactivityViewModel,
                timeViewModel = timeViewModel
            )
        }

        is MyReservationsState.Error -> {
            Text(text = (reservationsState as MyReservationsState.Error).error)
        }
        is MyReservationsState.Erased -> {
            Text(text = (reservationsState as MyReservationsState.Erased).toString())
        }
    }
}
/**
 * Content for the reservations list.
 *
 * Side effects:
 * - Calls [timeViewModel.prepareTime] whenever the currently-selected reservation changes.
 */
@Composable
fun ReservationsScreenContent(
    myReservationsViewModel: MyReservationsViewModel,
    myReservationsState: MyReservationsState.Success,
    onReservationClicked: (Reservation) -> Unit,
    inactivityViewModel: InactivityViewModel,
    timeViewModel: TimeViewModel

) {
    val reservations = myReservationsState.reservations
    val selectedReservationIndex = myReservationsState.selectedReservationIndex
    val currentSelectedReservation = myReservationsState.selectedReservation

    LaunchedEffect(currentSelectedReservation) {
        timeViewModel.prepareTime()
    }


    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(reservations) { index, reservation ->
            reservation.name
            val startTime =  reservation.start
            val endTime =  reservation.end

            val dateStartTime = LocalDateTime.parse(startTime)
            val dateEndTime = LocalDateTime.parse(endTime)
            val formatter = DateTimeFormatter.ofPattern("HH:mm  dd.MM.yyyy")
            val formattedStartTime=dateStartTime.format(formatter)
            val formattedEndTime=dateEndTime.format(formatter)

            val buttonText = reservation.instrument +
                    "\n\nStart: $formattedStartTime" +
                    "\nEnd: $formattedEndTime"

            ButtonToggle(
                onClick = {
                    myReservationsViewModel.updateSelectedReservationIndex(index)
                    onReservationClicked(reservation)
                },
                isSelected = selectedReservationIndex == index,
                text = buttonText,
                inactivityViewModel = inactivityViewModel
            )
        }
    }
}

