package com.githukudenis.feature_weather_info.data.repository

import com.githukudenis.feature_weather_info.common.DispatcherProvider
import com.githukudenis.feature_weather_info.common.Resource
import com.githukudenis.feature_weather_info.data.api.OpenWeatherApi
import com.githukudenis.feature_weather_info.data.model.LocationInfoResponse
import com.githukudenis.feature_weather_info.data.model.WeatherResponse
import com.githukudenis.feature_weather_info.domain.WeatherRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

class RemoteWeatherDataSource(
    private val openWeatherApi: OpenWeatherApi,
    private val userPrefsRepository: UserPrefsRepository,
    private val dispatcherProvider: DispatcherProvider
) : WeatherRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getCurrentWeather(
    ): Flow<Resource<WeatherResponse>> {
        return userPrefsRepository.userPrefs.flatMapLatest { userPrefs ->
            flow {
                try {
                    emit(Resource.Loading())
                    val response =
                        openWeatherApi.getCurrentWeatherAndForecastData(
                            lat = checkNotNull(userPrefs.latitude),
                            lon = checkNotNull(userPrefs.longitude)
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
            }
        }.flowOn(dispatcherProvider.ioDispatcher)
    }

    override suspend fun getCurrentLocationInfo(): Flow<Resource<LocationInfoResponse>> {
        return flow {
            try {
                emit(Resource.Loading())
                userPrefsRepository.userPrefs.collectLatest { prefs ->
                    val res =
                        prefs.latitude?.let {
                            prefs.longitude?.let { it1 ->
                                openWeatherApi.getCurrentLocationInfo(
                                    lat = it,
                                    lon = it1
                                )
                            }
                        }
                    res?.let { response ->
                        emit(Resource.Success(response))
                    }
                }
            } catch (e: Exception) {
                emit(Resource.Error(message = e.message ?: "An unknown error occurred"))
            }
        }.flowOn(dispatcherProvider.ioDispatcher)
    }
}