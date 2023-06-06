package com.githukudenis.feature_weather_info.ui.today.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun JumpingBubblesIndicator(
    animationDelay: Int = 500,
    indicatorSize: Dp = 12.dp,
    indicatorColor: Color = MaterialTheme.colorScheme.secondary
) {

    val circles = remember {
        listOf(
            Animatable(0f),
            Animatable(0f),
            Animatable(0f),
        )
    }

    circles.forEachIndexed { index, animatable ->
        LaunchedEffect(Unit) {
            delay(
                timeMillis = (animationDelay / circles.size).toLong() * (index + 1)
            )

            animatable.animateTo(
                targetValue = 10.dp.value,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = animationDelay,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                )
            )
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        circles.forEachIndexed { index, animatable ->
            if (index != 0) {
                Spacer(modifier = Modifier.width(6.dp))
            }
            Box(
                modifier = Modifier
                    .size(indicatorSize)
                    .offset {
                        IntOffset(x = 0.dp.value.toInt(), y = -animatable.value.toInt())
                    }
                    .clip(CircleShape)
                    .background(indicatorColor)
            )
        }
    }
}