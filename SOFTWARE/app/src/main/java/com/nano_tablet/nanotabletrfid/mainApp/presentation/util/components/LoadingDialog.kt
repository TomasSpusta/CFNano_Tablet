package com.nano_tablet.nanotabletrfid.mainApp.presentation.util.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nano_tablet.nanotabletrfid.ui.theme.NanoTabletRFIDTheme


@Composable
fun LoadingDialog(isLoading: Boolean, message: String) {
    if (isLoading) {
        Dialog(
            onDismissRequest = { /*TODO*/ },
            properties = DialogProperties(dismissOnClickOutside = true)
        ) {
            Box(
                modifier = Modifier
                    .width(300.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color.White)
                    .aspectRatio(2.0f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier,
                    //verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(text = message)
                }

            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun LoadingDialogPreview() {
    NanoTabletRFIDTheme {
        LoadingDialog(
            isLoading = true,
            message = "Loading..."
        )
    }
}