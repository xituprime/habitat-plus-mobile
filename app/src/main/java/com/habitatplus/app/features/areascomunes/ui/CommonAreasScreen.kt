package com.habitatplus.app.features.areascomunes.ui

import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitatplus.app.R
import com.habitatplus.app.features.areascomunes.model.CommonArea
import com.habitatplus.app.features.areascomunes.model.CommonAreaStatus
import com.habitatplus.app.features.areascomunes.state.CommonAreaState
import com.habitatplus.app.ui.theme.HabitatPlusTheme

@Composable
fun CommonAreasScreen(
    state: CommonAreaState,
    onAreaClick: (CommonArea) -> Unit = {}
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
    ) {

        // Encabezado del contenido.
        // El TopAppBar global NO pertenece a esta pantalla.

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {

            Column {

                Text(
                    text = "INSTALACIONES",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Reserva de Espacios",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {

                Text(
                    text = "${state.areas.size} áreas",
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 5.dp
                    ),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            items(
                items = state.areas,
                key = { area ->
                    area.id
                }
            ) { area ->

                CommonAreaCard(
                    area = area,
                    onClick = {
                        onAreaClick(area)
                    }
                )
            }
        }
    }
}

@Composable
private fun CommonAreaCard(
    area: CommonArea,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            // Fotografía del área

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(105.dp)
            ) {

                Image(
                    painter = painterResource(
                        id = areaImage(area.id)
                    ),
                    contentDescription = "Fotografía de ${area.name}",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(
                            RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp
                            )
                        ),
                    contentScale = ContentScale.Crop
                )

                // Estado

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surface
                ) {

                    Text(
                        text = statusText(area.status),
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        ),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = statusColor(area.status)
                    )
                }

                // Disponibilidad / dato principal

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp),
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surface
                ) {

                    Text(
                        text = area.availabilityText,
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        ),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Interior / Exterior / Solárium / Techado

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp),
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surface
                ) {

                    Text(
                        text = area.locationType,
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        ),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Column(
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 8.dp
                )
            ) {

                // Nombre

                Text(
                    text = area.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                // Descripción

                Text(
                    text = area.description,
                    fontSize = 10.sp,
                    lineHeight = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                // Características

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    area.features.forEach { feature ->

                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {

                            Text(
                                text = feature,
                                modifier = Modifier.padding(
                                    horizontal = 8.dp,
                                    vertical = 3.dp
                                ),
                                fontSize = 8.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(7.dp)
                )

                Button(
                    onClick = onClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {

                    Text(
                        text = "Ver disponibilidad",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@DrawableRes
private fun areaImage(
    areaId: String
): Int {

    return when (areaId) {

        "social-room" ->
            R.drawable.area_social_room

        "court" ->
            R.drawable.area_court

        "pool" ->
            R.drawable.area_pool

        "grill" ->
            R.drawable.area_grill

        else ->
            R.drawable.area_social_room
    }
}

private fun statusText(
    status: CommonAreaStatus
): String {

    return when (status) {

        CommonAreaStatus.AVAILABLE ->
            "● Disponible"

        CommonAreaStatus.UNAVAILABLE ->
            "● Reservada"

        CommonAreaStatus.MAINTENANCE ->
            "● Mantenimiento"
    }
}

@Composable
private fun statusColor(
    status: CommonAreaStatus
) = when (status) {

    CommonAreaStatus.AVAILABLE ->
        MaterialTheme.colorScheme.tertiary

    CommonAreaStatus.UNAVAILABLE ->
        MaterialTheme.colorScheme.error

    CommonAreaStatus.MAINTENANCE ->
        MaterialTheme.colorScheme.secondary
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Áreas - Teléfono"
)
@Composable
private fun CommonAreasPhonePreview() {

    HabitatPlusTheme {

        CommonAreasScreen(
            state = previewCommonAreaState()
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = false,
    widthDp = 393,
    heightDp = 1200,
    name = "Áreas - Vista Completa"
)
@Composable
private fun CommonAreasFullPreview() {

    HabitatPlusTheme {

        CommonAreasScreen(
            state = previewCommonAreaState()
        )
    }
}

private fun previewCommonAreaState(): CommonAreaState {

    return CommonAreaState(
        areas = listOf(

            CommonArea(
                id = "social-room",
                name = "Salón social",
                description = "Capacidad 50 personas, equipado con mobiliario, cocina auxiliar y climatización.",
                locationType = "Interior",
                availabilityText = "Hasta 50 personas",
                features = listOf(
                    "Climatizado",
                    "Cocina"
                ),
                status = CommonAreaStatus.AVAILABLE
            ),

            CommonArea(
                id = "court",
                name = "Cancha",
                description = "Cancha polideportiva sintética iluminada, disponible por turnos de 1 hora.",
                locationType = "Exterior",
                availabilityText = "Turnos de 1 hora",
                features = listOf(
                    "Iluminación LED",
                    "Césped sintético"
                ),
                status = CommonAreaStatus.AVAILABLE
            ),

            CommonArea(
                id = "pool",
                name = "Piscina",
                description = "Área acuática y solárium, normas de higiene vigentes.",
                locationType = "Solárium",
                availabilityText = "Normas vigentes",
                features = listOf(
                    "Solárium",
                    "Duchas previas"
                ),
                status = CommonAreaStatus.AVAILABLE
            ),

            CommonArea(
                id = "grill",
                name = "Churrasquera",
                description = "Parrilla techada con mesas exteriores y lavadero.",
                locationType = "Techado",
                availabilityText = "Parrilla & Mesas",
                features = listOf(
                    "Techado",
                    "Lavadero"
                ),
                status = CommonAreaStatus.AVAILABLE
            )
        )
    )
}