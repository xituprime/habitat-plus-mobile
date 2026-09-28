package com.habitatplus.app.navigation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.habitatplus.app.core.components.AppScaffold
import com.habitatplus.app.features.anuncios.intent.AnnouncementsIntent
import com.habitatplus.app.features.anuncios.ui.AnnouncementDetailScreen
import com.habitatplus.app.features.anuncios.ui.AnnouncementsScreen
import com.habitatplus.app.features.anuncios.viewmodel.AnnouncementsViewModel
import com.habitatplus.app.features.configuracion.state.ThemePreference
import com.habitatplus.app.features.configuracion.ui.SettingsScreen
import com.habitatplus.app.features.configuracion.viewmodel.SettingsViewModel
import com.habitatplus.app.features.dashboard.ui.DashboardScreen
import com.habitatplus.app.features.dashboard.viewmodel.DashboardViewModel
import com.habitatplus.app.features.perfil.ui.ProfileScreen
import com.habitatplus.app.features.perfil.viewmodel.ProfileViewModel
import com.habitatplus.app.ui.theme.ResidentTheme
import kotlinx.coroutines.launch

@Composable
fun NavGraph(
    onOpenParking: (() -> Unit)? = null,
    onOpenReports: (() -> Unit)? = null,
    onOpenAreas: (() -> Unit)? = null,
    onSignOut: (() -> Unit)? = null
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val dashboardViewModel: DashboardViewModel = viewModel()
    val announcementsViewModel: AnnouncementsViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()

    val dashboardState by dashboardViewModel.state.collectAsState()
    val announcementsState by announcementsViewModel.state.collectAsState()
    val profileState by profileViewModel.state.collectAsState()
    val settingsState by settingsViewModel.state.collectAsState()

    val systemDarkTheme = isSystemInDarkTheme()

    val darkTheme = when (settingsState.theme) {
        ThemePreference.SYSTEM -> systemDarkTheme
        ThemePreference.LIGHT -> false
        ThemePreference.DARK -> true
    }

    val showMessage: (String) -> Unit = { message ->
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    ResidentTheme(darkTheme = darkTheme) {
        AppScaffold { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
            ) {
                NavHost(
                    navController = navController,
                    startDestination = ResidentRoutes.HOME,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable(ResidentRoutes.HOME) {
                        DashboardScreen(
                            state = dashboardState,
                            onIntent = dashboardViewModel::onIntent,

                            onAnnouncements = {
                                navController.navigate(
                                    ResidentRoutes.ANNOUNCEMENTS
                                ) {
                                    launchSingleTop = true
                                }
                            },

                            onAnnouncement = { id ->
                                navController.navigate(
                                    ResidentRoutes.announcement(id)
                                ) {
                                    launchSingleTop = true
                                }
                            },

                            onParking = {
                                if (onOpenParking != null) {
                                    onOpenParking()
                                } else {
                                    showMessage(
                                        "Parqueos está pendiente de integración."
                                    )
                                }
                            },

                            onAreas = {
                                if (onOpenAreas != null) {
                                    onOpenAreas()
                                } else {
                                    showMessage(
                                        "Áreas está pendiente de integración."
                                    )
                                }
                            },

                            onReports = {
                                if (onOpenReports != null) {
                                    onOpenReports()
                                } else {
                                    showMessage(
                                        "Reportes está pendiente de integración."
                                    )
                                }
                            },

                            onProfile = {
                                navController.navigate(
                                    ResidentRoutes.PROFILE
                                ) {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }

                    composable(ResidentRoutes.ANNOUNCEMENTS) {
                        AnnouncementsScreen(
                            state = announcementsState,
                            onIntent = announcementsViewModel::onIntent,
                            onAnnouncement = { id ->
                                navController.navigate(
                                    ResidentRoutes.announcement(id)
                                ) {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }

                    composable(
                        route = ResidentRoutes.ANNOUNCEMENT_DETAIL,
                        arguments = listOf(
                            navArgument("announcementId") {
                                type = NavType.StringType
                            }
                        )
                    ) { entry ->
                        val announcementId = entry.arguments
                            ?.getString("announcementId")

                        val announcement =
                            announcementsState.announcements.firstOrNull {
                                it.id == announcementId
                            }

                        AnnouncementDetailScreen(
                            announcement = announcement,
                            isLoading = announcementsState.isLoading,
                            error = announcementsState.error,
                            isOffline = announcementsState.isOffline,

                            onRetry = {
                                announcementsViewModel.onIntent(
                                    AnnouncementsIntent.Refresh
                                )
                            },

                            onBack = {
                                val returnedToList =
                                    navController.popBackStack(
                                        ResidentRoutes.ANNOUNCEMENTS,
                                        false
                                    )

                                if (!returnedToList) {
                                    navController.navigate(
                                        ResidentRoutes.ANNOUNCEMENTS
                                    ) {
                                        popUpTo(ResidentRoutes.HOME) {
                                            inclusive = false
                                        }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }

                    composable(ResidentRoutes.PROFILE) {
                        ProfileScreen(
                            state = profileState,
                            onIntent = profileViewModel::onIntent,
                            onSettings = {
                                navController.navigate(
                                    ResidentRoutes.SETTINGS
                                ) {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }

                    composable(ResidentRoutes.SETTINGS) {
                        SettingsScreen(
                            state = settingsState,
                            userName = profileState.name,
                            userEmail = profileState.email,
                            userInitials = profileState.initials,
                            residence = "${profileState.tower} · " +
                                    profileState.apartment,
                            onIntent = settingsViewModel::onIntent,
                            onSignOut = {
                                if (onSignOut != null) {
                                    onSignOut()
                                } else {
                                    showMessage(
                                        "Firebase Auth y Login están pendientes " +
                                                "de integración. No se cerró la sesión."
                                    )
                                }
                            }
                        )
                    }
                }

                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .imePadding()
                        .padding(16.dp)
                )
            }
        }
    }
}