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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.habitatplus.app.ui.theme.HabitatPlusTheme
import com.habitatplus.app.ui.theme.HabitatSurface
import com.habitatplus.app.ui.theme.HabitatTextPrimary
import com.habitatplus.app.ui.theme.HabitatTextSecondary
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.imePadding

@Composable
fun LoginScreen(
    state: LoginState = LoginState(),
    onEvent: (LoginEvent) -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onLoginSuccess: () -> Unit = {}
) {
    androidx.compose.runtime.LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HabitatBackground)
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = 20.dp,
                vertical = 24.dp
            )
    ){

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = HabitatSurface,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = 18.dp,
                    vertical = 16.dp
                ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .background(
                            color = HabitatBlue,
                            shape = RoundedCornerShape(7.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "H+",
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Habitat+",
                    color = HabitatBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(
            modifier = Modifier.height(26.dp)
        )

        Text(
            text = "Bienvenido de nuevo",
            color = HabitatTextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = "Ingresa a tu portal residencial",
            color = HabitatTextSecondary,
            fontSize = 12.sp
        )

        Spacer(
            modifier = Modifier.height(28.dp)
        )

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
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                Text(
                    text = "Correo electrónico",
                    color = HabitatTextPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )

                OutlinedTextField(
                    value = state.email,
                    onValueChange = {
                        onEvent(LoginEvent.EmailChanged(it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !state.isLoading,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Text(
                    text = "Contraseña",
                    color = HabitatTextPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )

                OutlinedTextField(
                    value = state.password,
                    onValueChange = {
                        onEvent(LoginEvent.PasswordChanged(it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !state.isLoading,
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp)
                )

                Text(
                    text = "¿Olvidaste tu contraseña?",
                    modifier = Modifier.align(
                        Alignment.End
                    ),
                    color = HabitatBlue,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
                state.error?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
                Button(
                    onClick = {
                        onEvent(LoginEvent.LoginClicked)
                    },
                    enabled = !state.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HabitatBlue
                    )
                ) {
                    Text(
                        text = "Iniciar sesión  →",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        Row(
            horizontalArrangement = Arrangement.Center
        ) {

            Text(
                text = "¿No tienes cuenta? ",
                color = HabitatTextPrimary,
                fontSize = 11.sp
            )

            Text(
                text = "Crear cuenta",
                color = HabitatBlue,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable {
                    onRegisterClick()
                },
            )
        }
    }
}

@Preview(
    name = "01 - Login",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,dpi=420"
)
@Composable
private fun LoginScreenPreview() {
    HabitatPlusTheme {
        LoginScreen()
    }
}