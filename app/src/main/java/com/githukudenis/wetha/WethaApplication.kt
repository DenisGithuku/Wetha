package com.githukudenis.wetha

import android.app.Application
import timber.log.Timber

class WethaApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        //configure timber
        Timber.plant(Timber.DebugTree())
    }
}