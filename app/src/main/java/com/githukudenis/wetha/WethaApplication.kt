package com.githukudenis.wetha

import android.app.Application
import com.githukudenis.feature_weather_info.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class WethaApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        //configure timber
        Timber.plant(Timber.DebugTree())

        //start koin
        startKoin {
            androidLogger()
            androidContext(this@WethaApplication)
            modules(appModule)
        }
    }
}