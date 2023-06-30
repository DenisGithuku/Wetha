package com.githukudenis.feature_weather_info.data.local

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.githukudenis.feature_weather_info.common.Constants
import com.githukudenis.feature_weather_info.data.repository.Theme
import com.githukudenis.feature_weather_info.data.repository.Units
import com.githukudenis.feature_weather_info.data.repository.UserPrefs
import com.githukudenis.feature_weather_info.data.repository.UserPrefsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

class UserPrefsRepositoryImpl(
    private val context: Context
) : UserPrefsRepository {

    private val Context.dataStore by preferencesDataStore(name = Constants.user_prefs)

    override val userPrefs: Flow<UserPrefs>
        get() = context.dataStore.data
            .distinctUntilChanged()
            .map { prefs ->
                val theme = prefs[PreferenceKeys.theme]?.let { Theme.valueOf(it) }
                val units = prefs[PreferenceKeys.units]?.let { Units.valueOf(it) }
                val location = prefs[PreferenceKeys.location]?.let {
                    Pair(
                        it.substringBefore(',').toDouble(),
                        it.substringAfter(",").toDouble()
                    )
                }
                val updatesEnabled = prefs[PreferenceKeys.updatesEnabled] ?: false
                UserPrefs(theme, units, location, updatesEnabled)
            }

    override suspend fun changeTheme(theme: Theme) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.theme] = theme.name
        }
    }

    override suspend fun changeUnits(units: Units) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.units] = units.name
        }
    }

    override suspend fun changeLocation(location: Pair<Double, Double>) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.location] = "${location.first},${location.second}"
        }
    }

    override suspend fun toggleUpdateReminders(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.updatesEnabled] = enabled

        }
        if (enabled) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, WeatherInfoReceiver::class.java)
                .apply {
                    putExtra("message", "Remember to check your weather updates")
                }
            val pendingIntent =
                PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_IMMUTABLE)

            alarmManager.cancel(pendingIntent)

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                LocalDateTime.now()
                    .withHour(8)
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0)
                    .toInstant(ZoneOffset.UTC)
                    .toEpochMilli(),
                24 * 60 * 60 * 1000,
                pendingIntent
            )
        }
    }
}

private object PreferenceKeys {
    val theme = stringPreferencesKey("theme")
    val units = stringPreferencesKey("units")
    val location = stringPreferencesKey("location")
    val updatesEnabled = booleanPreferencesKey("updatesEnabled")
}