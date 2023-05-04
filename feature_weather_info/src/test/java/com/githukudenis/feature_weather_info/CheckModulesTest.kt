package com.githukudenis.feature_weather_info

import com.githukudenis.feature_weather_info.di.weatherModule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.verify.verify

class CheckModulesTest: KoinTest {
    @Test
    fun checkAllModules() {
        weatherModule.verify()
    }
}