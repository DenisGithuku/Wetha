package com.githukudenis.feature_weather_info.domain

import com.githukudenis.feature_weather_info.common.Resource
import com.githukudenis.feature_weather_info.data.model.LocationInfoResponse
import com.githukudenis.feature_weather_info.data.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getCurrentWeather(): Flow<Resource<WeatherResponse>>

    suspend fun getCurrentLocationInfo(): Flow<Resource<LocationInfoResponse>>
}