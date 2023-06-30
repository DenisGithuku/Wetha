package com.githukudenis.feature_weather_info.data.repository

import kotlinx.coroutines.flow.Flow

interface UserPrefsRepository {
    val userPrefs: Flow<UserPrefs>
    suspend fun changeTheme(theme: Theme)

    suspend fun changeUnits(units: Units)

    suspend fun changeLocation(location: Pair<Double, Double>)

    suspend fun toggleUpdateReminders(enabled: Boolean)
}

data class UserPrefs(
    val theme: Theme? = Theme.LIGHT,
    val units: Units? = null,
    val location: Pair<Double, Double>? = null,
    val updatesEnabled: Boolean = false,
)

enum class Theme {
    LIGHT,
    DARK,
    SYSTEM
}
