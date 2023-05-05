package com.githukudenis.feature_weather_info.ui.today

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.githukudenis.feature_weather_info.data.repository.Theme
import com.githukudenis.feature_weather_info.ui.today.components.CurrentWeatherItem
import com.githukudenis.feature_weather_info.ui.today.components.LocationContainer
import com.githukudenis.feature_weather_info.ui.today.components.TopRow
import com.githukudenis.feature_weather_info.ui.today.components.WeatherInfoItem
import com.githukudenis.feature_weather_info.util.WeatherIconMapper
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TodayRoute(
    snackbarHostState: SnackbarHostState,
    todayViewModel: TodayViewModel,
    appTheme: Theme,
    onChangeAppTheme: (Theme) -> Unit
) {
    val uiState by todayViewModel.state.collectAsStateWithLifecycle()

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

    if (todayUiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

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
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TopRow(appTheme = appTheme, onOpenMenu = {
            menuOpen = !menuOpen
        }, onChangeTheme = onChangeTheme)
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

        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            WeatherInfoItem(
                title = "Temp",
                value = todayUiState.currentWeatherState.temperature.toString(),
                tempInfoItem = true
            )
            WeatherInfoItem(
                title = "Wind",
                value = "${todayUiState.currentWeatherState.windSpeed} "
            )
            WeatherInfoItem(title = "Humidity", value = " ${todayUiState.currentWeatherState.humidity} %")
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
