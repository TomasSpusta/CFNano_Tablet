package com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import android.preference.PreferenceManager
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.State
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nano_tablet.nanotabletrfid.di.AppPreferences
import com.nano_tablet.nanotabletrfid.di.TabletLogger
import com.nano_tablet.nanotabletrfid.util.Event
import com.nano_tablet.nanotabletrfid.util.EventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class WifiStateViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appPreferences: AppPreferences,
    private val tabletLogger: TabletLogger
) : ViewModel() {

    private val _showArrow = MutableStateFlow(false)
    val showArrow: StateFlow<Boolean> = _showArrow

    private val isAlertShowing = MutableStateFlow(false)

    private val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private lateinit var wifiReceiver: BroadcastReceiver

    init {
        viewModelScope.launch {
            appPreferences.isFirstLaunch.firstOrNull()?.let { first ->
                if (first && wifiManager.isWifiEnabled) {
                    //showDebuggingReminder()
                    appPreferences.setFirstLaunchFalse()
                    tabletLogger.reportToGoogleSheet("Initial log", "Initial log after app startup")
                }
            }
        }
        registerWifiReceiver()
        registerConnectivityCallback()
    }


    private fun registerConnectivityCallback() {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        connectivityManager.registerNetworkCallback(
            request,
            object : ConnectivityManager.NetworkCallback() {
                override fun onLost(network: Network) {
                    // Wi-Fi network was disconnected (but still enabled)
                    showWifiDisconnectedAlert()
                }
            })
    }

    private fun showWifiDisconnectedAlert() {
        viewModelScope.launch {
            if (isAlertShowing.value) return@launch //prevent showing if already shown
            isAlertShowing.value = true

            EventBus.sendEvent(
                Event.AlertDialog(
                    title = "Wi-Fi Might be Disconnected",
                    message = "Please check the Wi-Fi connection. Wi-Fi should be 'devcei'." +
                            "\nPlease turn the Wireless debugging ON (right corner quick menu)" +
                            "\nIn case of any difficulties write to " +
                            "\ntomas.spusta@vutbr.cz" +
                            "\nnano@ceitec.vutbr.cz",
                    onOkClicked = { _showArrow.value = false
                                  isAlertShowing.value = false // reset wifi alert
                         },
                    onDismiss = { _showArrow.value = false
                                isAlertShowing.value = false // reset wifi alert
                                },
                )
            )
            _showArrow.value = true
        }
    }

    private fun showDebuggingReminder() {
        _showArrow.value = true
        viewModelScope.launch {
            tabletLogger.reportToGoogleSheet(
                "Wifi is connected",
                "Asking to turn on wifi debugging"
            )
            EventBus.sendEvent(
                Event.AlertDialog(
                    title = "Enable Wireless Debugging",
                    message = "Hi mindful User!" +
                            "\nTo increase chance of blitzkrieg support for you or for another users, please enable wireless debugging (just click on it)." +
                            "\nThank you very much.",
                    onOkClicked = {
                        _showArrow.value = false
                    },
                    { _showArrow.value = false },
                )
            )
        }
    }

    private fun registerWifiReceiver() {
        val filter = IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION)

        wifiReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val state = intent?.getIntExtra(
                    WifiManager.EXTRA_WIFI_STATE,
                    WifiManager.WIFI_STATE_UNKNOWN
                )

                if (state == WifiManager.WIFI_STATE_ENABLED) {
                    // showDebuggingReminder()
                }
            }
        }

        context.registerReceiver(wifiReceiver, filter)
    }


    override fun onCleared() {
        super.onCleared()
        context.unregisterReceiver(wifiReceiver)
    }
}


