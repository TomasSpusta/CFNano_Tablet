package com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.screens

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.nano_tablet.nanotabletrfid.ui.theme.NanoTabletRFIDTheme

@Composable
fun PlanningBoardScreen(onNavigate: () -> Unit) {
PlanningBoardWeb ()
}



@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PlanningBoardWeb () {
    val url = "https://today.ceitec.cz/nano/"
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = WebViewClient()
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.setSupportZoom(true)
            }
        },
        update = { webView ->
            webView.loadUrl(url)
        }
    )
}
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NanoTabletRFIDTheme {
        PlanningBoardWeb()
    }
}