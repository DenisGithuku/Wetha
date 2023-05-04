package com.githukudenis.wetha

import com.githukudenis.feature_weather_info.data.repository.Theme

sealed class MainEvent {
    class ChangeAppTheme(val newTheme: Theme): MainEvent()
}