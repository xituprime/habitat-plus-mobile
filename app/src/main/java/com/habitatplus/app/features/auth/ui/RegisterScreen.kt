
package com.habitatplus.app.features.auth.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitatplus.app.ui.theme.HabitatPlusTheme

@Composable
fun RegisterScreen(
    state: RegisterState = RegisterState(),
    onEvent: (RegisterEvent) -> Unit = {},
    onRegisterSuccess: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme

    LaunchedEffect(state.isRegistered) {
        if (state.isRegistered) {
            onRegisterSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = 20.dp,
                vertical = 24.dp
            ),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        // Encabezado y flecha de regreso.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = colors.primaryContainer,
                shape = CircleShape
            ) {
                IconButton(
                    onClick = onLoginClick
                ) {
                    val arrowColor = colors.onPrimaryContainer

                    Canvas(
                        modifier = Modifier.size(24.dp)
                    ) {
                        drawLine(
                            color = arrowColor,
                            start = Offset(
                                size.width * 0.85f,
                                size.height * 0.5f
                            ),
                            end = Offset(
                                size.width * 0.15f,
                                size.height * 0.5f
                            ),
                            strokeWidth = 2.5.dp.toPx(),
                            cap = StrokeCap.Round
                        )

                        drawLine(
                            color = arrowColor,
                            start = Offset(
                                size.width * 0.15f,
                                size.height * 0.5f
                            ),
                            end = Offset(
                                size.width * 0.45f,
                                size.height * 0.2f
                            ),
                            strokeWidth = 2.5.dp.toPx(),
                            cap = StrokeCap.Round
                        )

                        drawLine(
                            color = arrowColor,
                            start = Offset(
                                size.width * 0.15f,
                                size.height * 0.5f
                            ),
                            end = Offset(
                                size.width * 0.45f,
                                size.height * 0.8f
                            ),
                            strokeWidth = 2.5.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                }
            }

            Surface(
                color = colors.primaryContainer,
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "◉ Comunidad Segura",
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 6.dp
                    ),
                    color = colors.onPrimaryContainer,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Text(
            text = "Crear cuenta",
            color = colors.onBackground,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Regístrate para acceder a los servicios de tu condominio.",
            color = colors.onBackground.copy(alpha = 0.85f),
            fontSize = 12.sp
        )

        // Datos personales.
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
                        value = state.name,
                        onValueChange = {
                            onEvent(RegisterEvent.NameChanged(it))
                        }
                    )
                }

                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    RegisterField(
                        label = "Apellido",
                        value = state.lastName,
                        onValueChange = {
                            onEvent(RegisterEvent.LastNameChanged(it))
                        }
                    )
                }
            }

            RegisterField(
                label = "Correo electrónico",
                value = state.email,
                onValueChange = {
                    onEvent(RegisterEvent.EmailChanged(it))
                }
            )

            RegisterField(
                label = "Teléfono (Opcional)",
                value = state.phone,
                onValueChange = {
                    onEvent(RegisterEvent.PhoneChanged(it))
                }
            )
        }

        // Datos de residencia.
        RegisterSectionCard(
            title = "Ubicación de unidad"
        ) {
            RegisterField(
                label = "Condominio",
                value = state.condominium,
                onValueChange = {
                    onEvent(
                        RegisterEvent.CondominiumChanged(it)
                    )
                }
            )

            RegisterField(
                label = "Torre",
                value = state.tower,
                onValueChange = {
                    onEvent(
                        RegisterEvent.TowerChanged(it)
                    )
                }
            )

            RegisterField(
                label = "Apartamento",
                value = state.apartment,
                onValueChange = {
                    onEvent(
                        RegisterEvent.ApartmentChanged(it)
                    )
                }
            )

            Text(
                text = "La administración validará tu residencia contra el padrón.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }

        // Seguridad de la cuenta.
        RegisterSectionCard(
            title = "Seguridad de la cuenta"
        ) {
            RegisterField(
                label = "Contraseña",
                value = state.password,
                onValueChange = {
                    onEvent(
                        RegisterEvent.PasswordChanged(it)
                    )
                },
                isPassword = true
            )

            RegisterField(
                label = "Confirmar contraseña",
                value = state.confirmPassword,
                onValueChange = {
                    onEvent(
                        RegisterEvent.ConfirmPasswordChanged(it)
                    )
                },
                isPassword = true
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
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(10.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.22f)
                            .height(4.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(10.dp)
                            )
                    )
                }

                Text(
                    text = "  Básica",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
            }
        }

        // Mensaje de error.
        state.error?.let { error ->
            Text(
                text = error,
                color = colors.error,
                fontSize = 12.sp
            )
        }

        // Botón Crear cuenta.
        Button(
            onClick = {
                onEvent(RegisterEvent.RegisterClicked)
            },
            enabled = !state.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.primary,
                contentColor = colors.onPrimary,
                disabledContainerColor =
                    colors.onSurface.copy(alpha = 0.12f),
                disabledContentColor =
                    colors.onSurface.copy(alpha = 0.38f)
            )
        ) {
            Text(
                text = if (state.isLoading) {
                    "Creando cuenta..."
                } else {
                    "Crear cuenta  →"
                },
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Enlace a Iniciar sesión.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "¿Ya tienes una cuenta? ",
                color = colors.onBackground,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "Iniciar sesión",
                color = colors.onBackground,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    onLoginClick()
                }
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
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surface
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
                color = colors.onSurface,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            content()
        }
    }
}

@Composable
private fun RegisterField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false,
    enabled: Boolean = true
) {
    val colors = MaterialTheme.colorScheme

    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = label,
            color = colors.onSurface,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(
                color = colors.onSurface
            ),
            visualTransformation =
                if (isPassword) {
                    PasswordVisualTransformation()
                } else {
                    VisualTransformation.None
                },
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
