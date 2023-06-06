package com.githukudenis.feature_weather_info.ui.today.components

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.githukudenis.feature_weather_info.R

@Composable
fun CurrentWeatherItem(
    @DrawableRes icon: Int,
    temp: String,
    main: String
) {

    val degreeStyle = SpanStyle(
        baselineShift = BaselineShift.Superscript,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp
    )

    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = icon),
            modifier = Modifier.size(200.dp),
            contentDescription = "Weather icon"
        )
        Text(
            text = buildAnnotatedString {
                append(temp)
                withStyle(style = degreeStyle) {
                    append("o")
                }
                append("C")
            },
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = main,
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
fun CurrentWeatherItemPreview() {
    MaterialTheme {
        CurrentWeatherItem(
            icon = R.drawable.snowy_bulk,
            temp = "36",
            main = "Thunder Overcast"
        )
    }
}