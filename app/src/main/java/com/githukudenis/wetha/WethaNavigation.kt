package com.githukudenis.wetha

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.githukudenis.feature_weather_info.data.repository.Theme
import com.githukudenis.feature_weather_info.ui.full_report.DailyWeatherViewModel
import com.githukudenis.feature_weather_info.ui.full_report.DailyUpdatesRoute
import com.githukudenis.feature_weather_info.ui.today.TodayRoute
import com.githukudenis.feature_weather_info.ui.today.TodayViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun WethaNavigator(
    appTheme: Theme,
    onChangeAppTheme: (Theme) -> Unit,
    navHostController: NavHostController,
    context: Context
) {
    NavHost(navController = navHostController, startDestination = context.getString(R.string.nav_graph)) {
        navigation(
            startDestination = WethaDestination.Today.route,
            route = context.getString(R.string.nav_graph)
        ) {

            composable(route = WethaDestination.Today.route) {
                val todayViewModel: TodayViewModel = koinViewModel()
                TodayRoute(
                    todayViewModel = todayViewModel,
                    appTheme = appTheme,
                    onChangeAppTheme = onChangeAppTheme,
                    onViewFullReport = {
                        navHostController.navigate(WethaDestination.FullReport.route) {
                            popUpTo(WethaDestination.FullReport.route) {
                                inclusive = true
                            }
                            restoreState = true
                        }
                    }
                )
            }
            composable(WethaDestination.FullReport.route) {
                val dailyWeatherViewModel: DailyWeatherViewModel = koinViewModel()
                DailyUpdatesRoute(
                    dailyWeatherViewModel = dailyWeatherViewModel
                )
            }
        }
    }
}

sealed class WethaDestination(val route: String) {
    object Today: WethaDestination(route = "today")
    object FullReport: WethaDestination(route = "full_report")
}