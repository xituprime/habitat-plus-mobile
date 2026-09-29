package com.habitatplus.app.features.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitatplus.app.ui.theme.HabitatBackground
import com.habitatplus.app.ui.theme.HabitatBlue
import com.habitatplus.app.ui.theme.HabitatPlusTheme
import com.habitatplus.app.ui.theme.HabitatTextSecondary

@Composable
fun SplashScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HabitatBackground),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Box(
            modifier = Modifier
                .size(82.dp)
                .background(
                    color = HabitatBlue,
                    shape = RoundedCornerShape(22.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "H+",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Text(
            text = "Habitat+",
            color = HabitatBlue,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Plataforma inteligente para tu condominio",
            color = HabitatTextSecondary,
            fontSize = 12.sp
        )

        Spacer(
            modifier = Modifier.height(34.dp)
        )

        CircularProgressIndicator(
            modifier = Modifier.size(28.dp),
            color = HabitatBlue,
            strokeWidth = 3.dp
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Text(
            text = "Cargando...",
            color = HabitatTextSecondary,
            fontSize = 11.sp
        )
    }
}

@Preview(
    name = "00 - Splash",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,dpi=420"
)
@Composable
private fun SplashScreenPreview() {
    HabitatPlusTheme {
        SplashScreen()
    }
}