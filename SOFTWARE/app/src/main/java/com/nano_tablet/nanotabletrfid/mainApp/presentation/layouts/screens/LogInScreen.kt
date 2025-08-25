package com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nano_tablet.nanotabletrfid.R
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.DeviceReporterViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.SharedViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.TokenViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.TokenViewState
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.UserViewModel
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.UserViewState
import com.nano_tablet.nanotabletrfid.mainApp.presentation.util.components.LoadingDialog
//import com.example.nanotabletrfid.util.Constant.API_KEY_LOG
import com.nano_tablet.nanotabletrfid.util.Constant.APP_VERSION
//import com.example.nanotabletrfid.util.Constant.LIBRARY_ID
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * Log-in screen:
 * - Waits for card scan (via [ScanCardField]).
 * - Verifies user (RFID) through [UserViewModel].
 * - Ensures a valid token ([TokenViewModel]).
 * - Stores the user in shared state and navigates on success.
 *
 * Side effects:
 * - Reports a one-off device log via [logViewModel.reportOnce] on first composition.
 */
@Composable
internal fun LogInScreen(
    onNavigate: () -> Unit,
    onNavigatePlanningBoard: () -> Unit,
    onNavigateRegisterCard: () -> Unit,
    sharedViewModel: SharedViewModel,
    userViewModel: UserViewModel = hiltViewModel(),
    tokenViewModel: TokenViewModel = hiltViewModel(),
    logViewModel: DeviceReporterViewModel = hiltViewModel(),
    ) {
    val userState by userViewModel.stateFlow.collectAsStateWithLifecycle()
    val tokenState by tokenViewModel.stateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        logViewModel.reportOnce()
    }

    when (userState) {
        is UserViewState.Loading -> {
            LoadingDialog(isLoading = true, message = "Loading user")
        }

        is UserViewState.Success -> {
            LogInScreenContent(
                onNavigate = onNavigate,
                userState = userState as UserViewState.Success,
                sharedViewModel = sharedViewModel,
                tokenViewModel = tokenViewModel,
                tokenState = tokenState                           )
        }
        else -> {}
    }
    LogInField(
        userViewModel = userViewModel,
        onNavigatePlanningBoard = onNavigatePlanningBoard,
        onNavigateBookingSystem = onNavigateRegisterCard,
        //logViewModel = logViewModel
    )

}
/**
 * Coordinates user state + token state:
 * - Updates shared state with the resolved user.
 * - Navigates when both user and token are ready.
 */
@Composable
fun LogInScreenContent(
    onNavigate: () -> Unit,
    userState: UserViewState.Success,
    sharedViewModel: SharedViewModel,
    tokenViewModel: TokenViewModel,
    tokenState: TokenViewState,
) {
    LaunchedEffect(key1 = null) {
        tokenViewModel.verifyAndRefreshTokenIfNeeded()
        delay(500)
    }

    val context = LocalContext.current

    LaunchedEffect(userState, tokenState) {
        sharedViewModel.updateState(user = userState.user)


        if (userState.user.guid.isEmpty()) {
            Toast.makeText(context, "Card not registered", Toast.LENGTH_LONG).show()
        } else {
            when (tokenState) {
                is TokenViewState.Success -> {
                    onNavigate()
                }

                is TokenViewState.Idle -> {
                }

                else -> {}
            }
        }
    }
}

/**
 * Text field that captures a card ID (from an RFID “keyboard” wedge).
 * Submits automatically on IME Done when input has 10 characters.
 */
@Composable
fun LogInField(
    userViewModel: UserViewModel,
    onNavigatePlanningBoard: () -> Unit,
    onNavigateBookingSystem: () -> Unit,
) {
    Box(modifier = Modifier
        .fillMaxSize()
    ) {
        Information(
            onNavigatePlanningBoard = onNavigatePlanningBoard,
            onNavigateBookingSystem = onNavigateBookingSystem,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),

            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Top,

            ) {
            ScanCardImage()
            ScanCardField(userViewModel = userViewModel)
        }

    }

}
/**
 * Captures the 10-digit card ID and triggers [UserViewModel.fetchUser].
 * Implements a simple re-entrancy guard to avoid double-submission.
 */
@Composable
fun ScanCardField(
    userViewModel: UserViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    val isFetchingExecuted = remember { mutableStateOf(false) }
    var cardId by remember { mutableStateOf("") }

    OutlinedTextField(

        singleLine = true,
        value = cardId,
        onValueChange = {
            cardId = it
        },
        label = { Text("Please scan your card") },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Ascii,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = {
            if (!isFetchingExecuted.value && cardId.length == 10) {
                Log.d("Login", "Enter pressed")
                isFetchingExecuted.value = true
                softwareKeyboardController?.hide()
                userViewModel.fetchUser(cardId = cardId)

                coroutineScope.launch {
                    delay(500)
                    isFetchingExecuted.value = false
                    cardId = ""

                }
            } else {
                Toast.makeText(context, "Please scan card again ", Toast.LENGTH_LONG).show()
                cardId = ""
            }
        }),
        visualTransformation = PasswordVisualTransformation(),
    )
}

@Composable
fun ScanCardImage() {
    Column {
        Card(
            Modifier
                .size(250.dp)
                .background(Color.Transparent), shape = RectangleShape
        ) {
            Image(
                painter = painterResource(id = R.drawable.scan_card_icon_white),
                contentDescription = "Scan Card",
                contentScale = ContentScale.Fit
            )
        }
    }
}


@Composable
fun Information(
    onNavigatePlanningBoard: () -> Unit,
    onNavigateBookingSystem: () -> Unit,
) {

    Column(
        Modifier
            .padding(start = 10.dp)

    ) {
        Text(text = "How to operate:")
        Text(text = "1. Scan your registered user (employee card). Either from front (see icon --->), or from the back, same place.")
        Text(text = "2. Select instrument you want to use.")
        Text(text = "3. Select operations and their details.")
        Text(text = "4. Select Time - default 15 minutes, Select project and optionally sample.")
        Text(text = "5. Press 'Make reservation' button, it should notify you that reservation was created.")
        Text(text = "6. Button 'Instruments' will reset everything to default values.")

        Text(text = "\nFixes: ")
        Text(text = "Swiping card should work as before. One swipe = login")

        Text(text = "\nTODO and known issues: ")
        Text(text = "Known issue/feature -> Wifi alerts shows when disconnects/connects to wifi")
        Text(text = "")
        Text(text = "Version $APP_VERSION")
        Text(text = "")
        Text(text = "In the case of bugs, feedback, comments or suggestions, please contact: ")
        Text(text = "me -> tomas.spusta@vut.cz, or Nano Office -> nano@ceitec.vutbr.cz")


        Button(onClick = onNavigatePlanningBoard, modifier = Modifier.focusProperties{canFocus=false}) {
            Text(text = "Planning board")
        }
        Button(onClick = onNavigateBookingSystem,modifier = Modifier.focusProperties{canFocus=false}) {
            Text(text = "Booking system - register card")
        }
    }
}






