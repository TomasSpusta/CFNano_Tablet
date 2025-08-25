package com.nano_tablet.nanotabletrfid.di

import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import android.util.Log.e
import com.nano_tablet.nanotabletrfid.BuildConfig
import com.nano_tablet.nanotabletrfid.util.Constant.LOG_URL

import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.hours

/**
* Reports device status and logs to a Google Apps Script endpoint.
*
* Responsibilities:
* - Send ad‑hoc log entries via [reportToGoogleSheet].
* - Periodically send a heartbeat via [startHeartbeatLoop] / [sendHeartbeat].
*
* Notes on threading:
* - Uses IO dispatcher for network calls.
* - Launches coroutines from ad‑hoc scopes (see "Improvements" about structured concurrency).
*/
@Singleton
class TabletLogger @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val ip = getLocalIpAddress()
    val deviceName = BuildConfig.INSTRUMENT_LIST


    private val endpointUrl = LOG_URL

    fun reportToGoogleSheet(tag: String, message: String) {

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(endpointUrl)
                val json = JSONObject().apply {
                    put("ip", ip)
                    put("deviceName", deviceName)
                    put("tag", tag)
                    put("message", message)
                    put("isHeartbeat", false)
                }

                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "POST"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    outputStream.bufferedWriter().use { it.write(json.toString()) }

                    if (responseCode == 200) {
                        Log.d("DeviceReporter", "IP sent successfully")
                    } else {
                        e("DeviceReporter", "Error: $responseCode")
                    }
                }
            } catch (e: Exception) {
                e("DeviceReporter", "Exception: ${e.message}", e)
            }
        }
    }

    private fun getLocalIpAddress(): String {
        return try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            interfaces.toList().flatMap { it.inetAddresses.toList() }
                .firstOrNull { !it.isLoopbackAddress && it is Inet4Address }
                ?.hostAddress ?: "Unavailable"
        } catch (e: Exception) {
            "Unavailable"
        }
    }

    /**
     * Starts an infinite heartbeat loop that posts status once per hour.
     * The loop runs until the coroutine scope is cancelled.
     *
     * Warning: the returned Job is not stored; cancelling is not possible
     * from this class as‑is (see improvements for structured concurrency).
     */
    fun startHeartbeatLoop() {
        CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                sendHeartbeat()
                delay(1.hours)
            }
        }
    }

    fun sendHeartbeat() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(endpointUrl)
                val json = JSONObject().apply {
                    put("ip", getLocalIpAddress())
                    put("deviceName", deviceName)
                    put("tag", BuildConfig.INSTRUMENT_LIST)
                    put("message", "Device is online")
                    put("isHeartbeat", true) // 👈 tell server this is a heartbeat
                }

                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "POST"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    outputStream.bufferedWriter().use { it.write(json.toString()) }

                    if (responseCode == 200) {
                        Log.d("DeviceReporter", "Heartbeat sent successfully")
                    } else {
                        e("DeviceReporter", "Error: $responseCode")
                    }
                }

            } catch (e: Exception) {
                e("DeviceReporter", "Heartbeat error: ${e.message}", e)
            }
        }
    }
}
