package com.githukudenis.feature_weather_info.ui.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githukudenis.feature_weather_info.common.Resource
import com.githukudenis.feature_weather_info.data.local.LocationClient
import com.githukudenis.feature_weather_info.data.repository.UserPrefsRepository
import com.githukudenis.feature_weather_info.domain.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class TodayViewModel(
    private val weatherRepository: WeatherRepository,
    private val locationClient: LocationClient,
    private val userPrefsRepository: UserPrefsRepository
) : ViewModel() {

    private var _state = MutableStateFlow<String>("")
    val state: StateFlow<String> get() = _state

    init {
        getUserLocation()
        getCurrentWeatherData()
    }

    private fun getUserLocation() {
        viewModelScope.launch {
            locationClient.getCurrentLocationData().collectLatest { storedLocation ->
                userPrefsRepository.userPrefs.collectLatest { userPrefs ->
                    if (userPrefs.latitude != storedLocation.latitude || userPrefs.longitude != storedLocation.longitude) {
                        userPrefsRepository.changeUserLocation(storedLocation)
                    }
                }
            }
        }
    }

    private fun getCurrentWeatherData() {
        viewModelScope.launch {
            weatherRepository.getCurrentWeather()
                .collectLatest { res ->
                    when (res) {
                        is Resource.Error -> {
                            Timber.e(message = res.errorMessage)
                        }

                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            _state.value = res.data?.timezone ?: ""
                            Timber.i(message = res.data.toString())
                        }
                    }
                }
        }
    }
}