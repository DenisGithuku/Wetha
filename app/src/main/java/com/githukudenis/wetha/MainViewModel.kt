package com.githukudenis.wetha

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githukudenis.feature_weather_info.data.local.LocationClient
import com.githukudenis.feature_weather_info.data.repository.Theme
import com.githukudenis.feature_weather_info.data.repository.UserPrefsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel(
    private val userPrefsRepository: UserPrefsRepository,
    private val locationClient: LocationClient,
) : ViewModel() {
    var appState = MutableStateFlow(AppState())
        private set

    init {
        getAppTheme()
    }

    fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.ChangeAppTheme -> {
                changeAppTheme(event.newTheme)
            }
        }
    }

    private fun getAppTheme() {
        viewModelScope.launch {
            userPrefsRepository.userPrefs.collectLatest { prefs ->
                appState.update { oldState ->
                    oldState.copy(
                        appTheme = prefs.theme ?: Theme.LIGHT
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
}
