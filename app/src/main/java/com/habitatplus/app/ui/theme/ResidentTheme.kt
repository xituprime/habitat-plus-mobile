package com.habitatplus.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object ResidentColors {
    val Blue = Color(0xFF0B2F78)
    val SecondaryBlue = Color(0xFF3D6FC7)
    val LightBlue = Color(0xFFDCE8FF)

    val Background = Color(0xFFF8FAFC)
    val Text = Color(0xFF1E293B)
    val SecondaryText = Color(0xFF64748B)

    val Green = Color(0xFF15803D)
    val GreenContainer = Color(0xFFDCFCE7)

    val Orange = Color(0xFF92400E)
    val OrangeContainer = Color(0xFFFEF3C7)
    val Maintenance = Color(0xFFF59E0B)

    val Red = Color(0xFFB91C1C)
    val RedContainer = Color(0xFFFEE2E2)
}

private val ResidentLightScheme = lightColorScheme(
    primary = ResidentColors.Blue,
    onPrimary = Color.White,
    primaryContainer = ResidentColors.LightBlue,
    onPrimaryContainer = ResidentColors.Blue,

    secondary = ResidentColors.SecondaryBlue,
    onSecondary = Color.White,
    secondaryContainer = ResidentColors.LightBlue,
    onSecondaryContainer = ResidentColors.Blue,

    background = ResidentColors.Background,
    onBackground = ResidentColors.Text,

    surface = Color.White,
    onSurface = ResidentColors.Text,
    surfaceVariant = Color(0xFFEEF3FF),
    onSurfaceVariant = ResidentColors.SecondaryText,

    outline = Color(0xFFCBD5E1),

    error = ResidentColors.Red,
    onError = Color.White,
    errorContainer = ResidentColors.RedContainer,
    onErrorContainer = ResidentColors.Red
)

private val ResidentDarkScheme = darkColorScheme(
    primary = Color(0xFFAEC6FF),
    onPrimary = Color(0xFF002D70),
    primaryContainer = Color(0xFF173F80),
    onPrimaryContainer = ResidentColors.LightBlue,

    secondary = Color(0xFFB7CBF0),
    onSecondary = Color(0xFF173252),
    secondaryContainer = Color(0xFF263F60),
    onSecondaryContainer = Color(0xFFDCE8FF),

    background = Color(0xFF101820),
    onBackground = Color(0xFFE2E8F0),

    surface = Color(0xFF1B2633),
    onSurface = Color(0xFFE2E8F0),
    surfaceVariant = Color(0xFF263445),
    onSurfaceVariant = Color(0xFFCBD5E1),

    outline = Color(0xFF718096),

    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF6F2020),
    onErrorContainer = Color(0xFFFFDAD6)
)

@Composable
fun ResidentTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) {
            ResidentDarkScheme
        } else {
            ResidentLightScheme
        },
        typography = MaterialTheme.typography,
        shapes = Shapes(
            small = RoundedCornerShape(12.dp),
            medium = RoundedCornerShape(14.dp),
            large = RoundedCornerShape(16.dp)
        ),
        content = content
    )
}