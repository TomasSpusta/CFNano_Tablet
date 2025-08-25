package com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.components.operation_types

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.Operation
import com.nano_tablet.nanotabletrfid.util.UiConstants.BASE_PADDING


@Composable
fun OperationTextInput(
    operation: Operation,
    value: String,
    onValueChange: (String) -> Unit
) {
    Box(modifier = Modifier.padding(start = BASE_PADDING)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.width(300.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                ),
                label = { Text("Enter additional information") })
        }
    }
}