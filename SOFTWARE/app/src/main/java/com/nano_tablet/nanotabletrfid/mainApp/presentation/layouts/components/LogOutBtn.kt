package com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.components

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.Contact
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.InactivityViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.util.components.ButtonNormal
import com.nano_tablet.nanotabletrfid.ui.theme.LogOut
import com.nano_tablet.nanotabletrfid.util.UiConstants.BUTTON_HEIGHT

@Composable
fun LogOutBtn(
    onNavigationFinished: () -> Unit,
    viewModel: InactivityViewModel,
    contact: Contact?
) {
    ButtonNormal(
        onClick = { viewModel.stopTimer()
            onNavigationFinished()
                  },
        text = "${contact?.firstName} ${contact?.lastName} Log out",
        color = LogOut,
        modifier = Modifier
            .height(BUTTON_HEIGHT),
    )
}