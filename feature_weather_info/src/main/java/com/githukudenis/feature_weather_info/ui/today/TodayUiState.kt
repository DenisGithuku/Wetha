package com.githukudenis.feature_weather_info.ui.today

import com.githukudenis.feature_weather_info.common.UserMessage
import com.githukudenis.feature_weather_info.data.repository.Units
import java.time.LocalDate


data class TodayUiState(
    val isLoading: Boolean = false,
    val shouldAskForUnits: Boolean = false,
    val selectedUnits: Units? = null,
    val locationState: LocationState = LocationState(),
    val currentWeatherState: CurrentWeatherState = CurrentWeatherState(),
    val hourlyForeCastState: HourlyForeCastState = HourlyForeCastState(),
    val userMessages: List<UserMessage> = emptyList()
)

data class LocationState(
    val name: String? = null,
    val date: LocalDate = LocalDate.now()
)

data class CurrentWeatherState(
    val icon: String? = null,
    val temperature: Double? = null,
    val main: String? = null,
    val description: String? = null,
    val windSpeed: Double? = null,
    val humidity: Int? = null,
)

data class HourlyForeCastState(
    val foreCast: List<ForeCast> = emptyList()
)

data class ForeCast(
    val icon: String? = null,
    val time: Int? = null,
    val temperature: Double? = null
)