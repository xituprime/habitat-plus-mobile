
package com.habitatplus.app.ui.theme

import android.os.Build
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
    onPrimary = Color.White,
    primaryContainer = HabitatLightBlue,
    onPrimaryContainer = HabitatTextPrimary,

    secondary = HabitatSecondary,
    onSecondary = Color.White,
    secondaryContainer = HabitatBlueContainer,
    onSecondaryContainer = HabitatTextPrimary,

    background = HabitatBackground,
    onBackground = HabitatTextPrimary,

    surface = HabitatSurface,
    onSurface = HabitatTextPrimary,
    surfaceVariant = HabitatLightBlue,
    onSurfaceVariant = HabitatTextSecondary,

    surfaceContainer = Color(0xFFF1F5F9),
    surfaceContainerHigh = Color(0xFFE2E8F0),

    outline = Color(0xFF94A3B8),
    outlineVariant = Color(0xFFCBD5E1),

    error = StatusError,
    onError = Color.White,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF991B1B)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF93B4FF),
    onPrimary = Color(0xFF09265C),
    primaryContainer = Color(0xFF244578),
    onPrimaryContainer = Color(0xFFE0EAFF),

    secondary = Color(0xFFB4CBFF),
    onSecondary = Color(0xFF142C55),
    secondaryContainer = Color(0xFF263B60),
    onSecondaryContainer = Color(0xFFDCE8FF),

    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF8FAFC),

    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFFCBD5E1),

    surfaceContainer = Color(0xFF253247),
    surfaceContainerHigh = Color(0xFF334155),

    outline = Color(0xFF94A3B8),
    outlineVariant = Color(0xFF475569),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
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
        shapes = Shapes,
        content = content
    )
}
