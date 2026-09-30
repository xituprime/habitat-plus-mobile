package com.habitatplus.app.features.reportes.ui

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
import androidx.compose.foundation.Image
import com.habitatplus.app.R
import com.habitatplus.app.ui.theme.HabitatPlusTheme

@Composable
fun ReportDetailScreen(
    onBackClick: () -> Unit = {}
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
            .verticalScroll(
                rememberScrollState()
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

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "TICKET #REP-2024-884",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            StatusPill(
                text = "Iluminación"
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            StatusPill(
                text = "Pendiente"
            )
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

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
                        text = "Paso 2 de 4: En Asignación",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "50%",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                LinearProgressIndicator(
                    progress = { 0.5f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(
                            RoundedCornerShape(50)
                        )
                )

                Spacer(
                    modifier = Modifier.height(7.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {

                    ProgressLabel(
                        text = "Registrado"
                    )

                    ProgressLabel(
                        text = "Asignado",
                        selected = true
                    )

                    ProgressLabel(
                        text = "En Curso"
                    )

                    ProgressLabel(
                        text = "Resuelto"
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        SectionLabel(
            text = "DESCRIPCIÓN DEL INCIDENTE"
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {

            Text(
                text = "La luminaria LED del pasillo del piso 2 (frente al depto A-203) parpadea constantemente desde anoche, reduciendo la visibilidad.",
                modifier = Modifier.padding(16.dp),
                fontSize = 11.sp,
                lineHeight = 14.sp
            )
        }

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            SectionLabel(
                text = "EVIDENCIA FOTOGRÁFICA"
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "1 archivo JPG",
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        // Fotografía real de muestra para la vista.
        // Más adelante será reemplazada por la imagen
        // asociada al reporte desde cámara/Firebase.

        Image(
            painter = painterResource(
                id = R.drawable.report_evidence_hallway
            ),
            contentDescription =
                "Evidencia fotográfica del reporte",
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .clip(
                    RoundedCornerShape(16.dp)
                ),
            contentScale = ContentScale.Crop
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

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
                    value =
                        "14 de Octubre de 2024, 09:15 AM"
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                InfoRow(
                    title = "Última actualización",
                    value =
                        "14 de Octubre de 2024, 11:30 AM\nAsignado a técnico de mantenimiento"
                )
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Bitácora de Seguimiento",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Surface(
                shape = RoundedCornerShape(50),
                color =
                    MaterialTheme.colorScheme.primaryContainer
            ) {

                Text(
                    text = "3 registros",
                    modifier = Modifier.padding(
                        horizontal = 8.dp,
                        vertical = 4.dp
                    ),
                    fontSize = 9.sp,
                    color =
                        MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        TimelineItem(
            number = "1",
            title = "Técnico Asignado",
            time = "11:30 AM",
            description = "Administración asignó el caso a Carlos Méndez (Mantenimiento Eléctrico). Visita técnica programada para hoy a las 15:00 hrs.",
            extra =
                "Contacto interno técnico disponible"
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

        Spacer(
            modifier = Modifier.height(16.dp)
        )
    }
}

@Composable
private fun StatusPill(
    text: String
) {

    Surface(
        shape = RoundedCornerShape(50),
        color =
            MaterialTheme.colorScheme.primaryContainer
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
        color =
            MaterialTheme.colorScheme.onSurfaceVariant
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
            color =
                MaterialTheme.colorScheme.onSurfaceVariant
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
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Surface(
                shape = RoundedCornerShape(50),
                color =
                    MaterialTheme.colorScheme.primaryContainer
            ) {

                Text(
                    text = number,
                    modifier = Modifier.padding(6.dp),
                    fontSize = 8.sp,
                    color =
                        MaterialTheme.colorScheme.primary
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

        Spacer(
            modifier = Modifier.width(8.dp)
        )

        Surface(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(16.dp),
            color =
                MaterialTheme.colorScheme.surface
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
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = time,
                        fontSize = 8.sp,
                        color =
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = description,
                    fontSize = 9.sp,
                    lineHeight = 12.sp,
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (extra != null) {

                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )

                    Text(
                        text = extra,
                        fontSize = 8.sp,
                        color =
                            MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Detalle - Teléfono"
)
@Composable
private fun ReportDetailPhonePreview() {

    HabitatPlusTheme {
        ReportDetailScreen()
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
        ReportDetailScreen()
    }
}