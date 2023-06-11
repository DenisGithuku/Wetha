package com.githukudenis.feature_weather_info.ui.today

import android.content.res.Configuration
import android.graphics.PointF
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.githukudenis.feature_weather_info.R
import com.githukudenis.feature_weather_info.common.MessageType
import com.githukudenis.feature_weather_info.common.UserMessage
import com.githukudenis.feature_weather_info.data.repository.Theme
import com.githukudenis.feature_weather_info.data.repository.Units
import com.githukudenis.feature_weather_info.ui.today.components.CurrentWeatherItem
import com.githukudenis.feature_weather_info.ui.today.components.JumpingBubblesIndicator
import com.githukudenis.feature_weather_info.ui.today.components.LocationContainer
import com.githukudenis.feature_weather_info.ui.today.components.TopRow
import com.githukudenis.feature_weather_info.ui.today.components.WeatherInfoItem
import com.githukudenis.feature_weather_info.util.WeatherIconMapper
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun TodayRoute(
    snackbarHostState: SnackbarHostState,
    currentWeatherViewModel: CurrentWeatherViewModel,
    appTheme: Theme,
    onChangeAppTheme: (Theme) -> Unit,
    onViewFullReport: () -> Unit
) {
    val uiState by currentWeatherViewModel.state.collectAsStateWithLifecycle()

    Crossfade(
        targetState = uiState,
        label = "screen_state",
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    ) {
        when (val currentState = it) {
            is TodayScreenState.Loading -> {
                LoadingScreen(
                    shouldAskForUnits = currentState.shouldAskForUnits,
                    onSelectUnits = { units ->
                        currentWeatherViewModel.onEvent(
                            TodayUiEvent.ChangeUnits(units)
                        )
                    }
                )
            }

            is TodayScreenState.Loaded -> {
                LoadedScreen(
                    snackbarHostState = snackbarHostState,
                    todayUiState = currentState.todayUiState,
                    appTheme = appTheme,
                    onChangeUnits = {
                        currentWeatherViewModel.onEvent(TodayUiEvent.ChangeUnits(it))
                    },
                    onChangeTheme = onChangeAppTheme,
                    onShowUserMessage = { messageId, messageType ->
                        currentWeatherViewModel.onEvent(
                            TodayUiEvent.OnShowUserMessage(
                                messageId,
                                messageType
                            )
                        )
                    },
                    onViewFullReport = onViewFullReport
                )
            }

            is TodayScreenState.Error -> {
                ErrorScreen(
                    error = currentState.userMessages
                        .first(),
                    onRetry = {
                        currentWeatherViewModel.onEvent(TodayUiEvent.Retry)
                    }
                )
            }
        }
    }
}

@Composable
private fun ErrorScreen(
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun LoadedScreen(
    snackbarHostState: SnackbarHostState,
    todayUiState: TodayUiState,
    appTheme: Theme,
    onChangeUnits: (Units) -> Unit,
    onChangeTheme: (Theme) -> Unit,
    onShowUserMessage: (Int, MessageType) -> Unit,
    onViewFullReport: () -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())
    val time = LocalDateTime.now()
        .format(dateFormatter)

    val dialogProperties = DialogProperties()

    val units = listOf(
        Units.METRIC,
        Units.STANDARD,
        Units.IMPERIAL,
    )

    val selectedUnits = remember {
        mutableStateOf(todayUiState.selectedUnits)
    }

    val modalBottomSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()


    LaunchedEffect(todayUiState.userMessages) {
        if (todayUiState.userMessages.isNotEmpty()) {
            val userMessage = todayUiState.userMessages.first()
            snackbarHostState.showSnackbar(
                message = userMessage.description ?: return@LaunchedEffect,
                duration = SnackbarDuration.Long
            )
            userMessage.id?.let { onShowUserMessage(it, userMessage.messageType) }
        }
    }

    if (modalBottomSheetState.isVisible) {
        ModalBottomSheet(
            sheetState = modalBottomSheetState,
            onDismissRequest = {
                scope.launch {
                    modalBottomSheetState.hide()
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
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = units) {
                        FilterChip(
                            selected = it == todayUiState.selectedUnits,
                            leadingIcon = {
                                if (it == todayUiState.selectedUnits) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_check),
                                        contentDescription = "Selected"
                                    )
                                }
                            },
                            shape = RoundedCornerShape(32.dp),
                            onClick = {
                                onChangeUnits(it)
                            },
                            label = {
                                Text(it.name.lowercase().replaceFirstChar { it.uppercase() })
                            })
                    }
                }
            }
        }
    }


    Column(
        modifier = Modifier
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
        TopRow(appTheme = appTheme, onOpenMenu = {
            scope.launch {
                if (modalBottomSheetState.isVisible) {
                    modalBottomSheetState.hide()
                } else {
                    modalBottomSheetState.show()
                }
            }
        }, onChangeTheme = onChangeTheme)

        todayUiState.locationState.name?.let {
            LocationContainer(
                name = it,
                date = time
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
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.background
        ) {

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                maxItemsInEachRow = 3,
            ) {
                todayUiState.currentWeatherState.temperature?.let {
                    WeatherInfoItem(
                        title = "Temp",
                        value = it.toString(),
                        icon = R.drawable.ic_thermometer,
                        tempInfoItem = true
                    )
                }
                todayUiState.currentWeatherState.windSpeed?.let {
                    WeatherInfoItem(
                        title = "Wind",
                        icon = R.drawable.ic_wind_solid,
                        value = "$it"
                    )
                }
                todayUiState.currentWeatherState.humidity?.let {
                    WeatherInfoItem(
                        title = "Humidity",
                        icon = R.drawable.ic_humidity,
                        value = " $it %"
                    )
                }
                todayUiState.currentWeatherState.pressure?.let {
                    WeatherInfoItem(
                        title = "Pressure",
                        value = "$it hPa",
                        icon = R.drawable.ic_pressure
                    )
                }
                todayUiState.currentWeatherState.uvi?.let {
                    WeatherInfoItem(
                        title = "UVI",
                        value = "$it",
                        icon = R.drawable.ic_uv_index
                    )
                }
                todayUiState.currentWeatherState.sunrise?.let {
                    val formattedTime = formatTime("hh:mm a", it)
                    WeatherInfoItem(
                        title = "Sunrise",
                        value = formattedTime,
                        icon = R.drawable.ic_sunrise
                    )
                }
                todayUiState.currentWeatherState.sunset?.let {
                    val formattedTime = formatTime("hh:mm a", it)
                    WeatherInfoItem(
                        title = "Sunset",
                        value = formattedTime,
                        icon = R.drawable.ic_sunset
                    )
                }
            }
        }
        HourlySection(
            hourLyForeCast = todayUiState.hourlyForeCastState.foreCast,
            onViewFullReport = onViewFullReport
        )
    }
}

