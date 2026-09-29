package com.habitatplus.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(

    primary = HabitatBlue,
    onPrimary = HabitatSurface,

    primaryContainer = HabitatLightBlue,
    onPrimaryContainer = HabitatTextPrimary,

    secondary = HabitatSecondary,
    onSecondary = HabitatSurface,

    secondaryContainer = HabitatLightBlue,
    onSecondaryContainer = HabitatTextPrimary,

    background = HabitatBackground,
    onBackground = HabitatTextPrimary,

    surface = HabitatSurface,
    onSurface = HabitatTextPrimary,

    surfaceVariant = HabitatLightBlue,
    onSurfaceVariant = HabitatTextSecondary,

    error = StatusError,
    onError = HabitatSurface
)

/*
 * El modo oscuro queda preparado, pero actualmente Habitat+
 * prioriza el modo claro según el Brand Book.
 */
private val DarkColorScheme = darkColorScheme(

    primary = HabitatSecondary,
    onPrimary = Color.White,

    secondary = HabitatLightBlue,
    onSecondary = HabitatTextPrimary,

    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF8FAFC),

    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF8FAFC),

    error = Color(0xFFF87171)
)

@Composable
fun HabitatPlusTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {

    val colorScheme = when {

        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {

            val context = LocalContext.current

            if (darkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }

        darkTheme -> DarkColorScheme

        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}