package com.githukudenis.feature_weather_info.ui.today

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.githukudenis.feature_weather_info.R
import com.githukudenis.feature_weather_info.data.repository.Theme
import com.githukudenis.feature_weather_info.data.repository.Units
import com.githukudenis.feature_weather_info.ui.today.components.CurrentWeatherItem
import com.githukudenis.feature_weather_info.ui.today.components.JumpingBubblesIndicator
import com.githukudenis.feature_weather_info.ui.today.components.LocationContainer
import com.githukudenis.feature_weather_info.ui.today.components.TopRow
import com.githukudenis.feature_weather_info.ui.today.components.WeatherInfoItem
import com.githukudenis.feature_weather_info.util.WeatherIconMapper
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun TodayRoute(
    snackbarHostState: SnackbarHostState,
    todayViewModel: TodayViewModel,
    appTheme: Theme,
    onChangeAppTheme: (Theme) -> Unit
) {
    val uiState by todayViewModel.state.collectAsStateWithLifecycle()
    var selectedUnits by remember {
        mutableStateOf(Units.STANDARD)
    }

    val units = listOf(
        Units.METRIC,
        Units.STANDARD,
        Units.IMPERIAL,
    )
    val dialogProperties =
        DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)


    if (uiState.shouldAskForUnits) {
        // Ask for units
        Dialog(
            onDismissRequest = {
                todayViewModel.onEvent(TodayUiEvent.ChangeUnits(selectedUnits))
            }, properties = dialogProperties
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    val context = LocalContext.current

                    Text(
                        text = context.getString(R.string.unit_dialog_title),
                        style = MaterialTheme.typography.titleLarge
                    )

                    units.forEach {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = it.name.lowercase().replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.labelMedium
                            )
                            RadioButton(
                                selected = it == selectedUnits,
                                onClick = { selectedUnits = it })
                        }
                    }
                    Button(
                        onClick = {
                            todayViewModel.onEvent(TodayUiEvent.ChangeUnits(selectedUnits))
                        }
                    ) {
                        Text(
                            text = context.getString(R.string.ok)
                        )
                    }
                }
            }
        }
    }


    TodayScreen(
        todayUiState = uiState,
        appTheme = appTheme,
        onChangeTheme = onChangeAppTheme,
        snackbarHostState = snackbarHostState,
        onShowUserMessage = { messageId ->
            todayViewModel.onEvent(TodayUiEvent.OnShowUserMessage(messageId))
        })
}

@Composable
private fun TodayScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    todayUiState: TodayUiState,
    appTheme: Theme,
    onChangeTheme: (Theme) -> Unit,
    onShowUserMessage: (Int) -> Unit
) {
    var menuOpen by remember {
        mutableStateOf(false)
    }

    val dateFormatter = DateTimeFormatter
        .ofPattern("MMM d, yyyy")
    val date = dateFormatter.format(
        LocalDate.now()
    )

    LaunchedEffect(todayUiState.userMessages) {
        if (todayUiState.userMessages.isNotEmpty()) {
            val userMessage = todayUiState.userMessages.first()
            snackbarHostState.showSnackbar(
                message = userMessage.description ?: return@LaunchedEffect,
                duration = SnackbarDuration.Long
            )
            userMessage.id?.let { onShowUserMessage(it) }
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TopRow(appTheme = appTheme, onOpenMenu = {
            menuOpen = !menuOpen
        }, onChangeTheme = onChangeTheme)

        if (todayUiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                JumpingBubblesIndicator()
            }
            return
        }


        todayUiState.locationState.name?.let {
            LocationContainer(
                name = it,
                date = date
            )
        }
        val icon: Int? = todayUiState.currentWeatherState.icon?.let { iconId ->
            WeatherIconMapper.icons.find {
                it.first == iconId
            }?.second
        }
        icon?.let { iconId ->
            CurrentWeatherItem(
                icon = iconId,
                temp = todayUiState.currentWeatherState.temperature.toString(),
                main = todayUiState.currentWeatherState.main.toString()
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            todayUiState.currentWeatherState.temperature?.let {
                WeatherInfoItem(
                    title = "Temp",
                    value = it.toString(),
                    tempInfoItem = true
                )
            }
            todayUiState.currentWeatherState.windSpeed?.let {
                WeatherInfoItem(
                    title = "Wind",
                    value = "$it"
                )
            }
            todayUiState.currentWeatherState.humidity?.let {
                WeatherInfoItem(
                    title = "Humidity",
                    value = " $it %"
                )
            }
        }
        HourlySection(hourLyForeCast = todayUiState.hourlyForeCastState.foreCast)
    }
}

@Composable
fun HourlySection(
    hourLyForeCast: List<ForeCast>
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Today",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "See full report",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(hourLyForeCast) { weatherInfo ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val icon = weatherInfo.icon.run {
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
                    weatherInfo.time?.let { time ->
                        val formatter = DateTimeFormatter.ofPattern("hh mm:a")
                        val formattedTime =
                            LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(time.toLong()),
                                ZoneId.systemDefault()
                            ).format(formatter)

                        Text(
                            text = formattedTime,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    Text(
                        text = "${weatherInfo.temperature}",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}


@Preview(
    device = "id:pixel_7", showSystemUi = false,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun TodayRoutePreview() {
    MaterialTheme {
        TodayScreen(
            todayUiState = TodayUiState(),
            appTheme = Theme.DARK,
            onChangeTheme = {},
            onShowUserMessage = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}
