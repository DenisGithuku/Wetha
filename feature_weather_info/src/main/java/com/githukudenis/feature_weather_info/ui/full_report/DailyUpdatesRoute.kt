package com.githukudenis.feature_weather_info.ui.full_report

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.text.buildSpannedString
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.githukudenis.feature_weather_info.R
import com.githukudenis.feature_weather_info.data.model.Daily
import com.githukudenis.feature_weather_info.data.repository.Units
import com.githukudenis.feature_weather_info.ui.today.components.JumpingBubblesIndicator
import com.githukudenis.feature_weather_info.util.WeatherIconMapper
import okhttp3.internal.wait
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

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
                dailyUpdates = currentState.state.daily,
                selectedUnits = currentState.state.units
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
    dailyUpdates: List<Daily>,
    selectedUnits: Units
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .systemBarsPadding()
            .padding(vertical = 16.dp, horizontal = 8.dp)
    ) {
        items(items = dailyUpdates) {
            WeatherItem(daily = it, selectedUnits = selectedUnits)
        }
    }
}

@Composable
private fun WeatherItem(
    daily: Daily,
    selectedUnits: Units
) {
    val formatter = DateTimeFormatter
        .ofPattern("MMM dd", Locale.getDefault())
        .apply {
            withZone(ZoneId.systemDefault())
        }
    val currentTime = LocalDateTime
        .ofInstant(
            Instant.ofEpochMilli(daily.dt * 1_000L),
            ZoneId.systemDefault()
        )
    val date = when(currentTime.dayOfMonth) {
        LocalDateTime.now().dayOfMonth -> {
            "Today"
        }
        LocalDateTime.now().dayOfMonth + 1 -> {
            "Tomorrow"
        }
        else -> {
            formatter.format(currentTime)
        }
    }

    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = date,
                style = MaterialTheme.typography.headlineMedium
            )
            val icon = daily.weather[0].icon.run {
                WeatherIconMapper.icons.find {
                    it.first == this
                }
            }?.second
            icon?.let { ic ->
                Image(
                    painter = painterResource(id = ic),
                    contentDescription = "Weather icon",
                    modifier = Modifier.size(15.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Text(
                text = daily.weather[0].main,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = buildAnnotatedString {
                    append(daily.temp.day.toString())
                    withStyle(SpanStyle(baselineShift = BaselineShift.Superscript)) {
                        append("o")
                    }
                },
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "${daily.wind_speed} km/h"
            )
        }
    }
}

@Composable
private fun Error() {

}