package com.habitatplus.app.features.perfil.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.habitatplus.app.core.components.*
import com.habitatplus.app.features.perfil.intent.ProfileIntent
import com.habitatplus.app.features.perfil.state.ProfileState
import com.habitatplus.app.ui.components.HabitatDestination
import com.habitatplus.app.ui.components.HabitatScaffold
import com.habitatplus.app.ui.theme.ResidentTheme

@Composable
fun ProfileScreen(
    state: ProfileState,
    onIntent: (ProfileIntent) -> Unit,
    onSettings: () -> Unit,
    onDestinationClick: (HabitatDestination) -> Unit = {},
    modifier: Modifier = Modifier,
    profileImage: Painter? = null
) {
    LaunchedEffect(state.settingsRequested) {
        if (state.settingsRequested) {
            onIntent(ProfileIntent.SettingsNavigationHandled)
            onSettings()
        }
    }

    HabitatScaffold(
        title = "Perfil",
        selectedDestination = HabitatDestination.PROFILE,
        onDestinationClick = onDestinationClick
    ) { innerPadding ->
        ResidentPage(
            modifier = modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
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
                    state.successMessage?.let { message ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor =
                                MaterialTheme.colorScheme.onSecondaryContainer
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = message,
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                TextButton(
                                    onClick = {
                                        onIntent(ProfileIntent.DismissSuccess)
                                    }
                                ) {
                                    Text("Entendido")
                                }
                            }
                        }
                    }

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
                                    color =
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                onIntent(ProfileIntent.StartEditing)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Editar perfil")
                        }
                    }

                    ResidentField(
                        label = "UNIDAD REGISTRADA",
                        value = state.condominium
                    )

                    ResidentCard {
                        Text(
                            text = "Información de contacto",
                            style = MaterialTheme.typography.titleLarge,
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
                            onClick = {
                                onIntent(ProfileIntent.OpenSettings)
                            }
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
    }

    if (state.showResidence) {
        ProfileResidenceDialog(
            state = state,
            onDismiss = {
                onIntent(ProfileIntent.DismissResidence)
            }
        )
    }

    if (state.isEditing) {
        ProfileEditDialog(
            state = state,
            onIntent = onIntent
        )
    }
}

@Composable
private fun ProfileResidenceDialog(
    state: ProfileState,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Mis datos de residencia")
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(
                    rememberScrollState()
                ),
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
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
private fun ProfileEditDialog(
    state: ProfileState,
    onIntent: (ProfileIntent) -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            onIntent(ProfileIntent.CancelEditing)
        },
        title = {
            Text("Editar perfil")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Actualiza tu nombre y teléfono de contacto.",
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    value = state.draftName,
                    onValueChange = {
                        onIntent(
                            ProfileIntent.ChangeName(it)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Nombre")
                    },
                    singleLine = true,
                    shape = MaterialTheme.shapes.small,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words
                    ),
                    isError = state.nameError != null,
                    supportingText = {
                        state.nameError?.let { message ->
                            Text(message)
                        }
                    }
                )

                OutlinedTextField(
                    value = state.draftPhone,
                    onValueChange = {
                        onIntent(
                            ProfileIntent.ChangePhone(it)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Teléfono")
                    },
                    singleLine = true,
                    shape = MaterialTheme.shapes.small,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone
                    ),
                    isError = state.phoneError != null,
                    supportingText = {
                        Text(
                            text = state.phoneError
                                ?: "Opcional. Puedes incluir el código de país."
                        )
                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onIntent(ProfileIntent.SaveChanges)
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onIntent(ProfileIntent.CancelEditing)
                }
            ) {
                Text("Cancelar")
            }
        }
    )
}

@Preview(
    name = "Perfil",
    showBackground = true,
    widthDp = 412,
    heightDp = 1000
)
@Composable
private fun ProfileScreenPreview() {
    ResidentTheme(darkTheme = false) {
        ProfileScreen(
            state = ProfileState(
                isLoading = false,
                name = "Kenett Ortega",
                initials = "KO",
                role = "Residente",
                condominium = "Condominio Las Acacias",
                tower = "Torre A",
                apartment = "A-203",
                email = "kenett@example.com",
                phone = "+502 5555-0100",
                verified = true
            ),
            onIntent = {},
            onSettings = {},
            onDestinationClick = {}
        )
    }
}