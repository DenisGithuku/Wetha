package com.githukudenis.feature_weather_info.data.local

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.githukudenis.feature_weather_info.common.DispatcherProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class DefaultLocationClient(
    private val context: Context,
    private val client: FusedLocationProviderClient,
    private val dispatcherProvider: DispatcherProvider
) : LocationClient {
    override val locationData: Flow<Location>
        @SuppressLint("MissingPermission")
        get() = callbackFlow {
            val locationRequest = LocationRequest.create()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    result.lastLocation?.let { res ->
                        launch {
                            send(res)
                        }
                    }
                }
            }

            client.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            awaitClose {
                client.removeLocationUpdates(locationCallback)
            }
        }.flowOn(dispatcherProvider.ioDispatcher)
}