package com.githukudenis.feature_weather_info.data.model

import kotlinx.serialization.Serializable


data class Temp(
    val day: Double,
    val eve: Double,
    val max: Double,
    val min: Double,
    val morn: Double,
    val night: Double
)