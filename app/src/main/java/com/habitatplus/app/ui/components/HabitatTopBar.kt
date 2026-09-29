package com.habitatplus.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitatplus.app.R
import com.habitatplus.app.ui.theme.HabitatBlue
import com.habitatplus.app.ui.theme.HabitatPlusTheme
import com.habitatplus.app.ui.theme.HabitatSurface
import com.habitatplus.app.ui.theme.HabitatTextPrimary

@Composable
fun HabitatTopBar(
    title: String,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {}
) {
    Surface(
        color = HabitatSurface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(
                    horizontal = 16.dp,
                    vertical = 10.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (showBackButton) {

                IconButton(
                    onClick = onBackClick
                ) {
                    Icon(
                        painter = painterResource(
                            R.drawable.ic_arrow_back
                        ),
                        contentDescription = "Regresar",
                        tint = HabitatTextPrimary
                    )
                }

            } else {

                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(
                            color = HabitatBlue,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "H+",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }

                Spacer(
                    modifier = Modifier.width(10.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {

                if (!showBackButton) {
                    Text(
                        text = "HABITAT+",
                        color = HabitatBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    )
                }

                Text(
                    text = title,
                    color = HabitatTextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp
                )
            }

            IconButton(
                onClick = {}
            ) {
                Icon(
                    painter = painterResource(
                        R.drawable.ic_notifications
                    ),
                    contentDescription = "Notificaciones",
                    tint = HabitatTextPrimary,
                    modifier = Modifier.size(21.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = HabitatBlue,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(
                        R.drawable.ic_profile
                    ),
                    contentDescription = "Perfil",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Preview(
    name = "TopBar - Parqueos",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun HabitatTopBarPreview() {
    HabitatPlusTheme {
        HabitatTopBar(
            title = "Parqueos"
        )
    }
}

@Preview(
    name = "TopBar - Regresar",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun HabitatTopBarBackPreview() {
    HabitatPlusTheme {
        HabitatTopBar(
            title = "Reservar Parqueo",
            showBackButton = true
        )
    }
}