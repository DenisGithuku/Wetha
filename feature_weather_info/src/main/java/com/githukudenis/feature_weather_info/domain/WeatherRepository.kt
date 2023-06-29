package com.githukudenis.feature_weather_info.domain

import android.location.Location
import com.githukudenis.feature_weather_info.common.Resource
import com.githukudenis.feature_weather_info.data.api.model.LocationInfoResponse
import com.githukudenis.feature_weather_info.data.api.model.CurrentWeatherResponse
import com.githukudenis.feature_weather_info.data.api.model.DailyWeatherResponse
import com.githukudenis.feature_weather_info.data.repository.Units
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getCurrentData(location: Location, units: Units): Flow<Resource<CurrentWeatherResponse>>

    suspend fun getDailyUpdates(location: Location, units: Units): Flow<Resource<DailyWeatherResponse>>

    suspend fun getCurrentLocationInfo(location: Location): Flow<Resource<LocationInfoResponse>>
}