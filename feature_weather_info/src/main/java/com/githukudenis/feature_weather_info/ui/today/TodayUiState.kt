package com.githukudenis.feature_weather_info.ui.today

import java.time.LocalDateTime

data class TodayUiState(
    val isLoading: Boolean = false,
    val locationState: LocationState = LocationState(),
    val currentWeatherState: CurrentWeatherState = CurrentWeatherState(),
    val todayForeCastState: TodayForeCastState = TodayForeCastState()
)

data class LocationState(
    val isLoading: Boolean = false,
    val name: String? = null,
    val date: LocalDateTime = LocalDateTime.now(),
    val errorMessage: String? = null
)

data class CurrentWeatherState(
    val isLoading: Boolean = false,
    val icon: String? = null,
    val temperature: Double? = null,
    val main: String? = null,
    val description: String? = null,
    val windSpeed: Double? = null,
    val humidity: Int? = null,
    val errorMessage: String? = null
)

data class TodayForeCastState(
    val isLoading: Boolean = false,
    val foreCast: List<ForeCast> = emptyList()
)

data class ForeCast(
    val icon: String? = null,
    val time: String? = null,
    val temperature: Double? = null
)