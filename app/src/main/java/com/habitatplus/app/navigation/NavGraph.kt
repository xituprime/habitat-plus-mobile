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
import androidx.compose.runtime.LaunchedEffect
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
import com.habitatplus.app.features.areascomunes.intent.CommonAreaIntent
import com.habitatplus.app.features.areascomunes.ui.AreaAvailabilityScreen
import com.habitatplus.app.features.areascomunes.ui.CommonAreasScreen
import com.habitatplus.app.features.areascomunes.ui.ConfirmReservationScreen
import com.habitatplus.app.features.areascomunes.viewmodel.CommonAreaViewModel
import com.habitatplus.app.features.configuracion.state.ThemePreference
import com.habitatplus.app.features.configuracion.ui.SettingsScreen
import com.habitatplus.app.features.configuracion.viewmodel.SettingsViewModel
import com.habitatplus.app.features.dashboard.ui.DashboardScreen
import com.habitatplus.app.features.dashboard.viewmodel.DashboardViewModel
import com.habitatplus.app.features.parqueos.ui.HistoryScreen
import com.habitatplus.app.features.parqueos.ui.ParkingScreen
import com.habitatplus.app.features.parqueos.ui.ReservationScreen
import com.habitatplus.app.features.perfil.ui.ProfileScreen
import com.habitatplus.app.features.perfil.viewmodel.ProfileViewModel
import com.habitatplus.app.features.reportes.intent.ReportIntent
import com.habitatplus.app.features.reportes.ui.CreateReportScreen
import com.habitatplus.app.features.reportes.ui.ReportDetailScreen
import com.habitatplus.app.features.reportes.ui.ReportsScreen
import com.habitatplus.app.features.reportes.viewmodel.ReportViewModel
import com.habitatplus.app.ui.components.HabitatDestination
import com.habitatplus.app.ui.theme.ResidentTheme
import kotlinx.coroutines.launch

