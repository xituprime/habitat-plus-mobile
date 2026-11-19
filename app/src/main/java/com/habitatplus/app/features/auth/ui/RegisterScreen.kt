
package com.habitatplus.app.features.auth.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitatplus.app.R
import com.habitatplus.app.ui.theme.HabitatPlusTheme

@Composable
fun RegisterScreen(
    state: RegisterState = RegisterState(),
    onEvent: (RegisterEvent) -> Unit = {},
    onRegisterSuccess: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    

    LaunchedEffect(state.isRegistered) {
        if (state.isRegistered) onRegisterSuccess()
    }

    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = colors.primaryContainer,
                shape = CircleShape
            ) {
                IconButton(onClick = onLoginClick) {
                    Icon(
                        painter = painterResource(
                            R.drawable.ic_arrow_back
                        ),
                        contentDescription = "Regresar",
                        tint = colors.onPrimaryContainer
                    )
                }
            }

            Surface(
                color = colors.primaryContainer,
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    "Comunidad Segura",
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 6.dp
                    ),
                    color = colors.onPrimaryContainer,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Text(
            "Crear cuenta",
            color = colors.onBackground,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Regístrate para acceder a los servicios de tu condominio.",
            color = colors.onSurfaceVariant,
            fontSize = 11.sp
        )

        RegisterSectionCard(title = "Datos personales") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(Modifier.weight(1f)) {
                    RegisterField(
                        label = "Nombre",
                        value = state.name,
                        onValueChange = {
                            onEvent(RegisterEvent.NameChanged(it))
                        },
                        enabled = !state.isLoading
                    )
                }

                Box(Modifier.weight(1f)) {
                    RegisterField(
                        label = "Apellido",
                        value = state.lastName,
                        onValueChange = {
                            onEvent(RegisterEvent.LastNameChanged(it))
                        },
                        enabled = !state.isLoading
                    )
                }
            }

            RegisterField(
                label = "Correo electrónico",
                value = state.email,
                onValueChange = {
                    onEvent(RegisterEvent.EmailChanged(it))
                },
                enabled = !state.isLoading
            )

            RegisterField(
                label = "Teléfono (opcional)",
                value = state.phone,
                onValueChange = {
                    onEvent(RegisterEvent.PhoneChanged(it))
                },
                enabled = !state.isLoading
            )
        }

        RegisterSectionCard(title = "Ubicación de unidad") {
            RegisterField(
                label = "Apartamento solicitado",
                value = state.apartment,
                onValueChange = {
                    onEvent(RegisterEvent.ApartmentChanged(it))
                },
                enabled = !state.isLoading
            )

            Text(
                "La administración verificará y aprobará tu residencia.",
                color = colors.onSurfaceVariant,
                fontSize = 10.sp
            )
        }

        RegisterSectionCard(title = "Seguridad de la cuenta") {
            RegisterField(
                label = "Contraseña",
                value = state.password,
                onValueChange = {
                    onEvent(
                        RegisterEvent.PasswordChanged(it)
                    )
                },
                isPassword = true,
                enabled = !state.isLoading
            )

            RegisterField(
                label = "Confirmar contraseña",
                value = state.confirmPassword,
                onValueChange = {
                    onEvent(
                        RegisterEvent.ConfirmPasswordChanged(it)
                    )
                },
                isPassword = true,
                enabled = !state.isLoading
            )

            LinearProgressIndicator(
                progress = {
                    (state.password.length / 12f)
                        .coerceIn(0f, 1f)
                },
                modifier = Modifier.fillMaxWidth(),
                color = colors.primary,
                trackColor = colors.primaryContainer
            )

            Text(
                "Usa una contraseña de al menos 8 caracteres.",
                color = colors.onSurfaceVariant,
                fontSize = 10.sp
            )
        }

        state.error?.let { error ->
            Text(
                error,
                color = colors.error,
                fontSize = 12.sp
            )
        }

        Button(
            onClick = {
                onEvent(RegisterEvent.RegisterClicked)
            },
            enabled = !state.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = colors.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    "Crear cuenta",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Enlace a Iniciar sesión.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "¿Ya tienes una cuenta? ",
                color = colors.onBackground,
                fontSize = 10.sp
            )

            Text(
                "Iniciar sesión",
                color = colors.primary,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable {
                    onLoginClick()
                }
            )
        }

        Spacer(Modifier.height(8.dp))
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
            containerColor = colors.surface,
            contentColor = colors.onSurface
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
                title,
                color = colors.onSurface,
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
            label,
            color = colors.onSurface,
            fontSize = 10.sp
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            visualTransformation = if (isPassword) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = colors.onSurface,
                unfocusedTextColor = colors.onSurface,
                disabledTextColor = colors.onSurfaceVariant,
                focusedContainerColor = colors.surface,
                unfocusedContainerColor = colors.surface,
                focusedBorderColor = colors.primary,
                unfocusedBorderColor = colors.outline,
                cursorColor = colors.primary
            ),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Preview(name = "Registro claro", showBackground = true)
@Composable
private fun RegisterLightPreview() {
    HabitatPlusTheme(darkTheme = false) {
        RegisterScreen()
    }
}

@Preview(name = "Registro oscuro", showBackground = true)
@Composable
private fun RegisterDarkPreview() {
    HabitatPlusTheme(darkTheme = true) {
        RegisterScreen()
    }
}
