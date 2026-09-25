package com.habitatplus.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.habitatplus.app.core.components.AppScaffold
import com.habitatplus.app.features.dashboard.intent.DashboardIntent
import com.habitatplus.app.features.dashboard.ui.DashboardScreen
import com.habitatplus.app.features.dashboard.viewmodel.DashboardViewModel

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            val dashboardViewModel: DashboardViewModel = viewModel()
            val state by dashboardViewModel.state.collectAsState()

            LaunchedEffect(dashboardViewModel) {
                dashboardViewModel.onIntent(
                    DashboardIntent.LoadDashboard
                )
            }

            AppScaffold { paddingValues ->
                DashboardScreen(
                    state = state,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}