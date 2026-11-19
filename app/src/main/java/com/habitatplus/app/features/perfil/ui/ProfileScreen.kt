
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
    profileImage: Painter? = null,
    onNotificationsClick: () -> Unit = {},
    unreadCount: Int = 0
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
        onDestinationClick = onDestinationClick,
        onNotificationsClick = onNotificationsClick,
        unreadCount = unreadCount
    ) { innerPadding ->

        ResidentPage(
            modifier = modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            ResidentTitle(text = "Mi perfil")

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
                                ResidentAvatar(initials = state.initials)
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
                                    text = when (state.role.lowercase()) {
                                        "resident" -> "Residente"
                                        "admin" -> "Administrador"
                                        else -> state.role
                                    }
                                )

                                Text(
                                    text = if (
                                        state.verified &&
                                        state.apartment.isNotBlank()
                                    ) {
                                        residenceSummary(state)
                                    } else {
                                        "Residencia pendiente de verificación"
                                    },
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
                        value = if (state.verified) {
                            residenceSummary(state)
                        } else {
                            "Pendiente de aprobación"
                        }
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
                            value = state.phone.ifBlank {
                                "No registrado"
                            }
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
                            subtitle = if (state.verified) {
                                residenceSummary(state)
                            } else {
                                "Pendiente de aprobación"
                            },
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

private fun residenceSummary(state: ProfileState): String {
    val parts = listOf(
        state.tower,
        state.apartment
    ).filter { it.isNotBlank() }

    return if (parts.isEmpty()) {
        "Residencia pendiente de completar"
    } else {
        parts.joinToString(" · ")
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
                    value = state.condominium.ifBlank {
                        "No registrado"
                    }
                )

                ResidentField(
                    label = "Torre",
                    value = state.tower.ifBlank {
                        "No registrada"
                    }
                )

                ResidentField(
                    label = "Apartamento",
                    value = if (state.verified) {
                        state.apartment.ifBlank {
                            "No registrado"
                        }
                    } else {
                        "Pendiente de aprobación"
                    }
                )

                ResidentField(
                    label = "Rol",
                    value = when (state.role.lowercase()) {
                        "resident" -> "Residente"
                        "admin" -> "Administrador"
                        else -> state.role
                    }
                )

                Text(
                    text = "Los datos de residencia están sujetos " +
                        "a validación administrativa.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
            if (!state.isLoading) {
                onIntent(ProfileIntent.CancelEditing)
            }
        },
        title = {
            Text("Editar perfil")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Información personal",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                ProfileEditableField(
                    value = state.draftName,
                    label = "Nombre completo",
                    onValueChange = {
                        onIntent(ProfileIntent.ChangeName(it))
                    },
                    error = state.nameError,
                    enabled = !state.isLoading,
                    capitalization = KeyboardCapitalization.Words
                )

                ProfileEditableField(
                    value = state.draftPhone,
                    label = "Teléfono",
                    onValueChange = {
                        onIntent(ProfileIntent.ChangePhone(it))
                    },
                    error = state.phoneError,
                    enabled = !state.isLoading,
                    keyboardType = KeyboardType.Phone,
                    supporting = "Opcional. Incluye código de país si aplica."
                )

                HorizontalDivider()

                Text(
                    text = "Datos de residencia",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Los cambios de condominio, torre y apartamento " +
                        "se enviarán para revisión administrativa.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                ProfileEditableField(
                    value = state.draftCondominium,
                    label = "Condominio",
                    onValueChange = {
                        onIntent(ProfileIntent.ChangeCondominium(it))
                    },
                    error = state.condominiumError,
                    enabled = !state.isLoading,
                    capitalization = KeyboardCapitalization.Words
                )

                ProfileEditableField(
                    value = state.draftTower,
                    label = "Torre",
                    onValueChange = {
                        onIntent(ProfileIntent.ChangeTower(it))
                    },
                    error = state.towerError,
                    enabled = !state.isLoading,
                    capitalization = KeyboardCapitalization.Words
                )

                ProfileEditableField(
                    value = state.draftApartment,
                    label = "Apartamento solicitado",
                    onValueChange = {
                        onIntent(ProfileIntent.ChangeApartment(it))
                    },
                    error = state.apartmentError,
                    enabled = !state.isLoading,
                    capitalization = KeyboardCapitalization.Characters
                )

                HorizontalDivider()

                OutlinedTextField(
                    value = state.email,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Correo electrónico") },
                    readOnly = true,
                    singleLine = true,
                    enabled = false
                )

                OutlinedTextField(
                    value = when (state.role.lowercase()) {
                        "resident" -> "Residente"
                        "admin" -> "Administrador"
                        else -> state.role
                    },
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Rol") },
                    readOnly = true,
                    singleLine = true,
                    enabled = false
                )

                if (state.isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onIntent(ProfileIntent.SaveChanges)
                },
                enabled = !state.isLoading
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onIntent(ProfileIntent.CancelEditing)
                },
                enabled = !state.isLoading
            ) {
                Text("Cancelar")
            }
        )
    )
}

@Composable
private fun ProfileEditableField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    error: String?,
    enabled: Boolean,
    keyboardType: KeyboardType = KeyboardType.Text,
    capitalization: KeyboardCapitalization =
        KeyboardCapitalization.None,
    supporting: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        enabled = enabled,
        singleLine = true,
        shape = MaterialTheme.shapes.small,
        textStyle = MaterialTheme.typography.bodyLarge,
        keyboardOptions = KeyboardOptions(
            capitalization = capitalization,
            keyboardType = keyboardType
        ),
        isError = error != null,
        supportingText = if (error != null || supporting != null) {
            {
                Text(error ?: supporting.orEmpty())
            }
        } else {
            null
        }
    )
}

@Preview(name = "Perfil claro", showBackground = true)
@Composable
private fun ProfileLightPreview() {
    ResidentTheme(darkTheme = false) {
        ProfileScreen(
            state = ProfileState(
                isLoading = false,
                name = "Usuario de ejemplo",
                initials = "UE",
                role = "resident",
                condominium = "Condominio de prueba",
                tower = "Torre A",
                apartment = "A-203",
                email = "prueba@example.com",
                phone = "5555-0100",
                verified = true
            ),
            onIntent = {},
            onSettings = {}
        )
    }
}

@Preview(name = "Perfil oscuro", showBackground = true)
@Composable
private fun ProfileDarkPreview() {
    ResidentTheme(darkTheme = true) {
        ProfileScreen(
            state = ProfileState(
                isLoading = false,
                name = "Usuario de ejemplo",
                initials = "UE",
                role = "resident",
                condominium = "Condominio de prueba",
                tower = "Torre B",
                apartment = "B-204",
                email = "prueba@example.com",
                phone = "5555-0100",
                verified = true
            ),
            onIntent = {},
            onSettings = {}
        )
    }
}
