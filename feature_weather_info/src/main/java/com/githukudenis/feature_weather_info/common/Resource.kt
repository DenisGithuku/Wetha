package com.githukudenis.feature_weather_info.common

sealed class Resource <T>(val data: T? = null, val errorMessage: String? = null) {
    class Loading<T>(result: T? = null): Resource<T>(data = result)
    class Success<T>(result: T): Resource<T>(data = result)
    class Error<T>(result: T? = null, message: String): Resource<T>(data = result, errorMessage = message)
}