package com.habitatplus.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.habitatplus.app.ui.theme.ResidentTheme

@Composable
fun HabitatBottomBar(
    selectedDestination: HabitatDestination,
    onDestinationClick: (HabitatDestination) -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme

    Surface(
        color = colors.surface,
        contentColor = colors.onSurface,
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .selectableGroup(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HabitatDestination.entries.forEach { destination ->
                val selected = destination == selectedDestination

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 64.dp)
                        .selectable(
                            selected = selected,
                            role = Role.Tab,
                            onClick = {
                                onDestinationClick(destination)
                            }
                        )
                        .padding(
                            horizontal = 2.dp,
                            vertical = 8.dp
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(
                        4.dp,
                        Alignment.CenterVertically
                    )
                ) {
                    Surface(
                        color = if (selected) {
                            colors.primaryContainer
                        } else {
                            colors.surface
                        },
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            painter = painterResource(destination.iconRes),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(
                                    horizontal = 12.dp,
                                    vertical = 4.dp
                                )
                                .size(20.dp),
                            tint = if (selected) {
                                colors.onPrimaryContainer
                            } else {
                                colors.onSurfaceVariant
                            }
                        )
                    }

                    Text(
                        text = destination.label,
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                        color = if (selected) {
                            colors.primary
                        } else {
                            colors.onSurfaceVariant
                        },
                        fontWeight = if (selected) {
                            FontWeight.SemiBold
                        } else {
                            FontWeight.Normal
                        }
                    )
                }
            }
        }
    }
}

@Preview(
    name = "Barra inferior - Claro",
    showBackground = true,
    widthDp = 412
)
@Composable
private fun HabitatBottomBarLightPreview() {
    ResidentTheme(darkTheme = false) {
        HabitatBottomBar(
            selectedDestination = HabitatDestination.HOME
        )
    }
}

@Preview(
    name = "Barra inferior - Oscuro",
    showBackground = true,
    widthDp = 412
)
@Composable
private fun HabitatBottomBarDarkPreview() {
    ResidentTheme(darkTheme = true) {
        HabitatBottomBar(
            selectedDestination = HabitatDestination.PARKING
        )
    }
}