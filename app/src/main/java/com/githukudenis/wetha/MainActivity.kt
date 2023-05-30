package com.githukudenis.wetha

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.githukudenis.feature_weather_info.data.repository.Theme
import com.githukudenis.wetha.ui.theme.WethaTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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


            WethaTheme(darkTheme = appState.appTheme == Theme.DARK) {
                when {
                    permissionsState.allPermissionsGranted -> {
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

                    else -> {
                        LaunchedEffect(Unit) {
                          permissionsState.launchMultiplePermissionRequest()
                        }
                        PermissionsScreen(permissions = permissionsState.permissions.map { it.permission })
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