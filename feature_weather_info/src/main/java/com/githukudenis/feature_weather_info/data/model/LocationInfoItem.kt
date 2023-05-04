package com.githukudenis.feature_weather_info.data.model

data class LocationInfoItem(
    val country: String,
    val lat: Double,
    val local_names: LocalNames,
    val lon: Double,
    val name: String,
    val state: String
)