package com.nano_tablet.nanotabletrfid.mainApp.data.repository

import android.content.Context


import com.nano_tablet.nanotabletrfid.mainApp.domain.repository.TokenRepository


import dagger.hilt.android.qualifiers.ApplicationContext

import javax.inject.Inject
/**
 * Handles persistence and retrieval of authentication tokens.
 *
 * Currently implemented as a simple [SharedPreferences] wrapper storing:
 * - `"token"` → the bearer token string.
 * - `"expiresAt"` → optional expiration timestamp (as a string).
 *
 * Usage:
 * ```
 * val (token, expiresAt) = tokenHandling.loadToken()
 * if (token != null) {
 *     // safe to attach token to Authorization header
 * }
 * ```
 */
class TokenHandling @Inject constructor(
    @ApplicationContext private val appContext: Context,
) {
    /**
     * Loads the currently stored token and its expiry time from SharedPreferences.
     *
     * @return [Pair] where:
     *  - [Pair.first] = token string, or null if not set.
     *  - [Pair.second] = expiry timestamp string, or null if not set.
     */
    fun loadToken(): Pair<String?, String?> {
        val sharedPreferences = appContext.getSharedPreferences("TOKEN_PREFS", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        val expiresAt = sharedPreferences.getString("expiresAt", null)
        return Pair(token, expiresAt)
    }
}