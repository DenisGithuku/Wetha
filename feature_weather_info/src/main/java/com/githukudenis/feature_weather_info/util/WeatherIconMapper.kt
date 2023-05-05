package com.githukudenis.feature_weather_info.util

import android.content.Context
import com.githukudenis.feature_weather_info.R

class WeatherIconMapper {
    companion object {
        val icons = listOf(
            Pair("01d", R.drawable.day_clear),
            Pair("01n", R.drawable.night_clear),
            Pair("02d", R.drawable.clound_and_rain),
            Pair("02n", R.drawable.clound_and_rain),
            Pair("03d", R.drawable.cloudy),
            Pair("03n", R.drawable.cloudy),
            Pair("04d", R.drawable.cloudy),
            Pair("04n", R.drawable.cloudy),
            Pair("09d", R.drawable.clound_and_rain),
            Pair("09n", R.drawable.clound_and_rain),
            Pair("10d", R.drawable.rain),
            Pair("10n", R.drawable.night_rain),
            Pair("11d", R.drawable.thunder),
            Pair("11n", R.drawable.thunder),
            Pair("13d", R.drawable.snowy_bulk),
            Pair("13n", R.drawable.snowy_bulk),
        )
    }
}