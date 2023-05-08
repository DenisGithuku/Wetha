package com.githukudenis.wetha

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githukudenis.feature_weather_info.data.local.LocationClient
import com.githukudenis.feature_weather_info.data.repository.Theme
import com.githukudenis.feature_weather_info.data.repository.Units
import com.githukudenis.feature_weather_info.data.repository.UserPrefsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val userPrefsRepository: UserPrefsRepository,
    private val locationClient: LocationClient,
) : ViewModel() {
    var appState = MutableStateFlow(AppState())
        private set

    init {
        getUserPrefs()
    }

    fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.ChangeAppTheme -> {
                changeAppTheme(event.newTheme)
            }

            is MainEvent.ChangeUnits -> {
                if (appState.value.units == event.newUnits) {
                    return
                }
                changeUnits(event.newUnits)
            }
        }
    }

    private fun getUserPrefs() {
        viewModelScope.launch {
            userPrefsRepository.userPrefs.collectLatest { prefs ->
                appState.update { oldState ->
                    oldState.copy(
                        appTheme = prefs.theme ?: Theme.LIGHT,
                        units = prefs.units
                    )
                }
            }
        }
    }

    private fun changeAppTheme(newTheme: Theme) {
        viewModelScope.launch {
            userPrefsRepository.changeTheme(newTheme)
        }
    }

    private fun changeUnits(units: Units) {
        viewModelScope.launch {
            userPrefsRepository.changeUnits(units)
        }
    }
}
