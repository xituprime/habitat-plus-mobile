package com.habitatplus.app.features.reportes.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitatplus.app.features.reportes.intent.ReportIntent
import com.habitatplus.app.features.reportes.model.Report
import com.habitatplus.app.features.reportes.model.ReportCategory
import com.habitatplus.app.features.reportes.model.ReportStatus
import com.habitatplus.app.features.reportes.state.ReportState
import com.habitatplus.app.ui.components.HabitatDestination
import com.habitatplus.app.ui.components.HabitatScaffold
import com.habitatplus.app.ui.theme.HabitatPlusTheme

@Composable
fun ReportsScreen(
    state: ReportState,
    onIntent: (ReportIntent) -> Unit = {},
    onNewReportClick: () -> Unit = {},
    onReportClick: (Report) -> Unit = {},
    onDestinationClick: (HabitatDestination) -> Unit = {}
) {

    val filteredReports = state.reports.filter { report ->

        val category = categoryText(report.category)

        val matchesSearch =
            state.searchQuery.isBlank() ||
                    report.description.contains(
                        state.searchQuery,
                        ignoreCase = true
                    ) ||
                    category.contains(
                        state.searchQuery,
                        ignoreCase = true
                    )

        val matchesStatus =
            state.selectedStatus == null ||
                    report.status == state.selectedStatus

        matchesSearch && matchesStatus
    }

    HabitatScaffold(
        title = "Reportes",
        selectedDestination = HabitatDestination.REPORTS,
        onDestinationClick = onDestinationClick
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    MaterialTheme.colorScheme.background
                )
                .padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Reportes de Incidencias",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "${state.reports.count { it.status != ReportStatus.RESOLVED }} activos",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = "Gestione y dé seguimiento a las solicitudes de mantenimiento de la comunidad.",
                fontSize = 12.sp,
                lineHeight = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = {
                    onIntent(
                        ReportIntent.SearchReports(it)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                placeholder = {
                    Text(
                        text = "Buscar por descripción o categoría...",
                        fontSize = 12.sp
                    )
                },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor =
                        MaterialTheme.colorScheme.primary,
                    unfocusedContainerColor =
                        MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor =
                        MaterialTheme.colorScheme.surfaceVariant
                )
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(
                        rememberScrollState()
                    ),
                horizontalArrangement =
                    Arrangement.spacedBy(6.dp)
            ) {

                ReportFilterChip(
                    text = "Todos",
                    selected = state.selectedStatus == null,
                    onClick = {
                        onIntent(
                            ReportIntent.FilterByStatus(null)
                        )
                    }
                )

                ReportFilterChip(
                    text = "Pendientes",
                    selected =
                        state.selectedStatus ==
                                ReportStatus.PENDING,
                    onClick = {
                        onIntent(
                            ReportIntent.FilterByStatus(
                                ReportStatus.PENDING
                            )
                        )
                    }
                )

                ReportFilterChip(
                    text = "En proceso",
                    selected =
                        state.selectedStatus ==
                                ReportStatus.IN_PROGRESS,
                    onClick = {
                        onIntent(
                            ReportIntent.FilterByStatus(
                                ReportStatus.IN_PROGRESS
                            )
                        )
                    }
                )

                ReportFilterChip(
                    text = "Resueltos",
                    selected =
                        state.selectedStatus ==
                                ReportStatus.RESOLVED,
                    onClick = {
                        onIntent(
                            ReportIntent.FilterByStatus(
                                ReportStatus.RESOLVED
                            )
                        )
                    }
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                items(
                    items = filteredReports,
                    key = { it.id }
                ) { report ->

                    ReportCard(
                        report = report,
                        onClick = {
                            onReportClick(report)
                        }
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {

                Button(
                    onClick = onNewReportClick,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            MaterialTheme.colorScheme.primary
                    )
                ) {

                    Text(
                        text = "+  Nuevo Reporte",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun ReportFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = text,
                fontSize = 11.sp
            )
        },
        shape = RoundedCornerShape(50),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor =
                MaterialTheme.colorScheme.primary,
            selectedLabelColor =
                MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
private fun ReportCard(
    report: Report,
    onClick: () -> Unit
) {

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "${categoryText(report.category)}  •  ${report.location}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = "Ref: #${report.id}",
                        fontSize = 9.sp,
                        color =
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                ReportStatusBadge(
                    status = report.status
                )
            }

            Spacer(
                modifier = Modifier.height(7.dp)
            )

            Text(
                text = visualReportTitle(report),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = report.description,
                fontSize = 11.sp,
                lineHeight = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = report.date,
                    fontSize = 10.sp,
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = statusActionText(report.status),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color =
                        MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ReportStatusBadge(
    status: ReportStatus
) {

    val text = when (status) {
        ReportStatus.PENDING -> "Pendiente"
        ReportStatus.IN_PROGRESS -> "En proceso"
        ReportStatus.RESOLVED -> "Resuelto"
    }

    Text(
        text = text,
        fontSize = 9.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary
    )
}

private fun visualReportTitle(
    report: Report
): String {

    return when (report.id) {

        "REP-2041" ->
            "Falla en luminaria de pasillo Torre A"

        "REP-2038" ->
            "Ruido excesivo en extractor de aire sótano"

        "REP-2015" ->
            "Fuga de agua en llave de jardín central"

        else ->
            categoryText(report.category)
    }
}

private fun statusActionText(
    status: ReportStatus
): String {

    return when (status) {
        ReportStatus.PENDING -> "Ver detalle >"
        ReportStatus.IN_PROGRESS -> "Técnico asignado >"
        ReportStatus.RESOLVED -> "Solucionado"
    }
}

private fun categoryText(
    category: ReportCategory
): String {

    return when (category) {
        ReportCategory.LIGHTING -> "Iluminación"
        ReportCategory.WATER -> "Agua"
        ReportCategory.TRASH -> "Basura"
        ReportCategory.NOISE -> "Ruido"
        ReportCategory.OTHER -> "Otro"
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,dpi=420",
    name = "Reportes"
)
@Composable
private fun ReportsScreenPreview() {

    HabitatPlusTheme {

        ReportsScreen(
            state = ReportState(
                reports = previewReports()
            )
        )
    }
}

private fun previewReports(): List<Report> {

    return listOf(

        Report(
            id = "REP-2041",
            category = ReportCategory.LIGHTING,
            description = "La luz del pasillo principal parpadea continuamente desde ayer por la tarde, dificultando el tránsito de noche.",
            location = "Torre A - Piso 3",
            date = "14 Oct, 2024",
            lastUpdate = "14 Oct, 2024 • 11:30 AM",
            status = ReportStatus.PENDING,
            hasPhoto = true
        ),

        Report(
            id = "REP-2038",
            category = ReportCategory.NOISE,
            description = "El motor de extracción emite una vibración metálica constante perceptible en los apartamentos de planta baja.",
            location = "Subsuelo 1",
            date = "12 Oct, 2024",
            lastUpdate = "13 Oct, 2024 • 09:20 AM",
            status = ReportStatus.IN_PROGRESS
        ),

        Report(
            id = "REP-2015",
            category = ReportCategory.WATER,
            description = "Se completó el cambio de empaque y calibración de válvula. Área verificada sin goteo residual.",
            location = "Áreas Comunes",
            date = "08 Oct, 2024",
            lastUpdate = "09 Oct, 2024 • 04:10 PM",
            status = ReportStatus.RESOLVED,
            hasPhoto = true
        )
    )
}