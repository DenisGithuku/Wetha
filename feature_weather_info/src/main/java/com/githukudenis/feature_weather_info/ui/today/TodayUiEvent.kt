package com.githukudenis.feature_weather_info.ui.today

import com.githukudenis.feature_weather_info.data.repository.Units

sealed class TodayUiEvent {
    data class ChangeUnits(val units: Units): TodayUiEvent()
    data class OnShowUserMessage(val messageId: Int): TodayUiEvent()
}
