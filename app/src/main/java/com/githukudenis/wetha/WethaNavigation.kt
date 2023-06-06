package com.githukudenis.wetha

import android.location.Location
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.githukudenis.feature_weather_info.data.repository.Theme
import com.githukudenis.feature_weather_info.ui.full_report.FullReportRoute
import com.githukudenis.feature_weather_info.ui.today.TodayRoute
import com.githukudenis.feature_weather_info.ui.today.TodayViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun WethaNavigator(
    appTheme: Theme,
    onChangeAppTheme: (Theme) -> Unit,
    snackbarHostState: SnackbarHostState,
    navHostController: NavHostController
) {
    NavHost(navController = navHostController, startDestination = WethaDestination.Today.route) {
        composable(route = WethaDestination.Today.route) {
            val todayViewModel: TodayViewModel = koinViewModel()
            TodayRoute(
                snackbarHostState = snackbarHostState,
                todayViewModel = todayViewModel,
                appTheme = appTheme,
                onChangeAppTheme = onChangeAppTheme,
                onViewFullReport = {
                    navHostController.navigate(WethaDestination.FullReport.route) {
                        popUpTo(WethaDestination.FullReport.route){
                            inclusive = true
                        }
                        restoreState = true
                    }
                }
            )
        }
        composable(WethaDestination.FullReport.route) {
            FullReportRoute()
        }
    }
}

sealed class WethaDestination(val route: String) {
    object Today: WethaDestination(route = "today")
    object FullReport: WethaDestination(route = "full_report")
}