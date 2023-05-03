package com.githukudenis.feature_weather_info.data.api

import com.githukudenis.feature_weather_info.BuildConfig
import com.githukudenis.feature_weather_info.data.model.LocationInfoResponse
import com.githukudenis.feature_weather_info.data.model.WeatherResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.plugins.logging.*

interface OpenWeatherApi {
    @GET("data/3.0/onecall")
    suspend fun getCurrentWeatherAndForecastData(
        @Query("appid") appId: String = BuildConfig.appId,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): WeatherResponse

    @GET("geo/1.0/reverse")
    suspend fun getCurrentLocationInfo(
        @Query("appid") appId: String = BuildConfig.appId,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): LocationInfoResponse
}