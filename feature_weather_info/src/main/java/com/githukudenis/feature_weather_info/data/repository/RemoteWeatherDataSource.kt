package com.githukudenis.feature_weather_info.data.repository

import android.location.Location
import com.githukudenis.feature_weather_info.common.DispatcherProvider
import com.githukudenis.feature_weather_info.common.Resource
import com.githukudenis.feature_weather_info.data.api.OpenWeatherApi
import com.githukudenis.feature_weather_info.data.model.LocationInfoResponse
import com.githukudenis.feature_weather_info.data.model.CurrentWeatherResponse
import com.githukudenis.feature_weather_info.data.model.DailyWeatherResponse
import com.githukudenis.feature_weather_info.domain.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

class RemoteWeatherDataSource(
    private val openWeatherApi: OpenWeatherApi,
    private val dispatcherProvider: DispatcherProvider
) : WeatherRepository {

    override suspend fun getCurrentData(
        location: Location,
        units: Units
    ): Flow<Resource<CurrentWeatherResponse>> {
        return flow {
            try {
                emit(Resource.Loading())
                val response =
                    openWeatherApi.getCurrentWeatherAndForecastData(
                        lat = location.latitude,
                        lon = location.longitude,
                        units = units.name,
                        exclude = "minutely,daily"
                    )
                Timber.i(response.toString())
                emit(Resource.Success(response))
            } catch (ioException: IOException) {
                emit(Resource.Error(message = ioException.message.toString()))
            } catch (httpException: HttpException) {
                emit(Resource.Error(message = httpException.message.toString()))
            } catch (exception: Exception) {
                emit(Resource.Error(message = exception.message.toString()))
            }
        }.flowOn(dispatcherProvider.ioDispatcher)
    }

    override suspend fun getDailyUpdates(
        location: Location,
        units: Units
    ): Flow<Resource<DailyWeatherResponse>> {
        return flow {
            try {
                emit(Resource.Loading())
                val response =
                    openWeatherApi.getDailyWeatherForecastData(
                        lat = location.latitude,
                        lon = location.longitude,
                        units = units.name
                    )
                Timber.i(response.toString())
                emit(Resource.Success(response))
            } catch (ioException: IOException) {
                emit(Resource.Error(message = ioException.message.toString()))
            } catch (httpException: HttpException) {
                emit(Resource.Error(message = httpException.message.toString()))
            } catch (exception: Exception) {
                emit(Resource.Error(message = exception.message.toString()))
            }
        }.flowOn(dispatcherProvider.ioDispatcher)
    }

    override suspend fun getCurrentLocationInfo(location: Location): Flow<Resource<LocationInfoResponse>> {
        return flow {
            try {
                emit(Resource.Loading())
                val response = openWeatherApi.getCurrentLocationInfo(
                    lat = location.latitude,
                    lon = location.longitude
                )
                emit(Resource.Success(response))
            } catch (e: Exception) {
                emit(Resource.Error(message = e.message ?: "An unknown error occurred"))
            }
        }.flowOn(dispatcherProvider.ioDispatcher)
    }
}