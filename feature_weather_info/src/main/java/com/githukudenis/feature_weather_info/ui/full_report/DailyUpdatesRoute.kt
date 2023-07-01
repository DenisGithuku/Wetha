package com.githukudenis.feature_weather_info.ui.full_report

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.githukudenis.feature_weather_info.R
import com.githukudenis.feature_weather_info.common.UserMessage
import com.githukudenis.feature_weather_info.data.api.model.Daily
import com.githukudenis.feature_weather_info.data.repository.Units
import com.githukudenis.feature_weather_info.ui.today.TodayUiEvent
import com.githukudenis.feature_weather_info.ui.today.components.JumpingBubblesIndicator
import com.githukudenis.feature_weather_info.ui.today.components.WeatherInfoItem
import com.githukudenis.feature_weather_info.ui.today.generateUnits
import com.githukudenis.feature_weather_info.util.WeatherIconMapper
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun DailyUpdatesRoute(
    dailyWeatherViewModel: DailyWeatherViewModel
) {

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
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
            Error(error = currentState.userMessages.first()) {
                currentState.userMessages.first().id?.let { id ->
                    DailyUpdatesEvent.OnRetry(
                        id
                    )
                }?.let { dailyWeatherViewModel.onEvent(it) }
            }
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
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFE4EEF8),
                        Color.Transparent
                    )
                )
            )
            .padding(vertical = 16.dp, horizontal = 8.dp)
            .systemBarsPadding()
    ) {
        item {
            TomorrowWeatherItem(
                daily = dailyUpdates.first(),
                selectedUnits = selectedUnits
            )
        }
        item {
            Text(
                text = "In 7 days",
                style = MaterialTheme.typography.headlineMedium
            )
        }
        itemsIndexed(
            items = dailyUpdates.drop(2)
        ) { index, item ->
            WeatherItem(daily = item, selectedUnits = selectedUnits)
            if (index <= dailyUpdates.drop(2).size - 1) {
                Divider()
            }
        }
    }
}

@Composable
fun TomorrowWeatherItem(
    daily: Daily,
    selectedUnits: Units
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val icon = daily.weather.first().icon.run {
            WeatherIconMapper.icons.find {
                it.first == this
            }
        }?.second
        icon?.let { ic ->
            Image(
                painter = painterResource(id = ic),
                contentDescription = "Weather icon",
                modifier = Modifier.size(150.dp),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Tomorrow",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = daily.weather.first().main,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = buildAnnotatedString {
                    append("${daily.feels_like.day.roundToInt()}")
                    val formattedUnits = generateUnits(selectedUnits)
                    if (selectedUnits == Units.METRIC) {
                        withStyle(SpanStyle(baselineShift = BaselineShift.Superscript)) {
                            append("o")
                        }
                    }
                    append(formattedUnits.first)
                },
                style = MaterialTheme.typography.displayLarge
            )
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        WeatherInfoItem(
            title = "Temp",
            value = buildAnnotatedString {
                append("${daily.feels_like.day.roundToInt()}")
                val formattedUnits = generateUnits(selectedUnits)
                if (selectedUnits == Units.METRIC) {
                    withStyle(SpanStyle(baselineShift = BaselineShift.Superscript)) {
                        append("o")
                    }
                }
                append(formattedUnits.first)
            }.toString(),
        )

        WeatherInfoItem(
            title = "Wind",
            value = buildAnnotatedString {
                append(daily.wind_speed.roundToInt().toString())
                val formattedUnits = generateUnits(selectedUnits)
                append(formattedUnits.second)
            }.text
        )

        WeatherInfoItem(
            title = "Humidity",
            value = "${daily.humidity} %"
        )

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
            Instant.ofEpochMilli(daily.timestamp * 1_000L),
            ZoneId.systemDefault()
        )
    val date = formatter.format(currentTime)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = date,
            style = MaterialTheme.typography.titleMedium
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
                modifier = Modifier.size(40.dp),
                contentScale = ContentScale.Crop
            )
        }
        Text(
            text = daily.weather[0].main,
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = buildAnnotatedString {
                append("${daily.temp.day.roundToInt()}")
                val formattedUnits = generateUnits(selectedUnits)
                if (selectedUnits == Units.METRIC) {
                    withStyle(SpanStyle(baselineShift = BaselineShift.Superscript)) {
                        append("o")
                    }
                }
                append(formattedUnits.first)
            },
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = buildAnnotatedString {
                append(daily.wind_speed.roundToInt().toString())
                val formattedUnits = generateUnits(selectedUnits)
                append(formattedUnits.second)
            }
        )
    }
}

@Composable
private fun Error(
    error: UserMessage,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Oops",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = error.description ?: "An unknown error occurred",
            style = MaterialTheme.typography.bodyMedium.copy(),
            color = MaterialTheme.colorScheme.onBackground.copy(
                alpha = 0.8f
            ),
            textAlign = TextAlign.Justify
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = onRetry,
            shape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(
                text = "Retry",
            )
        }
    }


}