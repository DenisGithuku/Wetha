package com.githukudenis.feature_weather_info.ui.today.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.githukudenis.feature_weather_info.data.repository.Units

@Composable
fun WeatherInfoItem(
    title: String,
    value: String,
    icon: Int? = null,
) {
    Column(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge
        )
        icon?.let { painterResource(id = it) }?.let {
            Image(
                painter = it,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(24.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
            )
        }
        val degreeSpanStyle = SpanStyle(
            baselineShift = BaselineShift.Superscript,
            fontSize = 10.sp
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview
@Composable
fun WeatherInfoItemPreview() {
    MaterialTheme {
        WeatherInfoItem(
            title = "Temp",
            value = "36",
            icon = 0,
        )
    }
}