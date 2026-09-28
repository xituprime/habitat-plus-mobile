package com.habitatplus.app.features.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import com.habitatplus.app.ui.theme.HabitatLightBlue
import com.habitatplus.app.ui.theme.HabitatPlusTheme
import com.habitatplus.app.ui.theme.HabitatSurface
import com.habitatplus.app.ui.theme.HabitatTextPrimary
import com.habitatplus.app.ui.theme.HabitatTextSecondary
import androidx.compose.foundation.layout.ColumnScope


@Composable
fun RegisterScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HabitatBackground)
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = 20.dp,
                vertical = 24.dp
            ),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(
                color = HabitatLightBlue,
                shape = CircleShape
            ) {
                Text(
                    text = "←",
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    ),
                    color = HabitatBlue,
                    fontSize = 18.sp
                )
            }

            Surface(
                color = HabitatLightBlue,
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "◉ Comunidad Segura",
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 6.dp
                    ),
                    color = HabitatBlue,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Text(
            text = "Crear cuenta",
            color = HabitatTextPrimary,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Regístrate para acceder a los servicios de tu condominio.",
            color = HabitatTextSecondary,
            fontSize = 11.sp
        )

        RegisterSectionCard(
            title = "Datos personales"
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    RegisterField(
                        label = "Nombre",
                        value = "Carlos"
                    )
                }

                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    RegisterField(
                        label = "Apellido",
                        value = "Mendoza"
                    )
                }
            }

            RegisterField(
                label = "Correo electrónico",
                value = "carlos.mendoza@ejemplo.com"
            )

            RegisterField(
                label = "Teléfono (Opcional)",
                value = "+506 8888 1234"
            )
        }

        RegisterSectionCard(
            title = "Ubicación de unidad"
        ) {

            RegisterField(
                label = "Apartamento",
                value = "EJ. A-203"
            )

            Text(
                text = "La administración validará tu residencia contra el padrón.",
                color = HabitatTextSecondary,
                fontSize = 9.sp
            )
        }

        RegisterSectionCard(
            title = "Seguridad de la cuenta"
        ) {

            RegisterField(
                label = "Contraseña",
                value = "Mínimo 8 caracteres"
            )

            RegisterField(
                label = "Confirmar contraseña",
                value = "Repite tu contraseña"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .background(
                            color = HabitatLightBlue,
                            shape = RoundedCornerShape(10.dp)
                        )
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.22f)
                            .height(4.dp)
                            .background(
                                color = HabitatBlue,
                                shape = RoundedCornerShape(10.dp)
                            )
                    )
                }

                Text(
                    text = "  Básica",
                    color = HabitatTextSecondary,
                    fontSize = 8.sp
                )
            }
        }

        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = HabitatBlue
            )
        ) {
            Text(
                text = "Crear cuenta  →",
                fontWeight = FontWeight.SemiBold
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {

            Text(
                text = "¿Ya tienes una cuenta? ",
                color = HabitatTextSecondary,
                fontSize = 10.sp
            )

            Text(
                text = "Iniciar sesión",
                color = HabitatBlue,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )
    }
}

@Composable
private fun RegisterSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = HabitatSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                color = HabitatTextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            content()
        }
    }
}

@Composable
private fun RegisterField(
    label: String,
    value: String
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {

        Text(
            text = label,
            color = HabitatTextPrimary,
            fontSize = 9.sp
        )

        OutlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Preview(
    name = "02 - Registro",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,dpi=420"
)
@Composable
private fun RegisterScreenPreview() {
    HabitatPlusTheme {
        RegisterScreen()
    }
}