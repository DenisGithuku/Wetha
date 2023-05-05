package com.githukudenis.feature_weather_info.data.repository

import kotlinx.coroutines.flow.Flow

interface UserPrefsRepository {
    val userPrefs: Flow<UserPrefs>
    suspend fun changeTheme(theme: Theme)

    suspend fun changeUnits(units: Units)
}

data class UserPrefs(
    val theme: Theme? = Theme.LIGHT,
    val units: Units? = Units.STANDARD
)

enum class Theme {
    LIGHT,
    DARK,
    SYSTEM
}
