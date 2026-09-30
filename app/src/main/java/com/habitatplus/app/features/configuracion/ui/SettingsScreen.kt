package com.habitatplus.app.features.configuracion.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.habitatplus.app.core.components.*
import com.habitatplus.app.features.configuracion.intent.SettingsIntent
import com.habitatplus.app.features.configuracion.state.SettingsDialog
import com.habitatplus.app.features.configuracion.state.SettingsState
import com.habitatplus.app.features.configuracion.state.ThemePreference

@Composable
fun SettingsScreen(
    state: SettingsState,
    userName: String,
    userEmail: String,
    userInitials: String,
    residence: String,
    onIntent: (SettingsIntent) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    ResidentPage(modifier) {
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
                ResidentAvatar(
                    initials = userInitials
                )

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

                    ResidentBadge(
                        text = residence
                    )
                }
            }
        }

        ResidentCard {
            ResidentOption(
                title = "Idioma",
                subtitle = "Español (América Latina)",
                onClick = {
                    onIntent(
                        SettingsIntent.OpenDialog(
                            SettingsDialog.LANGUAGE
                        )
                    )
                }
            )

            HorizontalDivider()

            ResidentOption(
                title = "Tema",
                subtitle = state.theme.label,
                onClick = {
                    onIntent(
                        SettingsIntent.OpenDialog(
                            SettingsDialog.THEME
                        )
                    )
                }
            )

            HorizontalDivider()

            ResidentOption(
                title = "Acerca de",
                subtitle = "Información de Habitat+",
                onClick = {
                    onIntent(
                        SettingsIntent.OpenDialog(
                            SettingsDialog.ABOUT
                        )
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
                        SettingsIntent.OpenDialog(
                            SettingsDialog.SIGN_OUT
                        )
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
            AlertDialog(
                onDismissRequest = {
                    onIntent(SettingsIntent.DismissDialog)
                },
                title = {
                    Text("Idioma")
                },
                text = {
                    Text(
                        "Español (América Latina).\n\n" +
                                "Esta versión incluye únicamente español."
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onIntent(SettingsIntent.DismissDialog)
                        }
                    ) {
                        Text("Entendido")
                    }
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
                    Column {
                        ThemePreference.entries.forEach { preference ->
                            TextButton(
                                onClick = {
                                    onIntent(
                                        SettingsIntent.SelectTheme(
                                            preference
                                        )
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                RadioButton(
                                    selected = state.theme == preference,
                                    onClick = null
                                )

                                Spacer(
                                    modifier = Modifier.width(8.dp)
                                )

                                Text(
                                    text = preference.label,
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
            AlertDialog(
                onDismissRequest = {
                    onIntent(SettingsIntent.DismissDialog)
                },
                title = {
                    Text("Acerca de Habitat+")
                },
                text = {
                    Text(
                        "Plataforma para la administración de condominios.\n\n" +
                                "Consulta anuncios, parqueos, reportes y " +
                                "reservas de áreas comunes.\n\n" +
                                "Proyecto académico desarrollado por " +
                                "Axel, Kenett y Junior."
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onIntent(SettingsIntent.DismissDialog)
                        }
                    ) {
                        Text("Cerrar")
                    }
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
                        "Tendrás que iniciar sesión nuevamente " +
                                "para acceder a tu cuenta."
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onIntent(SettingsIntent.DismissDialog)
                            onSignOut()
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