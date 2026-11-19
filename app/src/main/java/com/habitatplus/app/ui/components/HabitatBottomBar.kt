
package com.habitatplus.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitatplus.app.ui.theme.HabitatPlusTheme

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
                .padding(vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HabitatDestination.entries.forEach { destination ->
                val selected = destination == selectedDestination

                Column(
                    modifier = Modifier.clickable {
                        onDestinationClick(destination)
                    },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(3.dp)
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
                            contentDescription = destination.label,
                            modifier = Modifier
                                .padding(
                                    horizontal = 11.dp,
                                    vertical = 4.dp
                                )
                                .size(18.dp),
                            tint = if (selected) {
                                colors.onPrimaryContainer
                            } else {
                                colors.onSurfaceVariant
                            }
                        )
                    }

                    Text(
                        text = destination.label,
                        fontSize = 9.sp,
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
    name = "BottomBar claro",
    showBackground = true
)
@Composable
private fun HabitatBottomBarLightPreview() {
    HabitatPlusTheme(darkTheme = false) {
        HabitatBottomBar(
            selectedDestination = HabitatDestination.PARKING
        )
    }
}

@Preview(
    name = "BottomBar oscuro",
    showBackground = true
)
@Composable
private fun HabitatBottomBarDarkPreview() {
    HabitatPlusTheme(darkTheme = true) {
        HabitatBottomBar(
            selectedDestination = HabitatDestination.PARKING
        )
    }
}
