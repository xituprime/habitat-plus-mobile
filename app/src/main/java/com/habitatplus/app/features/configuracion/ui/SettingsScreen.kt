package com.habitatplus.app.features.configuracion.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.habitatplus.app.core.components.ResidentAvatar
import com.habitatplus.app.core.components.ResidentBadge
import com.habitatplus.app.core.components.ResidentCard
import com.habitatplus.app.core.components.ResidentOption
import com.habitatplus.app.core.components.ResidentPage
import com.habitatplus.app.core.components.ResidentTitle
import com.habitatplus.app.features.configuracion.intent.SettingsIntent
import com.habitatplus.app.features.configuracion.state.SettingsDialog
import com.habitatplus.app.features.configuracion.state.SettingsState
import com.habitatplus.app.features.configuracion.state.ThemePreference
import com.habitatplus.app.ui.components.HabitatBackHeader

@Composable
fun SettingsScreen(
    state: SettingsState,
    userName: String,
    userEmail: String,
    userInitials: String,
    residence: String,
    onIntent: (SettingsIntent) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
    onBackClick : () -> Unit = {}
) {
    LaunchedEffect(state.signOutRequested) {
        if (state.signOutRequested) {
            onIntent(SettingsIntent.SignOutHandled)
            onSignOut()
        }
    }

    ResidentPage(modifier) {
        HabitatBackHeader(
            title = "Regresar a perfil",
            onBackClick = onBackClick
        )
        ResidentTitle(
            text = "Configuración",
            subtitle = "Preferencias de la aplicación"
        )

        ResidentCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ResidentAvatar(initials = userInitials)

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = userEmail,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    ResidentBadge(text = residence)
                }
            }
        }

        ResidentCard {
            ResidentOption(
                title = "Idioma",
                subtitle = "Español (América Latina)",
                onClick = {
                    onIntent(
                        SettingsIntent.OpenDialog(SettingsDialog.LANGUAGE)
                    )
                }
            )

            HorizontalDivider()

            ResidentOption(
                title = "Tema",
                subtitle = state.theme.label,
                onClick = {
                    onIntent(
                        SettingsIntent.OpenDialog(SettingsDialog.THEME)
                    )
                }
            )

            HorizontalDivider()

            ResidentOption(
                title = "Acerca de",
                subtitle = "Información de Habitat+",
                onClick = {
                    onIntent(
                        SettingsIntent.OpenDialog(SettingsDialog.ABOUT)
                    )
                }
            )

            HorizontalDivider()

            ResidentOption(
                title = "Cerrar sesión",
                subtitle = "Cerrar sesión en este dispositivo",
                destructive = true,
                onClick = {
                    onIntent(
                        SettingsIntent.OpenDialog(SettingsDialog.SIGN_OUT)
                    )
                }
            )
        }

        Text(
            text = "Habitat+ · Administración de condominios",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    when (state.dialog) {
        SettingsDialog.LANGUAGE -> {
            SettingsInformationDialog(
                title = "Idioma",
                message = "Español (América Latina).\n\n" +
                        "Esta versión incluye únicamente español.",
                onDismiss = {
                    onIntent(SettingsIntent.DismissDialog)
                }
            )
        }

        SettingsDialog.THEME -> {
            AlertDialog(
                onDismissRequest = {
                    onIntent(SettingsIntent.DismissDialog)
                },
                title = {
                    Text("Tema de la aplicación")
                },
                text = {
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .selectableGroup()
                    ) {
                        ThemePreference.entries.forEach { preference ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .sizeIn(minHeight = 48.dp)
                                    .selectable(
                                        selected = state.theme == preference,
                                        role = Role.RadioButton,
                                        onClick = {
                                            onIntent(
                                                SettingsIntent.SelectTheme(
                                                    preference
                                                )
                                            )
                                        }
                                    )
                                    .padding(
                                        horizontal = 8.dp,
                                        vertical = 8.dp
                                    ),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(
                                    12.dp
                                )
                            ) {
                                RadioButton(
                                    selected = state.theme == preference,
                                    onClick = null
                                )

                                Text(
                                    text = preference.label,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onIntent(SettingsIntent.DismissDialog)
                        }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }

        SettingsDialog.ABOUT -> {
            SettingsInformationDialog(
                title = "Acerca de Habitat+",
                message = "Plataforma para la administración de condominios.\n\n" +
                        "Consulta anuncios, parqueos, reportes y reservas " +
                        "de áreas comunes.\n\n" +
                        "Proyecto académico desarrollado por Axel, Kenett y Junior.",
                onDismiss = {
                    onIntent(SettingsIntent.DismissDialog)
                }
            )
        }

        SettingsDialog.SIGN_OUT -> {
            AlertDialog(
                onDismissRequest = {
                    onIntent(SettingsIntent.DismissDialog)
                },
                title = {
                    Text("¿Cerrar sesión?")
                },
                text = {
                    Text(
                        text = "Tendrás que iniciar sesión nuevamente " +
                                "para acceder a tu cuenta.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onIntent(SettingsIntent.ConfirmSignOut)
                        }
                    ) {
                        Text(
                            text = "Cerrar sesión",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            onIntent(SettingsIntent.DismissDialog)
                        }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }

        null -> Unit
    }
}

@Composable
private fun SettingsInformationDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(title)
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.verticalScroll(rememberScrollState())
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Entendido")
            }
        }
    )
}