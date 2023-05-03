package com.githukudenis.feature_weather_info.data.repository

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface UserPrefsRepository {
    val userPrefs: Flow<UserPrefs>
    suspend fun changeUserLocation(location: Location)
}

data class UserPrefs(
    val latitude: Double?,
    val longitude: Double?
)

