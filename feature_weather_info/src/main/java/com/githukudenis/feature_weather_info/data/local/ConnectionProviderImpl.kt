package com.githukudenis.feature_weather_info.data.local

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.githukudenis.feature_weather_info.common.DispatcherProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn

class ConnectionProviderImpl(
    context: Context,
    private val dispatcherProvider: DispatcherProvider
) : ConnectionProvider {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    override val networkStatus: Flow<NetworkStatus>
        get() = callbackFlow {
            val networkRequest = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build()


            val connectionCallback = object : NetworkCallback() {
                override fun onAvailable(network: Network) {
                    trySend(NetworkStatus.Connected)
                }

                override fun onLost(network: Network) {
                    trySend(NetworkStatus.Disconnected)
                }

                override fun onUnavailable() {
                    trySend(NetworkStatus.Disconnected)
                }
            }

            connectivityManager.registerNetworkCallback(networkRequest, connectionCallback)

            awaitClose {
                connectivityManager.unregisterNetworkCallback(connectionCallback)
            }
        }
            .distinctUntilChanged()
            .flowOn(dispatcherProvider.ioDispatcher)
}