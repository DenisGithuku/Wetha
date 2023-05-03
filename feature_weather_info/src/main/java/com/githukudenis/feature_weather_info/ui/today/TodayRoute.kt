package com.githukudenis.feature_weather_info.ui.today

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun TodayRoute(
    snackbarHostState: SnackbarHostState,
    todayViewModel: TodayViewModel
) {
    val state by todayViewModel.state.collectAsStateWithLifecycle()

    TodayScreen(timeZone = state)
}

@Composable
private fun TodayScreen(
    modifier: Modifier = Modifier,
    timeZone: String
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Timezone $timeZone"
        )
    }
}