private fun formatTime(pattern: String, time: Int): String {
    val formatter = DateTimeFormatter.ofPattern(
        pattern,
        Locale.getDefault()
    )
    val parsedTime = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(time * 1_000L),
        ZoneId.systemDefault()
    )
    return parsedTime.format(formatter)
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun HourlySection(
    hourLyForeCast: List<ForeCast>,
    onViewFullReport: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val interactionSource = remember { MutableInteractionSource() }
            Text(
                text = "Today",
                style = MaterialTheme.typography.titleMedium
            )

            TextButton(onClick = onViewFullReport) {
                Text(
                    text = "See full report",
                )
            }
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
                            val dateFormatter = DateTimeFormatter
                                .ofPattern("hh:mm a", Locale.getDefault())
                                .apply {
                                    withZone(ZoneId.systemDefault())
                                }

                            val time = LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(time * 1_000L),
                                ZoneId.systemDefault()
                            )
                            val formattedTime = time.format(dateFormatter)

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
        Spacer(modifier = Modifier.height(8.dp))
    }
}


@Composable
private fun LoadingScreen(
    shouldAskForUnits: Boolean,
    onSelectUnits: (Units) -> Unit
) {
    val units = listOf(
        Units.METRIC,
        Units.STANDARD,
        Units.IMPERIAL,
    )

    val selectedUnits = remember {
        mutableStateOf(units.first())
    }
    val dialogProperties = DialogProperties()

    if (shouldAskForUnits) {
        // Ask for units
        Dialog(
            onDismissRequest = {
                onSelectUnits(selectedUnits.value)
            }, properties = dialogProperties
        ) {
            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.large)
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
                                selected = it == selectedUnits.value,
                                onClick = { selectedUnits.value = it })
                        }
                    }
                    Button(
                        onClick = {
                            onSelectUnits(selectedUnits.value)
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

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        JumpingBubblesIndicator()
    }
}

private fun generateGraphPath(tempList: List<Float>, size: Size): Path {
    val path = Path()
    val numberEntries = tempList.size - 1
    val tempWidth = size.width / numberEntries

    val maxValue = tempList.maxBy { it }
    val minValue = tempList.minBy { it }
    val range = maxValue - minValue
    val heightPxPerTempValue = size.height / range

    var previousTempX = 0f
    var previousTempY = size.height
    tempList.forEachIndexed { index, temp ->
        if (index == 0) {
            path.moveTo(
                x = 0f,
                y = size.height - (temp - minValue) * heightPxPerTempValue
            )
        }
        val tempX = index * tempWidth
        val tempY = size.height - (temp - minValue) * heightPxPerTempValue

        // create a smooth curve using cubic bezier
        val controlPoint1 = PointF((tempX + previousTempX) / 2f, previousTempY)
        val controlPoint2 = PointF((tempX + previousTempX) / 2f, tempY)
        path.cubicTo(
            controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, tempX, tempY
        )
        previousTempX = tempX
        previousTempY = tempY
    }
    return path
}

private fun generatePoints(tempList: List<Float>, size: Size): List<Offset> {
    val numberEntries = tempList.size - 1
    val tempWidth = size.width / numberEntries

    val maxValue = tempList.maxBy { it }
    val minValue = tempList.minBy { it }
    val range = maxValue - minValue
    val heightPxPerTempValue = size.height / range

    return tempList.mapIndexed { index, temp ->
        if (index == 0) {
            Offset(0f, size.height - (temp - minValue) * heightPxPerTempValue)
        }
        val tempX = index * tempWidth
        val tempY = size.height - (temp - minValue) * heightPxPerTempValue
        Offset(tempX, tempY)
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
        LoadedScreen(
            todayUiState = TodayUiState(),
            appTheme = Theme.DARK,
            onChangeTheme = {},
            onShowUserMessage = { id, type -> },
            snackbarHostState = SnackbarHostState(),
            onChangeUnits = {},
            onViewFullReport = {}
        )
    }
}

