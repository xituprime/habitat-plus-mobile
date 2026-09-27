package com.habitatplus.app.features.reportes.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitatplus.app.features.reportes.intent.ReportIntent
import com.habitatplus.app.features.reportes.model.ReportCategory
import com.habitatplus.app.features.reportes.state.ReportState
import com.habitatplus.app.ui.theme.HabitatPlusTheme

@Composable
fun CreateReportScreen(
    state: ReportState,
    onIntent: (ReportIntent) -> Unit = {},
    onAddPhotoClick: () -> Unit = {}
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

        Text(
            text = "Crear Reporte",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Notifica incidencias para atención inmediata",
            fontSize = 11.sp,
            color =
                MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(
                text = "Categoría",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "Obligatorio",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(
            modifier = Modifier.height(7.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(6.dp)
        ) {

            CategoryOption(
                text = "Iluminación",
                selected =
                    state.selectedCategory ==
                            ReportCategory.LIGHTING,
                onClick = {
                    onIntent(
                        ReportIntent.SelectCategory(
                            ReportCategory.LIGHTING
                        )
                    )
                }
            )

            CategoryOption(
                text = "Agua",
                selected =
                    state.selectedCategory ==
                            ReportCategory.WATER,
                onClick = {
                    onIntent(
                        ReportIntent.SelectCategory(
                            ReportCategory.WATER
                        )
                    )
                }
            )

            CategoryOption(
                text = "Basura",
                selected =
                    state.selectedCategory ==
                            ReportCategory.TRASH,
                onClick = {
                    onIntent(
                        ReportIntent.SelectCategory(
                            ReportCategory.TRASH
                        )
                    )
                }
            )
        }

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(6.dp)
        ) {

            CategoryOption(
                text = "Ruido",
                selected =
                    state.selectedCategory ==
                            ReportCategory.NOISE,
                onClick = {
                    onIntent(
                        ReportIntent.SelectCategory(
                            ReportCategory.NOISE
                        )
                    )
                }
            )

            CategoryOption(
                text = "Otro",
                selected =
                    state.selectedCategory ==
                            ReportCategory.OTHER,
                onClick = {
                    onIntent(
                        ReportIntent.SelectCategory(
                            ReportCategory.OTHER
                        )
                    )
                }
            )
        }

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = "Descripción",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "${state.description.length}/500",
                fontSize = 10.sp,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        OutlinedTextField(
            value = state.description,
            onValueChange = { description ->

                if (description.length <= 500) {

                    onIntent(
                        ReportIntent.UpdateDescription(
                            description
                        )
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
            placeholder = {
                Text(
                    text = "Detalla el problema observado, ubicación específica o desperfecto...",
                    fontSize = 11.sp
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor =
                    MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor =
                    MaterialTheme.colorScheme.surfaceVariant
            )
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        Text(
            text = "Fotografía de Evidencia",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(105.dp)
                .clickable {
                    onAddPhotoClick()
                },
            shape = RoundedCornerShape(16.dp),
            color =
                MaterialTheme.colorScheme.surfaceVariant
        ) {

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement =
                    Arrangement.Center,
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Agregar fotografía de evidencia",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Tomar fotografía o seleccionar desde galería",
                    fontSize = 9.sp,
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Opcional",
                    fontSize = 9.sp,
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        Button(
            onClick = {
                onIntent(
                    ReportIntent.SubmitReport
                )
            },
            enabled =
                !state.isSubmitting &&
                        state.selectedCategory != null &&
                        state.description.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor =
                    MaterialTheme.colorScheme.primary
            )
        ) {

            Text(
                text = if (state.isSubmitting) {
                    "Enviando..."
                } else {
                    "Enviar Reporte"
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color =
                MaterialTheme.colorScheme.primaryContainer
        ) {

            Column(
                modifier = Modifier.padding(12.dp)
            ) {

                Text(
                    text = "Gestión centralizada",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "El personal administrativo recibirá este ticket al instante.",
                    fontSize = 9.sp,
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )
    }
}

@Composable
private fun CategoryOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Surface(
        modifier = Modifier.clickable {
            onClick()
        },
        shape = RoundedCornerShape(50),
        color = if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        }
    ) {

        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 6.dp
            ),
            fontSize = 10.sp,
            fontWeight = if (selected) {
                FontWeight.SemiBold
            } else {
                FontWeight.Normal
            }
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Crear Reporte"
)
@Composable
private fun CreateReportScreenPreview() {

    HabitatPlusTheme {

        CreateReportScreen(
            state = ReportState()
        )
    }
}