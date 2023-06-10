package com.githukudenis.feature_weather_info.ui.full_report

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githukudenis.feature_weather_info.common.MessageType
import com.githukudenis.feature_weather_info.common.Resource
import com.githukudenis.feature_weather_info.common.UserMessage
import com.githukudenis.feature_weather_info.data.local.ConnectionProvider
import com.githukudenis.feature_weather_info.data.local.NetworkStatus
import com.githukudenis.feature_weather_info.data.repository.Units
import com.githukudenis.feature_weather_info.data.repository.UserPrefsRepository
import com.githukudenis.feature_weather_info.domain.WeatherRepository
import com.githukudenis.feature_weather_info.ui.today.TodayScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DailyWeatherViewModel(
    private val userPrefsRepository: UserPrefsRepository,
    private val connectionProvider: ConnectionProvider,
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _state: MutableStateFlow<DailyUpdateState> =
        MutableStateFlow(DailyUpdateState.Loading)
    val state: StateFlow<DailyUpdateState> get() = _state.asStateFlow()

    fun getUpdates() {
        viewModelScope.launch {
            connectionProvider.networkStatus.collectLatest { connectionState ->
                when (connectionState) {
                    NetworkStatus.Connected -> {
                        userPrefsRepository.userPrefs
                            .distinctUntilChanged()
                            .collectLatest { prefs ->
                                prefs.location?.let { loc ->
                                    val location = Location("").apply {
                                        latitude = loc.first
                                        longitude = loc.second
                                    }

                                    getDailyUpdateWeatherData(
                                        location,
                                        units = prefs.units ?: Units.STANDARD
                                    )
                                }
                            }
                    }


                    NetworkStatus.Disconnected -> {
                        val userMessage = UserMessage(
                            id = 0,
                            description = "Connection unavailable. Please try connecting again!",
                            messageType = MessageType.ERROR
                        )

                        val userMessages = TodayScreenState.Error().userMessages.toMutableList()
                            .apply {
                                add(userMessage)
                            }
                        _state.update {
                            DailyUpdateState.Error(
                                userMessages = userMessages
                            )
                        }
                    }

                    NetworkStatus.Unknown -> {
                        val userMessage = UserMessage(
                            id = 0,
                            description = "Connection unavailable. Please try connecting again!",
                            messageType = MessageType.ERROR
                        )

                        val userMessages = DailyUpdateState.Error().userMessages.toMutableList()
                            .apply {
                                add(userMessage)
                            }
                        _state.update {
                            DailyUpdateState.Error(
                                userMessages = userMessages
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun getDailyUpdateWeatherData(
        location: Location,
        units: Units
    ) {
        weatherRepository.getDailyUpdates(location, units)
            .collect { result ->
                when (result) {
                    is Resource.Error -> {
                        val userMessage = UserMessage(
                            id = 0,
                            description = result.errorMessage
                                ?: "An unknown error occurred. Please try again",
                            messageType = MessageType.ERROR
                        )
                        val userMessages = DailyUpdateState.Error().userMessages.toMutableList()
                            .apply {
                                add(userMessage)
                            }
                        _state.update {
                            DailyUpdateState.Error(
                                userMessages = userMessages
                            )
                        }
                    }

                    is Resource.Loading -> {
                        _state.update {
                            DailyUpdateState.Loading
                        }
                    }

                    is Resource.Success -> {
                        _state.update {
                            DailyUpdateState.Loaded(
                                state = DailyUpdateUiState(result.data?.daily ?: emptyList())
                            )
                        }
                    }
                }
            }
    }
}