package com.habitatplus.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val HabitatLightColors = lightColorScheme(

    primary = HabitatBlue,
    onPrimary = BackgroundLight,

    secondary = HabitatBlueSecondary,
    onSecondary = BackgroundLight,

    background = BackgroundLight,
    onBackground = TextPrimary,

    surface = BackgroundLight,
    onSurface = TextPrimary,

    error = Error,
    onError = BackgroundLight

)

private val HabitatDarkColors = darkColorScheme(

    primary = HabitatBlue,
    secondary = HabitatBlueSecondary,
    background = TextPrimary,
    surface = TextPrimary

)

@Composable
fun HabitatPlusTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {

    MaterialTheme(

        colorScheme = if (darkTheme)
            HabitatDarkColors
        else
            HabitatLightColors,

        typography = Typography,
        shapes = Shapes,
        content = content

    )

}