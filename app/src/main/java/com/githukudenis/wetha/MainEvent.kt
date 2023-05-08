package com.githukudenis.wetha

import com.githukudenis.feature_weather_info.data.repository.Theme
import com.githukudenis.feature_weather_info.data.repository.Units

sealed class MainEvent {
    data class ChangeAppTheme(val newTheme: Theme): MainEvent()
    data class ChangeUnits(val newUnits: Units): MainEvent()
}