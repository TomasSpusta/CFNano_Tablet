package com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nano_tablet.nanotabletrfid.ui.theme.NanoBlue
import com.nano_tablet.nanotabletrfid.util.UiConstants.BUTTON_HEIGHT
import com.nano_tablet.nanotabletrfid.util.UiConstants.MIN_WIDTH


@Composable
fun RowHeader(
    text: String
) {
    Box(
        modifier = Modifier
            .defaultMinSize(minWidth = MIN_WIDTH)
            .height(BUTTON_HEIGHT)
            .clip(shape = RoundedCornerShape(10.dp))
            .background(NanoBlue)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) { Text(text = text, fontSize = 25.sp,
        textAlign = TextAlign.Center) }
}


@Preview(showBackground = true, device = Devices.PIXEL_TABLET)
@Composable
fun OperationBoxPreview() {
    MaterialTheme {
        RowHeader("Projects")
    }
}