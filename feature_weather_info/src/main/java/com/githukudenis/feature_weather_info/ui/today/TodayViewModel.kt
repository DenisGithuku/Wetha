package com.githukudenis.feature_weather_info.ui.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githukudenis.feature_weather_info.common.Resource
import com.githukudenis.feature_weather_info.common.UserMessage
import com.githukudenis.feature_weather_info.data.local.LocationClient
import com.githukudenis.feature_weather_info.data.repository.UserPrefsRepository
import com.githukudenis.feature_weather_info.domain.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TodayViewModel(
    private val weatherRepository: WeatherRepository,
    private val locationClient: LocationClient,
    private val userPrefsRepository: UserPrefsRepository
) : ViewModel() {

    var state = MutableStateFlow(TodayUiState())
        private set

    init {
        getUserLocation()
        getCurrentWeatherData()
    }

    private fun getUserLocation() {
        viewModelScope.launch {
            locationClient.getCurrentLocationData()
                .distinctUntilChanged()
                .collectLatest { location ->
                    val latestUserLocation = Pair(location.latitude, location.longitude)
                    userPrefsRepository.userPrefs.collectLatest { userPrefs ->
                        val storeLocation = Pair(userPrefs.latitude, userPrefs.longitude)
                        if (latestUserLocation != storeLocation) {
                            userPrefsRepository.changeUserLocation(location)
                        }
                    }
                }
        }
    }

    private fun getCurrentWeatherData() {
        viewModelScope.launch {
            weatherRepository.getCurrentWeather()
                .collectLatest { result ->
                    when (result) {
                        is Resource.Error -> {
                            val userMessage = UserMessage(
                                id = 0,
                                description = result.errorMessage
                            )
                            val userMessages = mutableListOf<UserMessage>().apply {
                                this.add(userMessage)
                            }
                            state.update { oldState ->
                                oldState.copy(
                                    isLoading = false,
                                    userMessages = userMessages
                                )
                            }
                        }

                        is Resource.Loading -> {
                            state.update { oldState -> oldState.copy(isLoading = true) }
                        }

                        is Resource.Success -> {
                            state.update { oldState ->
                                val currentWeatherState = CurrentWeatherState(
                                    icon = result.data?.current?.weather?.get(0)?.icon,
                                    temperature = result.data?.current?.temp,
                                    windSpeed = result.data?.current?.wind_speed,
                                    humidity = result.data?.current?.humidity,
                                    main = result.data?.current?.weather?.get(0)?.main,
                                    description = result.data?.current?.weather?.get(0)?.description
                                )

                                val hourlyForeCastState = result.data?.hourly?.let { hourlyData ->
                                    HourlyForeCastState(
                                        foreCast = hourlyData.map { hourly ->
                                            ForeCast(
                                                icon = hourly.weather[0].icon,
                                                time = hourly.dt,
                                                temperature = hourly.temp
                                            )
                                        }
                                    )
                                } ?: HourlyForeCastState()
                                oldState.copy(
                                    isLoading = false,
                                    currentWeatherState = currentWeatherState,
                                    hourlyForeCastState = hourlyForeCastState
                                )
                            }
                        }
                    }
                }
        }
    }
}