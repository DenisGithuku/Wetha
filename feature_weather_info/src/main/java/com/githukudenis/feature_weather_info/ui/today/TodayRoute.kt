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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayRoute(
    snackbarHostState: SnackbarHostState,
    todayViewModel: TodayViewModel,
    appTheme: Theme,
    onChangeAppTheme: (Theme) -> Unit
) {
    val uiState by todayViewModel.state.collectAsStateWithLifecycle()

    val units = listOf(
        Units.METRIC,
        Units.STANDARD,
        Units.IMPERIAL,
    )
    val dialogProperties =
        DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)

    val scope = rememberCoroutineScope()

    val modalBottomSheetShate = rememberModalBottomSheetState()


    if (uiState.shouldAskForUnits) {
        // Ask for units
        Dialog(
            onDismissRequest = {
                uiState.selectedUnits?.let { TodayUiEvent.ChangeUnits(it) }
                    ?.let { todayViewModel.onEvent(it) }
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
                                selected = it == uiState.selectedUnits,
                                onClick = { todayViewModel.onEvent(TodayUiEvent.ChangeUnits(it)) })
                        }
                    }
                    Button(
                        onClick = {
                            uiState.selectedUnits?.let {
                                TodayUiEvent.ChangeUnits(
                                    it
                                )
                            }?.let { todayViewModel.onEvent(it) }
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

    if (modalBottomSheetShate.isVisible) {
        ModalBottomSheet(
            sheetState = modalBottomSheetShate,
            onDismissRequest = {
                scope.launch {
                    modalBottomSheetShate.hide()
                }
            }) {
            Column(
                modifier = Modifier
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.Start
            ) {
                val context = LocalContext.current

                Text(
                    text = context.getString(R.string.unit_dialog_title),
                    style = MaterialTheme.typography.titleLarge
                )

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    items(items = units) {
                        FilterChip(
                            selected = it == uiState.selectedUnits,
                            leadingIcon = {
                                if (it == uiState.selectedUnits) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_check),
                                        contentDescription = "Selected"
                                    )
                                }
                            },
                            shape = RoundedCornerShape(32.dp),
                            onClick = {
                                todayViewModel.onEvent(TodayUiEvent.ChangeUnits(it))
                            },
                            label = {
                                Text(it.name.lowercase().replaceFirstChar { it.uppercase() })
                            })
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
        onOpenOptions = {
            scope.launch {
                if (modalBottomSheetShate.isVisible) {
                    modalBottomSheetShate.hide()
                } else {
                    modalBottomSheetShate.show()
                }
            }
        },
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
    onOpenOptions: () -> Unit,
    onChangeTheme: (Theme) -> Unit,
    onShowUserMessage: (Int) -> Unit
) {

    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getDefault()
    val date = dateFormat.format(Date())

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
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFE4EEF8),
                        Color.White
                    )
                )
            )
            .systemBarsPadding()
            .padding(12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TopRow(appTheme = appTheme, onOpenMenu = onOpenOptions, onChangeTheme = onChangeTheme)

        if (todayUiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
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
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
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
                style = MaterialTheme.typography.labelMedium
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(hourLyForeCast) { weatherInfo ->
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.extraLarge)
                        .background(Color(0xFFE4EEF8))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
                    ) {
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
                            val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
                            formatter.timeZone = TimeZone.getDefault()
                            val date = Date(time * 1000L)
                            val formattedTime = formatter.format(date)

                            Text(
                                text = formattedTime,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Text(
                            text = buildAnnotatedString {
                                append("${weatherInfo.temperature?.roundToInt()}")
                                withStyle(
                                    SpanStyle(
                                        baselineShift = BaselineShift.Superscript
                                    )
                                ) {
                                    append("o")
                                }
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
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
            onOpenOptions = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}
