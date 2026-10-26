
package com.habitatplus.app.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.habitatplus.app.R
import com.habitatplus.app.ui.theme.HabitatTextPrimary

@Composable
fun HabitatBackHeader(
    title: String,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick
        ) {
            Icon(
                painter = painterResource(
                    R.drawable.ic_arrow_back
                ),
                contentDescription = "Regresar",
                tint = HabitatTextPrimary
            )
        }

        Text(
            text = title,
            fontWeight = FontWeight.SemiBold,
            color = HabitatTextPrimary
        )
    }
}
