
package com.habitatplus.app.features.reportes.ui

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitatplus.app.R
import com.habitatplus.app.features.reportes.model.Report
import com.habitatplus.app.features.reportes.model.ReportCategory
import com.habitatplus.app.features.reportes.model.ReportStatus
import com.habitatplus.app.features.reportes.state.ReportState
import com.habitatplus.app.ui.theme.HabitatPlusTheme

@Composable
fun ReportDetailScreen(
    onBackClick: () -> Unit = {},
    state: ReportState = ReportState()
) {
    val report = state.selectedReport

    if (report == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No se encontró el reporte",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                onClick = onBackClick,
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "← Volver a reportes",
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        return
    }

    val category = reportCategoryText(report.category)
    val status = reportStatusText(report.status)

    val progress = when (report.status) {
        ReportStatus.PENDING -> 0.25f
        ReportStatus.IN_PROGRESS -> 0.75f
        ReportStatus.RESOLVED -> 1f
    }

    val progressTitle = when (report.status) {
        ReportStatus.PENDING -> "Paso 1 de 4: Registrado"
        ReportStatus.IN_PROGRESS -> "Paso 3 de 4: En Curso"
        ReportStatus.RESOLVED -> "Paso 4 de 4: Resuelto"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = onBackClick,
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = "←  Reportes",
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 6.dp
                    ),
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "TICKET #${report.id}",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatusPill(text = category)

            Spacer(modifier = Modifier.weight(1f))

            StatusPill(text = status)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = progressTitle,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "${(progress * 100).toInt()}%",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(50))
                )

                Spacer(modifier = Modifier.height(7.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ProgressLabel(
                        text = "Registrado",
                        selected = true
                    )

                    ProgressLabel(
                        text = "Asignado",
                        selected = report.status != ReportStatus.PENDING
                    )

                    ProgressLabel(
                        text = "En Curso",
                        selected = report.status != ReportStatus.PENDING
                    )

                    ProgressLabel(
                        text = "Resuelto",
                        selected = report.status == ReportStatus.RESOLVED
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        SectionLabel(
            text = "DESCRIPCIÓN DEL INCIDENTE"
        )

        Spacer(modifier = Modifier.height(6.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Text(
                text = report.description,
                modifier = Modifier.padding(16.dp),
                fontSize = 11.sp,
                lineHeight = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            SectionLabel(
                text = "EVIDENCIA FOTOGRÁFICA"
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = if (report.hasPhoto) {
                    "1 archivo JPG"
                } else {
                    "Sin archivos"
                },
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (report.hasPhoto) {
            Image(
                painter = painterResource(
                    id = R.drawable.report_evidence_hallway
                ),
                contentDescription = "Fotografía de evidencia de demostración",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "Este reporte no tiene fotografía adjunta",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                InfoRow(
                    title = "Fecha de creación",
                    value = report.date
                )

                Spacer(modifier = Modifier.height(10.dp))

                InfoRow(
                    title = "Última actualización",
                    value = report.lastUpdate
                )

                Spacer(modifier = Modifier.height(10.dp))

                InfoRow(
                    title = "Ubicación",
                    value = report.location
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val isLightingDemo = report.id == "REP-2041"

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Bitácora de Seguimiento",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(1f))

            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = if (isLightingDemo) {
                        "3 registros"
                    } else {
                        "1 registro"
                    },
                    modifier = Modifier.padding(
                        horizontal = 8.dp,
                        vertical = 4.dp
                    ),
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (isLightingDemo) {
            TimelineItem(
                number = "1",
                title = "Técnico Asignado",
                time = "11:30 AM",
                description = "Administración asignó el caso a Carlos Méndez (Mantenimiento Eléctrico). Visita técnica programada para hoy a las 15:00 hrs.",
                extra = "Contacto interno técnico disponible"
            )

            TimelineItem(
                number = "2",
                title = "Validación de Reporte",
                time = "10:05 AM",
                description = "Supervisor de piso validó la falla de balastro en luminaria #LED-P2-04. Nivel de prioridad asignado: Medio."
            )

            TimelineItem(
                number = "3",
                title = "Reporte Ingresado",
                time = "09:15 AM",
                description = "Generado por residente Mariana Soto (Depto A-203) mediante canal móvil."
            )
        } else {
            TimelineItem(
                number = "1",
                title = if (report.status == ReportStatus.RESOLVED) {
                    "Reporte Resuelto"
                } else {
                    "Reporte Registrado"
                },
                time = report.date,
                description = when (report.status) {
                    ReportStatus.PENDING ->
                        "El reporte fue registrado y está pendiente de atención."

                    ReportStatus.IN_PROGRESS ->
                        "El reporte se encuentra en proceso de atención."

                    ReportStatus.RESOLVED ->
                        "El reporte fue marcado como resuelto."
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun StatusPill(
    text: String
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 5.dp
            ),
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ProgressLabel(
    text: String,
    selected: Boolean = false
) {
    Text(
        text = text,
        fontSize = 8.sp,
        fontWeight = if (selected) {
            FontWeight.Bold
        } else {
            FontWeight.Normal
        },
        color = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
    )
}

@Composable
private fun SectionLabel(
    text: String
) {
    Text(
        text = text,
        fontSize = 9.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun InfoRow(
    title: String,
    value: String
) {
    Column {
        Text(
            text = title,
            fontSize = 9.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            fontSize = 10.sp,
            lineHeight = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun TimelineItem(
    number: String,
    title: String,
    time: String,
    description: String,
    extra: String? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = number,
                    modifier = Modifier.padding(6.dp),
                    fontSize = 8.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(78.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer
                    )
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = title,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = time,
                        fontSize = 8.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = description,
                    fontSize = 9.sp,
                    lineHeight = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (extra != null) {
                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = extra,
                        fontSize = 8.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

private fun reportCategoryText(
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

private fun reportStatusText(
    status: ReportStatus
): String {
    return when (status) {
        ReportStatus.PENDING -> "Pendiente"
        ReportStatus.IN_PROGRESS -> "En proceso"
        ReportStatus.RESOLVED -> "Resuelto"
    }
}

private fun detailPreviewState(): ReportState {
    val report = Report(
        id = "REP-2041",
        category = ReportCategory.LIGHTING,
        description = "La luz del pasillo principal parpadea continuamente desde ayer por la tarde, dificultando el tránsito de noche.",
        location = "Torre A - Piso 3",
        date = "14 Oct, 2024",
        lastUpdate = "14 Oct, 2024 • 11:30 AM",
        status = ReportStatus.PENDING,
        hasPhoto = true
    )

    return ReportState(
        reports = listOf(report),
        selectedReportId = report.id
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Detalle - Teléfono"
)
@Composable
private fun ReportDetailPhonePreview() {
    HabitatPlusTheme {
        ReportDetailScreen(
            state = detailPreviewState()
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = false,
    widthDp = 393,
    heightDp = 1200,
    name = "Detalle - Vista Completa"
)
@Composable
private fun ReportDetailFullPreview() {
    HabitatPlusTheme {
        ReportDetailScreen(
            state = detailPreviewState()
        )
    }
}
