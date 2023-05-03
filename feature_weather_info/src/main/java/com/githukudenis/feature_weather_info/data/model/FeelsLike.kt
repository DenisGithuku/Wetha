package com.githukudenis.feature_weather_info.data.model

import kotlinx.serialization.Serializable


data class FeelsLike(
    val day: Double,
    val eve: Double,
    val morn: Double,
    val night: Double
)