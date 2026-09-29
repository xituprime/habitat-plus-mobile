package com.habitatplus.app.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.habitatplus.app.ui.theme.HabitatBackground

@Composable
fun HabitatScaffold(
    title: String,
    selectedDestination: HabitatDestination,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        containerColor = HabitatBackground,

        contentWindowInsets = WindowInsets(
            left = 0,
            top = 0,
            right = 0,
            bottom = 0
        ),

        topBar = {
            HabitatTopBar(
                title = title,
                showBackButton = showBackButton,
                onBackClick = onBackClick
            )
        },

        bottomBar = {
            HabitatBottomBar(
                selectedDestination = selectedDestination
            )
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}