package com.githukudenis.feature_weather_info.data.local

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationClient {
    suspend fun getCurrentLocationData(): Flow<Location>

    class LocationException(message: String): Exception()
}