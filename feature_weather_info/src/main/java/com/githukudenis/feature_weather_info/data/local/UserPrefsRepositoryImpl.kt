package com.githukudenis.feature_weather_info.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.githukudenis.feature_weather_info.common.Constants
import com.githukudenis.feature_weather_info.data.repository.Theme
import com.githukudenis.feature_weather_info.data.repository.Units
import com.githukudenis.feature_weather_info.data.repository.UserPrefs
import com.githukudenis.feature_weather_info.data.repository.UserPrefsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPrefsRepositoryImpl(
    private val context: Context
) : UserPrefsRepository {

    private val Context.dataStore by preferencesDataStore(name = Constants.user_prefs)

    override val userPrefs: Flow<UserPrefs>
        get() = context.dataStore.data.map { prefs ->
            val theme = prefs[PreferenceKeys.theme]?.let { Theme.valueOf(it) }
            val units = prefs[PreferenceKeys.units]?.let { Units.valueOf(it) }
            UserPrefs(theme, units)
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
}

private object PreferenceKeys {
    val theme = stringPreferencesKey("theme")
    val units = stringPreferencesKey("units")
}