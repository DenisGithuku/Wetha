package com.githukudenis.feature_weather_info.data.local

import kotlinx.coroutines.flow.Flow

interface ConnectionProvider {
    val networkStatus: Flow<NetworkStatus>
}

enum class NetworkStatus {
    Connected,
    Disconnected,
    Unknown
}