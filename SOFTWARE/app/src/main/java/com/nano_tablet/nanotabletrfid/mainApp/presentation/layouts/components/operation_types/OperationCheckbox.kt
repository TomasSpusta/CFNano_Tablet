package com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.components.operation_types

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.Operation
import com.nano_tablet.nanotabletrfid.util.UiConstants.BASE_PADDING

@Composable
fun OperationCheckbox(
    operation: Operation,
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = BASE_PADDING)
    ) {
        Switch(
            checked = isChecked,
            onCheckedChange = { onToggle(it) }
        )
    }
}
