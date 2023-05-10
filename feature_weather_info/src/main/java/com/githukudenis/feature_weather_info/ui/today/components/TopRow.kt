package com.githukudenis.feature_weather_info.ui.today.components

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.githukudenis.feature_weather_info.R
import com.githukudenis.feature_weather_info.data.repository.Theme

@Composable
fun TopRow(
    modifier: Modifier = Modifier,
    appTheme: Theme,
    onOpenMenu: () -> Unit,
    onChangeTheme: (Theme) -> Unit
) {
    val context = LocalContext.current

    Row(
        modifier = modifier
            .padding(12.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = onOpenMenu,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.menu),
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                contentDescription = context.getString(R.string.menu_icon)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .clip(shape = RoundedCornerShape(32.dp))
                .background(color = MaterialTheme.colorScheme.secondaryContainer)
                .clickable {
                    onChangeTheme(
                        if (appTheme == Theme.LIGHT) Theme.DARK else Theme.LIGHT
                    )
                }
                .padding(4.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.light_mode),
                contentDescription = "Light",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = if (appTheme == Theme.LIGHT) Color(0xFF1471FD) else Color.Transparent,
                        shape = CircleShape
                    )
                    .padding(4.dp)

            )
            Icon(
                painter = painterResource(id = R.drawable.dark_mode),
                contentDescription = "Dark",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = if (appTheme == Theme.DARK) Color(0xFF1471FD) else Color.Transparent,
                        shape = CircleShape
                    )
                    .padding(4.dp)
            )
        }
    }
}

@Preview(
    device = "spec:width=1080px,height=2400px,isRound=true", showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun TopRowPreview() {
    MaterialTheme {
        TopRow(appTheme = Theme.DARK, onChangeTheme = { newTheme -> }, onOpenMenu = {})
    }
}