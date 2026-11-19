
package com.habitatplus.app.features.admin.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.habitatplus.app.features.admin.data.AdminParkingRecord
import com.habitatplus.app.features.admin.data.PendingResident
import com.habitatplus.app.features.admin.viewmodel.AdminResidentsState
import com.habitatplus.app.ui.components.HabitatBackHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminResidentsScreen(
    state: AdminResidentsState,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onApprove: (String, String) -> Unit,
    onReject: (String) -> Unit,
    onRefreshParking: () -> Unit = {}
) {
    var statusFilter by remember { mutableStateOf("Todas") }
    var search by remember { mutableStateOf("") }

    val filtered = state.parkingReservations.filter { record ->
        val r = record.reservation

        val matchesStatus = when (statusFilter) {
            "Activas" -> r.status == "Activa"
            "Canceladas" -> r.status == "Cancelada"
            else -> true
        }

        val matchesSearch = search.isBlank() ||
                record.residentName.contains(search, true) ||
                record.residentEmail.contains(search, true) ||
                r.visitorName.contains(search, true) ||
                r.parkingId.contains(search, true) ||
                r.apartment.contains(search, true) ||
                r.date.contains(search, true)

        matchesStatus && matchesSearch
    }

    Scaffold(
        topBar = {
            HabitatBackHeader(
                title = "Administración",
                onBackClick = onBack
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {
                Text(
                    "Solicitudes de residencia",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            item {
                Button(
                    onClick = onRefresh,
                    enabled = !state.isLoading && !state.isSaving
                ) {
                    Text("Actualizar solicitudes")
                }
            }

            if (state.isLoading) {
                item {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            items(
                state.residents,
                key = { "resident_${it.uid}" }
            ) { resident ->
                ResidentRequestCard(
                    resident = resident,
                    enabled = !state.isSaving,
                    onApprove = onApprove,
                    onReject = onReject
                )
            }

            if (!state.isLoading && state.residents.isEmpty()) {
                item { Text("No hay solicitudes pendientes.") }
            }

            item { HorizontalDivider() }

            item {
                Text(
                    "Supervisión de parqueos",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            item {
                Button(
                    onClick = onRefreshParking,
                    enabled = !state.isLoadingParking
                ) {
                    Text("Actualizar parqueos")
                }
            }

            item {
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    label = {
                        Text("Buscar residente, parqueo o fecha")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                Row(
                    modifier = Modifier.horizontalScroll(
                        rememberScrollState()
                    ),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "Todas",
                        "Activas",
                        "Canceladas"
                    ).forEach { option ->
                        FilterChip(
                            selected = statusFilter == option,
                            onClick = { statusFilter = option },
                            label = { Text(option) }
                        )
                    }
                }
            }

            if (state.isLoadingParking) {
                item {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (!state.isLoadingParking && filtered.isEmpty()) {
                item {
                    Text("No hay reservas para mostrar.")
                }
            }

            items(
                filtered,
                key = { "parking_${it.reservation.id}" }
            ) { record ->
                AdminParkingCard(record)
            }

            state.error?.let { error ->
                item {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            state.message?.let { message ->
                item {
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            item {
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun AdminParkingCard(
    record: AdminParkingRecord
) {
    val reservation = record.reservation

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                "Parqueo ${reservation.parkingId}",
                style = MaterialTheme.typography.titleMedium
            )

            Text("Residente: ${record.residentName}")

            if (record.residentEmail.isNotBlank()) {
                Text(
                    record.residentEmail,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text("Visitante: ${reservation.visitorName}")
            Text("Apartamento: ${reservation.apartment}")
            Text("Fecha: ${reservation.date}")
            Text(
                "Horario: ${reservation.entryTime} - " +
                        reservation.exitTime
            )
            Text("Estado: ${reservation.status}")
        }
    }
}

@Composable
private fun ResidentRequestCard(
    resident: PendingResident,
    enabled: Boolean,
    onApprove: (String, String) -> Unit,
    onReject: (String) -> Unit
) {
    var apartment by remember(resident.uid) {
        mutableStateOf(resident.requestedApartment)
    }

    var confirmReject by remember(resident.uid) {
        mutableStateOf(false)
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                "${resident.name} ${resident.lastName}",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                resident.email,
                style = MaterialTheme.typography.bodySmall
            )

            OutlinedTextField(
                value = apartment,
                onValueChange = { apartment = it },
                label = { Text("Apartamento a asignar") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = enabled
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        onApprove(resident.uid, apartment.trim())
                    },
                    enabled = enabled && apartment.isNotBlank()
                ) {
                    Text("Aprobar")
                }

                OutlinedButton(
                    onClick = { confirmReject = true },
                    enabled = enabled
                ) {
                    Text("Rechazar")
                }
            }
        }
    }

    if (confirmReject) {
        AlertDialog(
            onDismissRequest = { confirmReject = false },
            title = { Text("Rechazar solicitud") },
            text = {
                Text(
                    "¿Rechazar la solicitud de " +
                            "${resident.name} ${resident.lastName}?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmReject = false
                        onReject(resident.uid)
                    }
                ) {
                    Text("Rechazar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { confirmReject = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
