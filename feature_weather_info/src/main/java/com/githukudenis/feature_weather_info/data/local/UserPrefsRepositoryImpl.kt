package com.githukudenis.feature_weather_info.data.local

import android.content.Context
import android.location.Location
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.githukudenis.feature_weather_info.common.Constants
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
            val latitude = prefs[PreferenceKeys.latitude]
            val longitude = prefs[PreferenceKeys.longitude]
            UserPrefs(latitude, longitude)
        }

    override suspend fun changeUserLocation(location: Location) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.latitude] = location.latitude
            prefs[PreferenceKeys.longitude] = location.longitude
        }
    }
}

private object PreferenceKeys {
    val latitude = doublePreferencesKey("latitude")
    val longitude = doublePreferencesKey("longitude")
}