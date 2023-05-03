package com.githukudenis.feature_weather_info.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

data class DispatcherProvider (
    val ioDispatcher: CoroutineDispatcher,
    val mainDispatcher: CoroutineDispatcher,
    val defaultDispatcher: CoroutineDispatcher,
    val unconfinedDispatcher: CoroutineDispatcher,
)
