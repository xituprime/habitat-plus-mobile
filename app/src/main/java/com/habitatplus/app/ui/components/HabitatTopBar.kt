
package com.habitatplus.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitatplus.app.R
import com.habitatplus.app.ui.theme.HabitatPlusTheme

@Composable
fun HabitatTopBar(
    title: String,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    unreadCount: Int = 0
) {
    val colors = MaterialTheme.colorScheme

    Surface(
        color = colors.surface,
        contentColor = colors.onSurface,
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
            // Flecha de regreso o logo.
            if (showBackButton) {
                IconButton(
                    onClick = onBackClick
                ) {
                    Icon(
                        painter = painterResource(
                            R.drawable.ic_arrow_back
                        ),
                        contentDescription = "Regresar",
                        tint = colors.onSurface
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(
                            color = colors.primary,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "H+",
                        color = colors.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }

                Spacer(Modifier.width(10.dp))
            }

            // Titulo de la pantalla.
            Column(
                modifier = Modifier.weight(1f)
            ) {
                if (!showBackButton) {
                    Text(
                        text = "HABITAT+",
                        color = colors.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    )
                }

                Text(
                    text = title,
                    color = colors.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp
                )
            }

            // Campanita de notificaciones.
            Box {
                IconButton(
                    onClick = onNotificationsClick
                ) {
                    Icon(
                        painter = painterResource(
                            R.drawable.ic_notifications
                        ),
                        contentDescription = "Notificaciones",
                        tint = colors.onSurface,
                        modifier = Modifier.size(21.dp)
                    )
                }

                // Contador de notificaciones no leidas.
                if (unreadCount > 0) {
                    Badge(
                        modifier = Modifier.align(
                            Alignment.TopEnd
                        ),
                        containerColor = colors.error,
                        contentColor = colors.onError
                    ) {
                        Text(
                            text = if (unreadCount > 99) {
                                "99+"
                            } else {
                                unreadCount.toString()
                            }
                        )
                    }
                }
            }

            // Icono de perfil.
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = colors.primary,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(
                        R.drawable.ic_profile
                    ),
                    contentDescription = "Perfil",
                    tint = colors.onPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Preview(
    name = "TopBar claro",
    showBackground = true,
    widthDp = 412
)
@Composable
private fun HabitatTopBarLightPreview() {
    HabitatPlusTheme(darkTheme = false) {
        HabitatTopBar(
            title = "Inicio",
            unreadCount = 3
        )
    }
}

@Preview(
    name = "TopBar oscuro",
    showBackground = true,
    widthDp = 412
)
@Composable
private fun HabitatTopBarDarkPreview() {
    HabitatPlusTheme(darkTheme = true) {
        HabitatTopBar(
            title = "Inicio",
            unreadCount = 3
        )
    }
}

@Preview(
    name = "TopBar regresar oscuro",
    showBackground = true,
    widthDp = 412
)
@Composable
private fun HabitatTopBarBackPreview() {
    HabitatPlusTheme(darkTheme = true) {
        HabitatTopBar(
            title = "Reservar Parqueo",
            showBackButton = true
        )
    }
}
