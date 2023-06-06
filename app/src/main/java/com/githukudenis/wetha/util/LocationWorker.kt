package com.githukudenis.wetha.util

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.githukudenis.feature_weather_info.common.DispatcherProvider
import com.githukudenis.feature_weather_info.data.local.LocationClient
import com.githukudenis.feature_weather_info.data.repository.UserPrefsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent

class LocationListenerWorker(
    context: Context,
    params: WorkerParameters,
    private val userPrefsRepository: UserPrefsRepository,
    private val locationClient: LocationClient,
    private val dispatcherProvider: DispatcherProvider
) : CoroutineWorker(context, params), KoinComponent {
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun doWork(): Result {
        return try {
            userPrefsRepository
                .userPrefs
                .mapLatest {
                    it.location
                }
                .collect { storedLocation ->
                    locationClient.locationData
                        .collectLatest { preciseLocation ->
                            val preciseLocationPair =
                                Pair(preciseLocation.latitude, preciseLocation.longitude)
                            if (preciseLocationPair != storedLocation) {
                                withContext(dispatcherProvider.ioDispatcher) {
                                    userPrefsRepository.changeLocation(preciseLocationPair)
                                }
                            }
                        }
                }
            Result.success()
        } catch (err: Exception) {
            Result.failure(
                workDataOf("location_error" to err.message)
            )
        }
    }
}