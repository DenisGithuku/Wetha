package com.githukudenis.wetha.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.githukudenis.feature_weather_info.R


val QuickSand = FontFamily(
    Font(R.font.quicksand_bold),
    Font(R.font.quicksand_light),
    Font(R.font.quicksand_medium),
    Font(R.font.quicksand_regular),
    Font(R.font.quicksand_semi_bold),
)
// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = QuickSand,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    // Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = QuickSand,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = QuickSand,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelMedium = TextStyle(
        fontFamily = QuickSand,
        fontSize = 16.sp
    ),
    labelLarge = TextStyle(
        fontFamily = QuickSand,
        fontSize = 20.sp,
        lineHeight = 16.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = QuickSand,
        fontSize = 18.sp,
    )
)