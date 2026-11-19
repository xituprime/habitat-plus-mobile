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
import kotlinx.coroutines.tasks.await
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.produceState
import com.habitatplus.app.data.repository.ReservationImageRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminResidentsScreen(
    state: AdminResidentsState,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onApprove: (String, String) -> Unit,
    onReject: (String) -> Unit,
    onRefreshParking: () -> Unit = {},
    onRefreshAreas: () -> Unit = {},
    onRefreshReports: () -> Unit = {},
    onChangeReportStatus: (String, String) -> Unit = { _, _ -> },
    onRefreshCatalog: () -> Unit = {},
    onAddParking: (String) -> Unit = {}
) {

    val tabs = listOf("Viviendas", "Parqueos", "Áreas", "Reportes")
    var selected by remember { mutableIntStateOf(0) }
    var search by remember { mutableStateOf("") }
    var statusFilter by remember { mutableStateOf("Todas") }
    var newParking by remember { mutableStateOf("") }
    Scaffold(topBar = { HabitatBackHeader("Administración", onBack) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                tabs.forEachIndexed { index, label ->
                    Tab(selected = selected == index, onClick = { selected = index },
                        text = { Text(label) }, modifier = Modifier.width(116.dp))
                }
            }
            LazyColumn(contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                when (selected) {
                    0 -> {
                        item { Button(onClick = onRefresh, enabled = !state.isLoading && !state.isSaving) { Text("Actualizar solicitudes") } }
                        if (state.isLoading) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
                        items(state.residents, key = { "resident_${it.uid}" }) { resident ->
                            ResidentRequestCard(resident, !state.isSaving, onApprove, onReject)
                        }
                        if (!state.isLoading && state.residents.isEmpty()) item { Text("No hay solicitudes pendientes.") }
                    }
                    1 -> {
                        item { Button(onClick = onRefreshParking) { Text("Actualizar reservas") } }
                        item {
                            OutlinedTextField(search, { search = it }, label = { Text("Buscar residente o parqueo") },
                                modifier = Modifier.fillMaxWidth())
                        }
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("Todas", "Activas", "Canceladas").forEach { option ->
                                    FilterChip(selected = statusFilter == option,
                                        onClick = { statusFilter = option }, label = { Text(option) })
                                }
                            }
                        }
                        val filtered = state.parkingReservations.filter { record ->
                            (statusFilter == "Todas" || record.reservation.status == if (statusFilter == "Activas") "Activa" else "Cancelada") &&
                                (search.isBlank() || record.residentName.contains(search, true) ||
                                    record.reservation.parkingId.contains(search, true) ||
                                    record.reservation.date.contains(search, true))
                        }
                        items(filtered, key = { "parking_${it.reservation.id}" }) { AdminParkingCard(it) }
                        item { HorizontalDivider() }
                        item { Text("Catálogo de parqueos", style = MaterialTheme.typography.titleMedium) }
                        item { Button(onClick = onRefreshCatalog) { Text("Actualizar catálogo") } }
                        item { Text(if (state.parkingSpaces.isEmpty()) "Sin parqueos dinámicos" else state.parkingSpaces.joinToString()) }
                        item {
                            OutlinedTextField(newParking, { newParking = it }, label = { Text("Nuevo código (V-06)") },
                                modifier = Modifier.fillMaxWidth(), singleLine = true)
                        }
                        item { Button(onClick = { onAddParking(newParking); newParking = "" }) { Text("Agregar parqueo") } }
                    }
                    2 -> {
                        item { Button(onClick = onRefreshAreas) { Text("Actualizar áreas") } }
                        if (state.isLoadingAreas) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
                        items(state.areaReservations, key = { "area_${it.id}" }) { record ->
                            Card(Modifier.fillMaxWidth()) {
                                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(record.areaId, style = MaterialTheme.typography.titleMedium)
                                    Text("Residente: ${record.residentName}")
                                    Text("Apartamento: ${record.apartment}")
                                    Text("Fecha: ${record.date}")
                                    Text("Horario: ${record.startTime} - ${record.endTime}")
                                    Text("Estado: ${record.status}")
                                }
                            }
                        }
                    }
                    3 -> {
                        item { Button(onClick = onRefreshReports) { Text("Actualizar reportes") } }
                        if (state.isLoadingReports) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
                        items(state.reports, key = { "report_${it.id}" }) { report ->
                            Card(Modifier.fillMaxWidth()) {
                                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(report.category, style = MaterialTheme.typography.titleMedium)
                                    Text(report.description)
                                    Text("Ubicación: ${report.location}")
                                    Text("Estado: ${report.status}")
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        listOf("PENDING" to "Pendiente", "IN_PROGRESS" to "En proceso", "RESOLVED" to "Resuelto").forEach { (code, label) ->
                                            FilterChip(selected = report.status == code,
                                                onClick = { onChangeReportStatus(report.id, code) },
                                                label = { Text(label) })
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                state.error?.let { item { Text(it, color = MaterialTheme.colorScheme.error) } }
                state.message?.let { item { Text(it, color = MaterialTheme.colorScheme.primary) } }
            }
        }
    }
}

@Composable
private fun AdminParkingCard(
    record: AdminParkingRecord
) {
    val reservation = record.reservation

    var detailOpen by remember { mutableStateOf(false) }
    Card(onClick = { detailOpen = true }, modifier = Modifier.fillMaxWidth()) {
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
    if (detailOpen) {
        val photo by produceState<android.graphics.Bitmap?>(null, reservation.id) {
            value = try { ReservationImageRepository().load("parking", reservation.id) }
                catch (_: Exception) { null }
        }
        AlertDialog(
            onDismissRequest = { detailOpen = false },
            title = { Text("Detalle de parqueo ${reservation.parkingId}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Residente: ${record.residentName}")
                    Text("Visitante: ${reservation.visitorName}")
                    Text("Placa: ${reservation.licensePlate}")
                    Text("Marca: ${reservation.vehicleBrand} · Color: ${reservation.vehicleColor}")
                    Text("Apartamento: ${reservation.apartment}")
                    Text("${reservation.date} · ${reservation.entryTime}–${reservation.exitTime}")
                    if (photo != null) {
                        Image(bitmap = photo!!.asImageBitmap(), contentDescription = "Foto del vehículo",
                            modifier = Modifier.fillMaxWidth().height(180.dp), contentScale = ContentScale.Fit)
                    } else Text("Esta reserva no tiene fotografía guardada.")
                }
            },
            confirmButton = { TextButton(onClick = { detailOpen = false }) { Text("Cerrar") } }
        )
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
