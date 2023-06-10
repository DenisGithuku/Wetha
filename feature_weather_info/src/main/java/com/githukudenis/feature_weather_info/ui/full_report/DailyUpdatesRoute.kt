package com.githukudenis.feature_weather_info.ui.full_report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.githukudenis.feature_weather_info.R
import com.githukudenis.feature_weather_info.data.model.Daily
import com.githukudenis.feature_weather_info.ui.today.components.JumpingBubblesIndicator

@Composable
fun DailyUpdatesRoute(
    dailyWeatherViewModel: DailyWeatherViewModel
) {

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver {_ , event ->
            when(event) {
                Lifecycle.Event.ON_START -> {
                    dailyWeatherViewModel.getUpdates()
                }
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val uiState = dailyWeatherViewModel.state.collectAsStateWithLifecycle()

    when (val currentState = uiState.value) {
        is DailyUpdateState.Error -> {
            Error()
        }

        is DailyUpdateState.Loaded -> {
            Loaded(
                dailyUpdates = currentState.state.daily
            )
        }

        is DailyUpdateState.Loading -> {
            Loading()
        }
    }


}

@Composable
private fun Loading() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val context = LocalContext.current
        JumpingBubblesIndicator()
        Text(
            text = context.getString(R.string.loading_indicator),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun Loaded(
    dailyUpdates: List<Daily>
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items = dailyUpdates) {
            Text(
                text = "${it.temp}"
            )
        }
    }
}

@Composable
private fun Error() {

}