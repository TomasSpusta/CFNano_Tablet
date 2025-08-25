package com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.screens

import android.os.Build.VERSION_CODES.BASE
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.components.RowHeader
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.InactivityViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.SharedViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.SharedViewState
import com.nano_tablet.nanotabletrfid.util.UiConstants.BASE_PADDING


@Composable
internal fun ReservationNameScreen(
    sharedViewModel: SharedViewModel,
    sharedViewState: SharedViewState,
    inactivityViewModel: InactivityViewModel
) {

    when (sharedViewState) {
        is SharedViewState.Saved -> {
            ReservationNameScreenContent(
                sharedViewModel = sharedViewModel,
                inactivityViewModel = inactivityViewModel,
            )
        }

        else -> {}
    }
}

@Composable
fun ReservationNameScreenContent(
    sharedViewModel: SharedViewModel,
    inactivityViewModel: InactivityViewModel,
) {

    var reservationName by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .height(75.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {
        RowHeader("Reservation Name")
        TextField(
            value = reservationName,
            onValueChange = {
                reservationName = it
                if (reservationName.isNotBlank()) {
                    sharedViewModel.updateState(
                        reservationName = reservationName
                    )
                    sharedViewModel.updateReservationName(reservationName)
                    inactivityViewModel.resetTimer()
                }
            },
            label = { Text("Enter Reservation Name") },
            modifier = Modifier
                .width(300.dp)
                .padding(start = BASE_PADDING),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
            )
        )
    }
}