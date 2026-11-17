
package com.habitatplus.app.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable

@Composable
fun HabitatScaffold(
    title: String,
    selectedDestination: HabitatDestination,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    onDestinationClick: (HabitatDestination) -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    unreadCount: Int = 0,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,

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
                onBackClick = onBackClick,
                onNotificationsClick = onNotificationsClick,
                unreadCount = unreadCount
            )
        },

        bottomBar = {
            HabitatBottomBar(
                selectedDestination = selectedDestination,
                onDestinationClick = onDestinationClick
            )
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}
