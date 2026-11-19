
package com.habitatplus.app.features.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitatplus.app.ui.theme.HabitatPlusTheme

@Composable
fun SplashScreen(
    state: SplashState = SplashState(),
    onEvent: (SplashEvent) -> Unit = {},
    onNavigate: (SplashDestination) -> Unit = {}
) {
    LaunchedEffect(Unit) {
        onEvent(SplashEvent.CheckSession)
    }

    LaunchedEffect(state.destination) {
        state.destination?.let(onNavigate)
    }

    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(82.dp)
                .background(
                    color = colors.primary,
                    shape = RoundedCornerShape(22.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "H+",
                color = colors.onPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(18.dp))

        Text(
            text = "Habitat+",
            color = colors.primary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Plataforma inteligente para tu condominio",
            color = colors.onSurfaceVariant,
            fontSize = 12.sp
        )

        Spacer(Modifier.height(34.dp))

        CircularProgressIndicator(
            modifier = Modifier.size(28.dp),
            color = colors.primary,
            strokeWidth = 3.dp
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Cargando...",
            color = colors.onSurfaceVariant,
            fontSize = 11.sp
        )
    }
}

@Preview(name = "Splash claro", showBackground = true)
@Composable
private fun SplashLightPreview() {
    HabitatPlusTheme(darkTheme = false) {
        SplashScreen()
    }
}

@Preview(name = "Splash oscuro", showBackground = true)
@Composable
private fun SplashDarkPreview() {
    HabitatPlusTheme(darkTheme = true) {
        SplashScreen()
    }
}
