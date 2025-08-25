package com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.screens


import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.components.RowHeader
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.InactivityViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.TimeViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.util.components.ButtonNormal
import com.nano_tablet.nanotabletrfid.ui.theme.Green
import com.nano_tablet.nanotabletrfid.ui.theme.NanoBlue
import com.nano_tablet.nanotabletrfid.util.UiConstants.BASE_PADDING
import com.nano_tablet.nanotabletrfid.util.UiConstants.BUTTON_HEIGHT


@Composable
fun TimePicker(
    timeViewModel: TimeViewModel?,
    inactivityViewModel: InactivityViewModel?
) {
    var selectedField by remember { mutableStateOf("minute") }
    var hour by remember { mutableStateOf(0) }
    var minute by remember { mutableStateOf(15) }
    var selectedTime by remember { mutableStateOf(0) }

    // Functions to update values
    fun increment() {
        if (selectedField == "hour") {
            if (hour < 72) hour += 1
        } else {
            if (minute < 55) minute += 5
        }
        selectedTime = hour * 60 + minute
        timeViewModel?.saveTime(selectedTime)
        timeViewModel?.prepareTime()
    }

    fun decrement() {
        if (selectedField == "hour") {
            if (hour > 0) hour -= 1
        } else {
            if (minute > 5) minute -= 5
        }
        selectedTime = hour * 60 + minute
        timeViewModel?.saveTime(selectedTime)
        timeViewModel?.prepareTime()
    }


    Row(
        modifier = Modifier.padding(bottom = BASE_PADDING),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        RowHeader("Pick Time")
        Box(
            modifier = Modifier
                .height(BUTTON_HEIGHT)
                .width(100.dp)
                .border(
                    width = 3.dp,
                    color = if (selectedField == "hour") Green else Color.Gray,
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable { selectedField = "hour" },

            ) {
            Text(
                text = String.format("%02d h", hour),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.Center)

            )
        }

        Box(
            modifier = Modifier
                .height(BUTTON_HEIGHT)
                .width(100.dp)
                .border(
                    width = 3.dp,
                    color = if (selectedField == "minute") Green else Color.Gray,
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable { selectedField = "minute" },

        ) {
            Text(
                text = String.format("%02d min", minute),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.Center)

            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ButtonNormal(
                modifier = Modifier.size(BUTTON_HEIGHT),
                onClick = { decrement() },
                text = "-",
                color = NanoBlue,
                inactivityViewModel = inactivityViewModel
            )
            ButtonNormal(
                modifier = Modifier.size(BUTTON_HEIGHT),
                onClick = { increment() },
                text = "+",
                color = NanoBlue,
                inactivityViewModel = inactivityViewModel
            )
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_TABLET)
@Composable
fun SelectionScreenContentPreview() {
    MaterialTheme {
        TimePicker(timeViewModel = null, inactivityViewModel = null)
    }
}
