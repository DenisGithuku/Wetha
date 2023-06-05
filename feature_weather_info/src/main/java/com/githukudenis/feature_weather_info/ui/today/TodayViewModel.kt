package com.githukudenis.feature_weather_info.ui.today

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githukudenis.feature_weather_info.common.MessageType
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

    private val todayUiState = MutableStateFlow(TodayUiState())
    var state = MutableStateFlow<TodayScreenState>(TodayScreenState.Loading())
        private set

    init {
        initialize()
    }

    private fun initialize() {
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
                        state.update {
                            TodayScreenState.Loading(shouldAskForUnits = true)
                        }
                    } else {
                        getLocationInfo(it.first)
                        getCurrentWeatherData(it.first, units = it.second ?: Units.STANDARD)
                    }
                }
        }
    }

    fun onEvent(event: TodayUiEvent) {
        when (event) {
            is TodayUiEvent.OnShowUserMessage -> {
                clearUserMessage(event.messageId, event.messageType)
            }

            is TodayUiEvent.ChangeUnits -> {
                viewModelScope.launch {
                    userPrefsRepository.changeUnits(event.units).also {
                        todayUiState.update { oldUiState ->
                            oldUiState.copy(
                                selectedUnits = event.units,
                                shouldAskForUnits = !todayUiState.value.shouldAskForUnits
                            )
                        }
                    }
                }
            }

            is TodayUiEvent.Retry -> {
                initialize()
            }
        }
    }

    private fun clearUserMessage(messageId: Int, messageType: MessageType) {
        when (messageType) {
            MessageType.STANDARD -> {
                val userMessages =
                    todayUiState.value.userMessages.filterNot { userMessage -> userMessage.id == messageId }
                todayUiState.update { oldUiState ->
                    oldUiState.copy(userMessages = userMessages)
                }
            }

            MessageType.ERROR -> {
                val userMessages = TodayScreenState.Error().userMessages.filterNot {
                    it.id == messageId
                }

                TodayScreenState.Error(
                    userMessages = userMessages
                )
            }
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
                                description = result.errorMessage,
                                messageType = MessageType.ERROR
                            )

                            val userMessages = TodayScreenState.Error().userMessages.toMutableList()
                                .apply {
                                    add(userMessage)
                                }
                            state.update {
                                TodayScreenState.Error(
                                    userMessages = userMessages
                                )
                            }
                        }

                        is Resource.Loading -> {
                            state.update { TodayScreenState.Loading() }
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
                                todayUiState.update { oldUiState ->
                                    oldUiState.copy(
                                        currentWeatherState = currentWeatherState,
                                        hourlyForeCastState = hourlyForeCastState
                                    )
                                }
                                TodayScreenState.Loaded(
                                    todayUiState.value
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
                            description = result.errorMessage,
                            messageType = MessageType.ERROR
                        )
                        val userMessages = mutableListOf<UserMessage>().apply {
                            this.add(userMessage)
                        }

                        state.update {
                            TodayScreenState.Error(userMessages)
                        }
                    }

                    is Resource.Loading -> {
                        state.update { TodayScreenState.Loading() }
                    }

                    is Resource.Success -> {
                        state.update {
                            val locationState = LocationState(result.data?.first()?.name)
                            todayUiState.update { oldUiState -> oldUiState.copy(locationState = locationState) }
                            TodayScreenState.Loaded(todayUiState.value)
                        }
                    }
                }
            }
        }
    }
}