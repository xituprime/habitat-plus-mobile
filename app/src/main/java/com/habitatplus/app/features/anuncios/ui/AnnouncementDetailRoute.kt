package com.habitatplus.app.features.anuncios.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.habitatplus.app.features.anuncios.intent.AnnouncementDetailIntent
import com.habitatplus.app.features.anuncios.viewmodel.AnnouncementDetailViewModel

@Composable
fun AnnouncementDetailRoute(
    announcementId: String?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    detailViewModel: AnnouncementDetailViewModel = viewModel()
) {
    val state by detailViewModel.state.collectAsState()

    LaunchedEffect(announcementId, detailViewModel) {
        detailViewModel.onIntent(
            AnnouncementDetailIntent.Load(announcementId)
        )
    }

    LaunchedEffect(state.backRequested) {
        if (state.backRequested) {
            detailViewModel.onIntent(
                AnnouncementDetailIntent.BackHandled
            )
            onBack()
        }
    }

    AnnouncementDetailScreen(
        announcement = state.announcement,
        isLoading = state.isLoading,
        error = state.error,
        isOffline = state.isOffline,
        onRetry = {
            detailViewModel.onIntent(
                AnnouncementDetailIntent.Retry
            )
        },
        onBack = {
            detailViewModel.onIntent(
                AnnouncementDetailIntent.Back
            )
        },
        modifier = modifier
    )
}