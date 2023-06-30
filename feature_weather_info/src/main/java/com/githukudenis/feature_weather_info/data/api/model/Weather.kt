package com.githukudenis.feature_weather_info.data.api.model

import kotlinx.serialization.Serializable


data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)