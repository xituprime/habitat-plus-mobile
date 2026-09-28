package com.habitatplus.app.features.perfil.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.habitatplus.app.core.components.*
import com.habitatplus.app.features.perfil.intent.ProfileIntent
import com.habitatplus.app.features.perfil.state.ProfileState

@Composable
fun ProfileScreen(
    state: ProfileState,
    onIntent: (ProfileIntent) -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier,
    profileImage: Painter? = null
) {
    ResidentPage(modifier) {
        ResidentTitle(
            text = "Mi perfil"
        )

        if (state.isOffline) {
            ResidentOfflineNotice()
        }

        when {
            state.isLoading -> {
                ResidentLoading()
            }

            state.error != null -> {
                ResidentError(
                    message = state.error,
                    onRetry = {
                        onIntent(ProfileIntent.Load)
                    }
                )
            }

            else -> {
                ResidentCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (profileImage != null) {
                            Image(
                                painter = profileImage,
                                contentDescription = "Foto de perfil",
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            ResidentAvatar(
                                initials = state.initials
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = state.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            ResidentBadge(
                                text = state.role
                            )

                            Text(
                                text = "${state.tower} · ${state.apartment}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                ResidentField(
                    label = "UNIDAD REGISTRADA",
                    value = state.condominium
                )

                ResidentCard {
                    Text(
                        text = "Información de contacto",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    ResidentField(
                        label = "Correo electrónico",
                        value = state.email
                    )

                    HorizontalDivider()

                    ResidentField(
                        label = "Teléfono móvil",
                        value = state.phone
                    )
                }

                Text(
                    text = "GESTIÓN Y ACCESOS",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                ResidentCard {
                    ResidentOption(
                        title = "Mis datos de residencia",
                        subtitle = "Apartamento ${state.apartment}",
                        onClick = {
                            onIntent(ProfileIntent.ShowResidence)
                        }
                    )

                    HorizontalDivider()

                    ResidentOption(
                        title = "Configuración",
                        subtitle = "Preferencias de la aplicación",
                        onClick = onSettings
                    )
                }

                Text(
                    text = if (state.verified) {
                        "Perfil verificado por la administración."
                    } else {
                        "Perfil pendiente de verificación."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (state.showResidence) {
        AlertDialog(
            onDismissRequest = {
                onIntent(ProfileIntent.DismissResidence)
            },
            title = {
                Text("Mis datos de residencia")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ResidentField(
                        label = "Condominio",
                        value = state.condominium
                    )

                    ResidentField(
                        label = "Torre",
                        value = state.tower
                    )

                    ResidentField(
                        label = "Apartamento",
                        value = state.apartment
                    )

                    ResidentField(
                        label = "Rol",
                        value = state.role
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onIntent(ProfileIntent.DismissResidence)
                    }
                ) {
                    Text("Cerrar")
                }
            }
        )
    }
}