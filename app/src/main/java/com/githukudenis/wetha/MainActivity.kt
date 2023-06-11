package com.githukudenis.wetha

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.githukudenis.feature_weather_info.data.repository.Theme
import com.githukudenis.wetha.ui.theme.WethaTheme
import com.githukudenis.wetha.util.LocationListenerWorker
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.koin.androidx.compose.koinViewModel
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        )

        setupLocationRequestWorker(this)

        setContent {
            val snackbarHostState = SnackbarHostState()
            val navHostController = rememberNavController()
            val permissionsState = rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )

            val mainViewModel: MainViewModel = koinViewModel()
            val appState by mainViewModel.appState.collectAsStateWithLifecycle()

            val systemUiController = rememberSystemUiController()
            val useDarkIcons = appState.appTheme != Theme.DARK
            DisposableEffect(systemUiController, useDarkIcons) {
                systemUiController.setStatusBarColor(
                    color = Color.Transparent,
                    darkIcons = useDarkIcons
                )
                onDispose {}
            }

            WethaTheme(darkTheme = appState.appTheme == Theme.DARK) {
                when {
                    permissionsState.allPermissionsGranted -> {
                        Surface(
                            modifier = Modifier
                        ) {
                            WethaNavigator(
                                appTheme = appState.appTheme,
                                onChangeAppTheme = { newTheme ->
                                    mainViewModel.onEvent(MainEvent.ChangeAppTheme(newTheme))
                                },
                                snackbarHostState = snackbarHostState,
                                navHostController = navHostController,
                                context = this
                            )
                        }
                    }

                    else -> {
                        LaunchedEffect(Unit) {
                            permissionsState.launchMultiplePermissionRequest()
                        }
                        PermissionsScreen(listOf("Location permissions"))
                    }
                }

            }
        }
    }
}

@Composable
fun PermissionsScreen(
    permissions: List<String>
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier.padding(
                vertical = 100.dp,
                horizontal = 20.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Permissions required",
                style = MaterialTheme.typography.headlineMedium
            )
            permissions.map { message ->
                Text(
                    text = message
                )
            }
            Text(
                text = "Permissions required in order to proceed using the app"
            )
        }
        Button(
            onClick = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            },
            shape = MaterialTheme.shapes.medium.copy(
                all = CornerSize(6.dp)
            ),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text(
                text = "Go to settings"
            )
        }
    }
}

private fun setupLocationRequestWorker(context: Context) {
    val constraints = Constraints.Builder()
        .setRequiresBatteryNotLow(true)
        .setRequiredNetworkType(NetworkType.METERED)
        .build()

    val workRequest = PeriodicWorkRequestBuilder<LocationListenerWorker>(1, TimeUnit.DAYS)
        .setConstraints(constraints)
        .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.HOURS)
        .build()

    WorkManager.getInstance(context)
        .enqueue(workRequest)
}