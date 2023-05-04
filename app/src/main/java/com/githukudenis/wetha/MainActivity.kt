package com.githukudenis.wetha

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.githukudenis.feature_weather_info.data.repository.Theme
import com.githukudenis.wetha.ui.theme.WethaTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            0
        )
        setContent {
            val snackbarHostState = SnackbarHostState()
            val navHostController = rememberNavController()

            val systemUiController = rememberSystemUiController()
            val useDarkIcons = !isSystemInDarkTheme()

            DisposableEffect(systemUiController, useDarkIcons) {
                systemUiController.setSystemBarsColor(
                    color = Color.Transparent,
                    darkIcons = useDarkIcons
                )

                onDispose { }
            }

            val mainViewModel: MainViewModel = koinViewModel()
            val appState by mainViewModel.appState.collectAsStateWithLifecycle()

            WethaTheme(darkTheme = appState.appTheme == Theme.DARK) {
                Surface {
                    WethaNavigator(
                        appTheme = appState.appTheme,
                        onChangeAppTheme = { newTheme ->
                            mainViewModel.onEvent(MainEvent.ChangeAppTheme(newTheme))
                        },
                        snackbarHostState = snackbarHostState,
                        navHostController = navHostController
                    )
                }
            }
        }
    }
}