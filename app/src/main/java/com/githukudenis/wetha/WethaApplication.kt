package com.githukudenis.wetha

import android.app.Application
import com.githukudenis.feature_weather_info.di.weatherModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import timber.log.Timber

class WethaApplication : Application(), KoinComponent {
    override fun onCreate() {
        super.onCreate()

        //configure timber
        Timber.plant(Timber.DebugTree())

        //start koin
        startKoin {
            androidLogger()
            androidContext(this@WethaApplication)
            workManagerFactory()
            modules(listOf(weatherModule, appModule))
        }
    }
}