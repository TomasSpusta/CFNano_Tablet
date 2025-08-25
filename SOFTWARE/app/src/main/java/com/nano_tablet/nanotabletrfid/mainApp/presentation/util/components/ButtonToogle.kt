package com.nano_tablet.nanotabletrfid.mainApp.presentation.util.components

import android.util.Log
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
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
import com.nano_tablet.nanotabletrfid.ui.theme.NotSelected
import com.nano_tablet.nanotabletrfid.ui.theme.Selected
import com.nano_tablet.nanotabletrfid.util.UiConstants.BUTTON_HEIGHT
import com.nano_tablet.nanotabletrfid.util.UiConstants.MIN_WIDTH

@Composable
fun ButtonToggle(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    shape: Shape = RoundedCornerShape(10.dp),
    isSelected: Boolean,
    inactivityViewModel: InactivityViewModel? = null
) {
    Button(
        onClick = {
            onClick()
            inactivityViewModel?.resetTimer()
            Log.d("ButtonToggle", "Text=$text, isSelected=$isSelected")
        },
        modifier = modifier.height(BUTTON_HEIGHT).defaultMinSize(minWidth = MIN_WIDTH),
        shape = shape,

        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Selected else NotSelected,
            contentColor = Color.Black
        )
    ) {

        Text(text = text, fontSize = 20.sp)
    }
}
