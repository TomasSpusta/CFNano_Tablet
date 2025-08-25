package com.nano_tablet.nanotabletrfid.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A wrapper around [DataStore] providing application-level preferences.
 *
 * Currently stores:
 * - [isFirstLaunch] → whether the app is being launched for the first time.
 *
 * This class uses Jetpack [DataStore] with a `Preferences` schema and exposes
 * values as cold [Flow]s, so consumers can collect them reactively.
 *
 * Usage:
 * ```
 * // Injected with Hilt
 * @Inject lateinit var appPreferences: AppPreferences
 *
 * lifecycleScope.launch {
 *     appPreferences.isFirstLaunch.collect { firstLaunch ->
 *         if (firstLaunch) { showOnboarding() }
 *     }
 * }
 *
 * // Later mark it as completed:
 * appPreferences.setFirstLaunchFalse()
 * ```
 */
@Singleton
class AppPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Extension property to lazily create a [DataStore] instance
     * bound to the application [Context].
     *
     * The data is stored in a file named `app_settings.preferences_pb`.
     */
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

    companion object {
        /** Preference key storing whether the app has been launched before. */
        private val KEY_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
    }

    /**
     * Flow that emits whether the app is being launched for the first time.
     *
     * Defaults to `true` if the preference has not yet been set.
     * - `true` → user is on their first launch.
     * - `false` → user has launched the app at least once before.
     */
    val isFirstLaunch: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[KEY_FIRST_LAUNCH] != false }

    /**
     * Marks that the app is no longer in its first launch state.
     *
     * This writes `false` into [DataStore] for [KEY_FIRST_LAUNCH].
     * Call this once onboarding or setup flow has been completed.
     */
    suspend fun setFirstLaunchFalse() {
        context.dataStore.edit { prefs ->
            prefs[KEY_FIRST_LAUNCH] = false
        }
    }
}