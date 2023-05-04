package com.githukudenis.feature_weather_info.ui.today

sealed class TodayUiEvent {
    data class OnShowUserMessage(val messageId: Int): TodayUiEvent()
}