@Composable
fun NavGraph(
    onSignOut: (() -> Unit)? = null
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // ViewModels
    val dashboardViewModel: DashboardViewModel = viewModel()
    val announcementsViewModel: AnnouncementsViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()
    val reportViewModel: ReportViewModel = viewModel()
    val commonAreaViewModel: CommonAreaViewModel = viewModel()

    // States
    val dashboardState by dashboardViewModel.state.collectAsState()
    val announcementsState by announcementsViewModel.state.collectAsState()
    val profileState by profileViewModel.state.collectAsState()
    val settingsState by settingsViewModel.state.collectAsState()
    val reportState by reportViewModel.state.collectAsState()
    val commonAreaState by commonAreaViewModel.state.collectAsState()

    // Tema
    val systemDarkTheme = isSystemInDarkTheme()

    val darkTheme = when (settingsState.theme) {
        ThemePreference.SYSTEM -> systemDarkTheme
        ThemePreference.LIGHT -> false
        ThemePreference.DARK -> true
    }

    // Snackbar
    val showMessage: (String) -> Unit = { message ->
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    // Navegación centralizada del BottomBar
    val navigateToDestination: (HabitatDestination) -> Unit = { destination ->

        val route = when (destination) {
            HabitatDestination.HOME -> ResidentRoutes.HOME
            HabitatDestination.PARKING -> Routes.PARKING
            HabitatDestination.REPORTS -> Routes.REPORTS
            HabitatDestination.AREAS -> Routes.AREAS
            HabitatDestination.PROFILE -> ResidentRoutes.PROFILE
        }

        navController.navigate(route) {
            launchSingleTop = true

            popUpTo(ResidentRoutes.HOME) {
                saveState = true
            }

            restoreState = true
        }
    }

    ResidentTheme(
        darkTheme = darkTheme
    ) {
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

                    // ---------------------------------------------------------
                    // DASHBOARD
                    // ---------------------------------------------------------

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
                                navController.navigate(
                                    Routes.PARKING
                                ) {
                                    launchSingleTop = true
                                }
                            },

                            onAreas = {
                                navController.navigate(
                                    Routes.AREAS
                                ) {
                                    launchSingleTop = true
                                }
                            },

                            onReports = {
                                navController.navigate(
                                    Routes.REPORTS
                                ) {
                                    launchSingleTop = true
                                }
                            },

                            onProfile = {
                                navController.navigate(
                                    ResidentRoutes.PROFILE
                                ) {
                                    launchSingleTop = true
                                }
                            },

                            onDestinationClick = navigateToDestination
                        )
                    }

                    // ---------------------------------------------------------
                    // PARQUEOS
                    // ---------------------------------------------------------

                    composable(Routes.PARKING) {

                        ParkingScreen(
                            onReservationClick = {
                                navController.navigate(
                                    Routes.PARKING_RESERVATION
                                )
                            },

                            onHistoryClick = {
                                navController.navigate(
                                    Routes.PARKING_HISTORY
                                )
                            },

                            onDestinationClick = navigateToDestination
                        )
                    }

                    composable(Routes.PARKING_RESERVATION) {

                        ReservationScreen(
                            onBackClick = {
                                navController.popBackStack()
                            },

                            onDestinationClick = navigateToDestination
                        )
                    }

                    composable(Routes.PARKING_HISTORY) {

                        HistoryScreen(
                            onBackClick = {
                                navController.popBackStack()
                            },

                            onDestinationClick = navigateToDestination
                        )
                    }

                    // ---------------------------------------------------------
                    // REPORTES
                    // ---------------------------------------------------------

                    composable(Routes.REPORTS) {

                        LaunchedEffect(Unit) {
                            reportViewModel.onIntent(
                                ReportIntent.LoadReports
                            )
                        }

                        ReportsScreen(
                            state = reportState,
                            onIntent = reportViewModel::onIntent,

                            onNewReportClick = {
                                navController.navigate(
                                    Routes.CREATE_REPORT
                                )
                            },

                            onReportClick = {
                                navController.navigate(
                                    Routes.REPORT_DETAIL
                                )
                            }
                        )
                    }

                    composable(Routes.CREATE_REPORT) {

                        CreateReportScreen(
                            state = reportState,
                            onIntent = reportViewModel::onIntent
                        )
                    }

                    composable(Routes.REPORT_DETAIL) {

                        ReportDetailScreen(
                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }

                    // ---------------------------------------------------------
                    // ÁREAS COMUNES
                    // ---------------------------------------------------------

                    composable(Routes.AREAS) {

                        LaunchedEffect(Unit) {
                            commonAreaViewModel.onIntent(
                                CommonAreaIntent.LoadAreas
                            )
                        }

                        CommonAreasScreen(
                            state = commonAreaState,

                            onAreaClick = { area ->

                                commonAreaViewModel.onIntent(
                                    CommonAreaIntent.SelectArea(
                                        area.id
                                    )
                                )

                                commonAreaViewModel.onIntent(
                                    CommonAreaIntent.LoadAvailability
                                )

                                navController.navigate(
                                    Routes.AREA_AVAILABILITY
                                )
                            },

                            onDestinationClick = navigateToDestination
                        )
                    }

                    composable(Routes.AREA_AVAILABILITY) {

                        AreaAvailabilityScreen(
                            state = commonAreaState,
                            onIntent = commonAreaViewModel::onIntent,

                            onContinueClick = {
                                navController.navigate(
                                    Routes.CONFIRM_AREA_RESERVATION
                                )
                            }
                        )
                    }

                    composable(
                        Routes.CONFIRM_AREA_RESERVATION
                    ) {

                        ConfirmReservationScreen(
                            state = commonAreaState,
                            onIntent = commonAreaViewModel::onIntent,

                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }

                    // ---------------------------------------------------------
                    // ANUNCIOS
                    // ---------------------------------------------------------

                    composable(
                        ResidentRoutes.ANNOUNCEMENTS
                    ) {

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

                    // ---------------------------------------------------------
                    // DETALLE DE ANUNCIO
                    // ---------------------------------------------------------

                    composable(
                        route = ResidentRoutes.ANNOUNCEMENT_DETAIL,
                        arguments = listOf(
                            navArgument("announcementId") {
                                type = NavType.StringType
                            }
                        )
                    ) { entry ->

                        val announcementId =
                            entry.arguments
                                ?.getString("announcementId")

                        val announcement =
                            announcementsState
                                .announcements
                                .firstOrNull {
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

                                        popUpTo(
                                            ResidentRoutes.HOME
                                        ) {
                                            inclusive = false
                                        }

                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }

                    // ---------------------------------------------------------
                    // PERFIL
                    // ---------------------------------------------------------

                    composable(
                        ResidentRoutes.PROFILE
                    ) {

                        ProfileScreen(
                            state = profileState,
                            onIntent = profileViewModel::onIntent,

                            onSettings = {
                                navController.navigate(
                                    ResidentRoutes.SETTINGS
                                ) {
                                    launchSingleTop = true
                                }
                            },

                            onDestinationClick = navigateToDestination
                        )
                    }

                    // ---------------------------------------------------------
                    // CONFIGURACIÓN
                    // ---------------------------------------------------------

                    composable(
                        ResidentRoutes.SETTINGS
                    ) {

                        SettingsScreen(
                            state = settingsState,
                            userName = profileState.name,
                            userEmail = profileState.email,
                            userInitials = profileState.initials,
                            residence =
                                "${profileState.tower} · " +
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