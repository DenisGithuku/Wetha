package com.githukudenis.feature_weather_info.di

import com.githukudenis.feature_weather_info.common.Constants
import com.githukudenis.feature_weather_info.common.DispatcherProvider
import com.githukudenis.feature_weather_info.data.api.OpenWeatherApi
import com.githukudenis.feature_weather_info.data.local.DefaultLocationClient
import com.githukudenis.feature_weather_info.data.local.LocationClient
import com.githukudenis.feature_weather_info.data.local.UserPrefsRepositoryImpl
import com.githukudenis.feature_weather_info.data.repository.RemoteWeatherDataSource
import com.githukudenis.feature_weather_info.data.repository.UserPrefsRepository
import com.githukudenis.feature_weather_info.domain.WeatherRepository
import com.githukudenis.feature_weather_info.ui.today.TodayViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.gson.gson
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<FusedLocationProviderClient> {
        LocationServices.getFusedLocationProviderClient(androidContext())
    }

    single<LocationClient> {
        DefaultLocationClient(androidContext(), get())
    }

    single<WeatherRepository> {
        RemoteWeatherDataSource(get(), get(), get())
    }

    single<UserPrefsRepository> {
        UserPrefsRepositoryImpl(androidContext())
    }

    single {
        DispatcherProvider(
            ioDispatcher = Dispatchers.IO,
            mainDispatcher = Dispatchers.Main,
            defaultDispatcher = Dispatchers.Default,
            unconfinedDispatcher = Dispatchers.Unconfined
        )
    }

    viewModel {
        TodayViewModel(get(), get(), get())
    }

    single<OpenWeatherApi> {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                gson()
            }
            install(Logging) {
                level = LogLevel.HEADERS
                logger = Logger.DEFAULT
            }
        }
        val openWeatherApiService = Ktorfit.Builder()
            .baseUrl(Constants.baseUrl)
            .httpClient(client)
            .build()
            .create<OpenWeatherApi>()
        openWeatherApiService
    }

    single {
        DispatcherProvider(
            ioDispatcher = Dispatchers.IO,
            mainDispatcher = Dispatchers.Main,
            defaultDispatcher = Dispatchers.Default,
            unconfinedDispatcher = Dispatchers.Unconfined
        )
    }
}