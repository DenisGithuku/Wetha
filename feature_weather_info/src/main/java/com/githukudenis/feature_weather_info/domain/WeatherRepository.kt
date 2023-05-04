package com.githukudenis.feature_weather_info.domain

import android.location.Location
import com.githukudenis.feature_weather_info.common.Resource
import com.githukudenis.feature_weather_info.data.model.LocationInfoResponse
import com.githukudenis.feature_weather_info.data.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getCurrentWeather(location: Location): Flow<Resource<WeatherResponse>>

    suspend fun getCurrentLocationInfo(location: Location): Flow<Resource<LocationInfoResponse>>
}