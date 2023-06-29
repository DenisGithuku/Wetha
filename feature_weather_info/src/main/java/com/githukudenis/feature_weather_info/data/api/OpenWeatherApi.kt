package com.githukudenis.feature_weather_info.data.api

import com.githukudenis.feature_weather_info.BuildConfig
import com.githukudenis.feature_weather_info.data.api.model.LocationInfoResponse
import com.githukudenis.feature_weather_info.data.api.model.CurrentWeatherResponse
import com.githukudenis.feature_weather_info.data.api.model.DailyWeatherResponse
import com.githukudenis.feature_weather_info.data.repository.Units
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface OpenWeatherApi {
    @GET("data/3.0/onecall")
    suspend fun getCurrentWeatherAndForecastData(
        @Query("appid") appId: String = BuildConfig.appId,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = Units.STANDARD.name,
        @Query("exclude") exclude: String = "minutely,daily"
    ): CurrentWeatherResponse

    @GET("data/3.0/onecall")
    suspend fun getDailyWeatherForecastData(
        @Query("appid") appId: String = BuildConfig.appId,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = Units.STANDARD.name
    ): DailyWeatherResponse

    @GET("geo/1.0/reverse")
    suspend fun getCurrentLocationInfo(
        @Query("appid") appId: String = BuildConfig.appId,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): LocationInfoResponse
}