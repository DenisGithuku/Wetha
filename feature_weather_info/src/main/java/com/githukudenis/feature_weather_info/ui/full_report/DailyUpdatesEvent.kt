package com.githukudenis.feature_weather_info.ui.full_report

sealed class DailyUpdatesEvent {
    data class OnRetry(val id: Int): DailyUpdatesEvent()
}
