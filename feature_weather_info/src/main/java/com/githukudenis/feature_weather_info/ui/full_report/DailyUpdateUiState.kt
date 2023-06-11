package com.githukudenis.feature_weather_info.ui.full_report

import com.githukudenis.feature_weather_info.common.UserMessage
import com.githukudenis.feature_weather_info.data.model.Daily
import com.githukudenis.feature_weather_info.data.model.FeelsLike
import com.githukudenis.feature_weather_info.data.model.Temp
import com.githukudenis.feature_weather_info.data.model.Weather
import com.githukudenis.feature_weather_info.data.repository.Units

data class DailyUpdateUiState(
    val daily: List<Daily> = emptyList(),
    val units: Units = Units.STANDARD
)

sealed class DailyUpdateState {
    object Loading : DailyUpdateState()

    data class Loaded(
        val state: DailyUpdateUiState = DailyUpdateUiState()
    ) : DailyUpdateState()

    data class Error(val userMessages: List<UserMessage> = listOf()) : DailyUpdateState()
}