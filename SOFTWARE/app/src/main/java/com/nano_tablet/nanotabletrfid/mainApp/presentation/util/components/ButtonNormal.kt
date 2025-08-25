package com.nano_tablet.nanotabletrfid.mainApp.presentation.util.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.InactivityViewModel

@Composable
fun ButtonNormal(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    color: Color,
    shape: Shape = RoundedCornerShape(10.dp),
    enabled: Boolean = true,
    inactivityViewModel: InactivityViewModel? = null
) {
    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = Color.Black
        ),
        onClick = {
            onClick()
            inactivityViewModel?.resetTimer()
        },
        modifier = modifier,
        shape = shape,
        enabled = enabled


    ) {
        Text(text = text, fontSize = 22.sp)
    }
}
