package com.githukudenis.wetha

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.githukudenis.feature_weather_info.ui.today.TodayRoute
import com.githukudenis.feature_weather_info.ui.today.TodayViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun WethaNavigator(
    snackbarHostState: SnackbarHostState,
    navHostController: NavHostController
) {
    NavHost(navController = navHostController, startDestination = WethaDestination.Today.route) {
        composable(route = WethaDestination.Today.route) {
            val todayViewModel: TodayViewModel = koinViewModel()
            TodayRoute(snackbarHostState = snackbarHostState, todayViewModel = todayViewModel)
        }
    }
}

sealed class WethaDestination(val route: String) {
    object Today: WethaDestination(route = "today")
}