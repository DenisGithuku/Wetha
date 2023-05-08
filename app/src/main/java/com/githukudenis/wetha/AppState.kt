package com.githukudenis.wetha

import android.location.Location
import com.githukudenis.feature_weather_info.data.repository.Theme
import com.githukudenis.feature_weather_info.data.repository.Units

data class AppState(
    val units: Units? = null,
    val appTheme: Theme = Theme.LIGHT
)
