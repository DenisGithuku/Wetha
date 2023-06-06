package com.githukudenis.feature_weather_info.ui.today.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LocationContainer(
    name: String,
    date: String
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.displayMedium
        )
        Text(
            text = date,
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Preview
@Composable
fun LocationContainerPreview() {
    LocationContainer(name = "Nairobi, Kenya", date = "Today, 4 May")
}