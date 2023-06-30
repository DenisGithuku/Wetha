package com.githukudenis.feature_weather_info.data.api.model

import com.google.gson.annotations.SerializedName


data class Hourly(
    val clouds: Int,
    val dew_point: Double,
    @SerializedName("dt")
    val timestamp: Int,
    val feels_like: Double,
    val humidity: Int,
    val pop: Double,
    val pressure: Int,
    val rain: Rain,
    val temp: Double,
    val uvi: Double,
    val visibility: Int,
    val weather: List<Weather>,
    val wind_deg: Int,
    val wind_gust: Double,
    val wind_speed: Double
)