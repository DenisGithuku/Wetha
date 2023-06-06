package com.githukudenis.wetha

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githukudenis.feature_weather_info.common.DispatcherProvider
import com.githukudenis.feature_weather_info.data.local.LocationClient
import com.githukudenis.feature_weather_info.data.repository.Theme
import com.githukudenis.feature_weather_info.data.repository.UserPrefsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class MainViewModel(
    private val userPrefsRepository: UserPrefsRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val locationClient: LocationClient
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
        }
    }

    private fun getUserPrefs() {
        viewModelScope.launch {
            userPrefsRepository.userPrefs.collectLatest { prefs ->
                if (prefs.location == null) {
                    locationClient.locationData
                        .distinctUntilChanged()
                        .collectLatest { userLocation ->
                                userPrefsRepository.changeLocation(
                                    Pair(
                                        userLocation.latitude,
                                        userLocation.longitude
                                    )
                                )
                            }
                }
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
