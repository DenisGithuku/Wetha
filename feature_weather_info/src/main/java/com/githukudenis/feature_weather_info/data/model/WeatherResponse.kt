package com.githukudenis.feature_weather_info.data.model

import kotlinx.serialization.Serializable


data class WeatherResponse(
    val current: Current,
    val daily: List<Daily>,
    val hourly: List<Hourly>,
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: Int
)