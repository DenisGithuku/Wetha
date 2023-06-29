package com.githukudenis.feature_weather_info.data.api.model

import com.google.gson.annotations.SerializedName


data class Current(
    val clouds: Int,
    val dew_point: Double,
    @SerializedName("dt")
    val timestamp: Int,
    val feels_like: Double,
    val humidity: Int,
    val pressure: Int,
    val sunrise: Int,
    val sunset: Int,
    val temp: Double,
    val uvi: Double,
    val visibility: Int,
    val weather: List<Weather>,
    val wind_deg: Int,
    val wind_speed: Double
)