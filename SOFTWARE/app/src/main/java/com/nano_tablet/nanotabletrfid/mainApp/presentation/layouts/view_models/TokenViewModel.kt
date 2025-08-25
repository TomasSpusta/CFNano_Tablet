package com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nano_tablet.nanotabletrfid.mainApp.domain.models.Token
import com.nano_tablet.nanotabletrfid.mainApp.domain.repository.TokenRepository
import com.nano_tablet.nanotabletrfid.util.Event
import com.nano_tablet.nanotabletrfid.util.EventBus.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * Handles acquisition, validation and persistence of the API token.
 *
 * State machine:
 *  - Idle → Loading → Success | Error
 *  - [verifyAndRefreshTokenIfNeeded] will try to load a cached token and refresh it if expired.
 *
 */
@HiltViewModel
class TokenViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    @ApplicationContext private val appContext: Context,
) : ViewModel() {
    private val _tokenStateFlow = MutableStateFlow<TokenViewState>(
        value = TokenViewState.Idle
    )

    val stateFlow: StateFlow<TokenViewState> = _tokenStateFlow
    private val tag = "Token"

    private fun saveToken(token: Token) {
        val sharedPreferences = appContext.getSharedPreferences("TOKEN_PREFS", Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString("token", token.accessToken)
            putString("expiresAt", token.expiresAt)
            apply()
        }
        Log.i("Token", "Token saved")
    }

    private fun loadToken(): Pair<String?, String?> {
        val sharedPreferences = appContext.getSharedPreferences("TOKEN_PREFS", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        val expiresAt = sharedPreferences.getString("expiresAt", null)
        return Pair(token, expiresAt)
    }


    private fun isTokenValid(tokenExpiration: String): Boolean {

        return try {
            val formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.systemDefault())
            //val timeNow = LocalDateTime.now()
            val tokenExpirationTime = LocalDateTime.parse(tokenExpiration, formatter)
            LocalDateTime.now().isBefore(tokenExpirationTime)
        } catch (e: Exception) {
            Log.i("$tag error", "Error parsing token expiration time: $e")
            false
        }
    }


    private fun fetchToken() = viewModelScope.launch {
        Log.i(tag, "Fetching token function")
        _tokenStateFlow.update { return@update TokenViewState.Loading }
        tokenRepository.fetchToken()
            .onRight { token ->
                _tokenStateFlow.update {
                    return@update TokenViewState.Success(
                        token = token,
                        isValid = true
                    )
                }
                sendEvent(Event.Toast("Token created"))
                Log.i(tag, "New token created, it expires: ${token.expiresAt}")
                saveToken(token)
            }
            .onLeft { error ->
                _tokenStateFlow.update {
                    return@update TokenViewState.Error(error.error.message)
                }
                sendEvent(
                    Event.AlertDialog(
                        "$tag Error",
                        "${error.error}\n${error.t?.message}"
                    )
                )
                Log.i("$tag Error", error.toString())
            }
    }

    fun deleteToken() = viewModelScope.launch {
        val sharedPreferences = appContext.getSharedPreferences("TOKEN_PREFS", Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString("token", null)
            putString("expiresAt", null)
            apply()
        }
        _tokenStateFlow.update {
            return@update TokenViewState.Idle
        }
        Log.i("Token", "Token deleted")
        sendEvent(
            Event.AlertDialog(
                "$tag info",
                "Token deleted"
            )
        )
    }


    /**
     * Loads a persisted token and validates it; refreshes if missing/expired.
     * Emits Success immediately when a valid cached token is present.
     */
    fun verifyAndRefreshTokenIfNeeded() = viewModelScope.launch {
        Log.i(tag, "Verifying token")
        val (loadedToken, loadedExpiresAt) = loadToken()
        val isTokenInvalid =
            loadedToken == null || (loadedExpiresAt != null
                    && !isTokenValid(loadedExpiresAt))
        if (isTokenInvalid) {
            Log.i(tag, "Token is invalid. Refreshing...")
            sendEvent(Event.Toast("Refreshing token"))
            fetchToken()
        } else {
            val token = loadedExpiresAt?.let { Token (it,loadedExpiresAt) }
            _tokenStateFlow.update {
                return@update TokenViewState.Success(
                    token = token,
                    isValid = true
                )
            }
            Log.i(tag, "Token is valid")
            sendEvent(Event.Toast("Token is OK"))
        }
    }
}

