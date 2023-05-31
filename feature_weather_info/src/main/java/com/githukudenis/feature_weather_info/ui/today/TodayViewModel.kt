package com.githukudenis.feature_weather_info.ui.today

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githukudenis.feature_weather_info.common.Resource
import com.githukudenis.feature_weather_info.common.UserMessage
import com.githukudenis.feature_weather_info.data.local.LocationClient
import com.githukudenis.feature_weather_info.data.repository.Units
import com.githukudenis.feature_weather_info.data.repository.UserPrefsRepository
import com.githukudenis.feature_weather_info.domain.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TodayViewModel(
    private val weatherRepository: WeatherRepository,
    private val userPrefsRepository: UserPrefsRepository,
    private val locationClient: LocationClient
) : ViewModel() {

    var state = MutableStateFlow(TodayUiState())
        private set

    init {
        viewModelScope.launch {
            combine(
                userPrefsRepository.userPrefs,
                locationClient.getCurrentLocationData()
            ) { prefs, location ->
                Pair(location, prefs.units)
            }
                .distinctUntilChanged()
                .collectLatest {
                    if (it.second == null) {
                        state.update { oldState ->
                            oldState.copy(shouldAskForUnits = true)
                        }
                    } else {
                        state.update { oldState ->
                            oldState.copy(selectedUnits = it.second)
                        }
                        getLocationInfo(it.first)
                        getCurrentWeatherData(it.first, units = it.second ?: Units.STANDARD)
                    }
                }
        }
    }

    fun onEvent(event: TodayUiEvent) {
        when (event) {
            is TodayUiEvent.OnShowUserMessage -> {
                clearUserMessage(event.messageId)
            }

            is TodayUiEvent.ChangeUnits -> {
                viewModelScope.launch {
                    userPrefsRepository.changeUnits(event.units).also {
                        state.update { oldState ->
                            oldState.copy(selectedUnits = event.units, shouldAskForUnits = false)
                        }
                    }
                }
            }
        }
    }

    private fun clearUserMessage(messageId: Int) {
        val userMessages =
            state.value.userMessages.filterNot { userMessage -> userMessage.id == messageId }
        state.update { oldState ->
            oldState.copy(userMessages = userMessages)
        }
    }

    private fun getCurrentWeatherData(location: Location, units: Units) {
        viewModelScope.launch {
            weatherRepository.getCurrentWeather(location, units)
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

    private fun getLocationInfo(location: Location) {
        viewModelScope.launch {
            weatherRepository.getCurrentLocationInfo(location).collect { result ->
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
                            val locationState = LocationState(result.data?.first()?.name)
                            oldState.copy(
                                locationState = locationState
                            )
                        }
                    }
                }
            }
        }
    }
}