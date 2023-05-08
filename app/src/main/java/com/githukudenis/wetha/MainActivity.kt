package com.githukudenis.wetha

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.githukudenis.feature_weather_info.data.repository.Theme
import com.githukudenis.feature_weather_info.data.repository.Units
import com.githukudenis.feature_weather_info.data.repository.UserPrefsRepository
import com.githukudenis.wetha.ui.theme.WethaTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.koin.android.ext.android.inject
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
                    if (appState.units == null) {

                        val units = listOf(
                            Units.STANDARD,
                            Units.IMPERIAL,
                            Units.METRIC,
                        )
                        var selectedUnit by remember {
                            mutableStateOf(units.first())
                        }
                        Dialog(
                            onDismissRequest = {
                                mainViewModel.onEvent(MainEvent.ChangeUnits(selectedUnit))
                            }
                        ) {
                            Box {
                                LazyColumn {
                                    item {
                                        Text(
                                            text = "Select unit",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Divider()
                                    }
                                    items(items = units) {
                                        Row(modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically ) {
                                            RadioButton(
                                                selected = selectedUnit == it,
                                                onClick = { selectedUnit = it })
                                            Text(
                                                text = it.name.replaceFirstChar { it.uppercase() },
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                        }

                                    }
                                }
                            }
                        }
                        return@Surface
                    }
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