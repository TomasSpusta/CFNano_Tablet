package com.nano_tablet.nanotabletrfid


import android.app.AlertDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.nano_tablet.nanotabletrfid.mainApp.presentation.MainNavigation
import com.nano_tablet.nanotabletrfid.ui.theme.NanoTabletRFIDTheme
import com.nano_tablet.nanotabletrfid.util.Event
import com.nano_tablet.nanotabletrfid.util.EventBus
import dagger.hilt.android.AndroidEntryPoint


/**
 * Entry point Activity for the Nano Tablet RFID app.
 *
 * Responsibilities:
 * - Installs the splash screen.
 * - Sets the Compose content and app theme.
 * - Collects one-off UI events (toast / alert dialog) from [EventBus]
 *   while the Activity is at least in the STARTED state.
 * - Hosts the app’s navigation graph via [MainNavigation].
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity(
) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()
        setContent {

            NanoTabletRFIDTheme {
                // Lifecycle used to collect events only when the activity is STARTED.
                val lifecycle = LocalLifecycleOwner.current.lifecycle
                /**
                 * One-shot collector of app-wide UI events.
                 *
                 * Uses repeatOnLifecycle(STARTED) so collection stops when the Activity moves
                 * to the background and restarts when it returns to foreground.
                 */
                LaunchedEffect(key1 = lifecycle) {
                    repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                        EventBus.events.collect { event ->
                            when (event) {
                                is Event.Toast -> {
                                    Toast.makeText(
                                        this@MainActivity,
                                        event.message,
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                }

                                is Event.AlertDialog -> {
                                    AlertDialog.Builder(this@MainActivity)
                                        .setTitle(event.title)
                                        .setMessage(event.message)
                                        .setCancelable(true)
                                        .setOnCancelListener {}
                                        .setPositiveButton("OK") { dialog, which ->
                                            event.onOkClicked?.invoke()
                                        }
                                        //.setNegativeButton("Back") { dialog, which ->Unit}
                                        .show()
                                }
                            }
                        }
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    MainNavigation()
                }
            }
        }

    }
}


@Preview(
    showBackground = true,
    //device = Devices.TABLET
)
@Composable
fun MainScreenPreview() {
    NanoTabletRFIDTheme {
        MainNavigation()

    }
}



