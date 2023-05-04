package com.githukudenis.feature_weather_info.data.repository

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface UserPrefsRepository {
    val userPrefs: Flow<UserPrefs>
    suspend fun changeTheme(theme: Theme)
}

data class UserPrefs(
    val theme: Theme? = Theme.LIGHT
)

enum class Theme {
    LIGHT,
    DARK,
    SYSTEM
}
