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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
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


    val navigateToDestination: (HabitatDestination) -> Unit = { destination ->

        val route = when (destination) {
            HabitatDestination.HOME -> AppRoute.Home
            HabitatDestination.PARKING -> AppRoute.Parking
            HabitatDestination.REPORTS -> AppRoute.Reports
            HabitatDestination.AREAS -> AppRoute.Areas
            HabitatDestination.PROFILE -> AppRoute.Profile
        }

        navController.navigate(route) {
            launchSingleTop = true

            popUpTo(AppRoute.Home) {
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
                    startDestination = AppRoute.Home,
                    modifier = Modifier.fillMaxSize()
                ) {

                    // ---------------------------------------------------------
                    // DASHBOARD
                    // ---------------------------------------------------------

                    composable<AppRoute.Home>() {

                        DashboardScreen(
                            state = dashboardState,
                            onIntent = dashboardViewModel::onIntent,

                            onAnnouncements = {
                                navController.navigate(
                                    AppRoute.Announcements
                                ) {
                                    launchSingleTop = true
                                }
                            },

                            onAnnouncement = { id ->
                                navController.navigate(
                                    AppRoute.AnnouncementDetail(id.toString())
                                ) {
                                    launchSingleTop = true
                                }
                            },

                            onParking = {
                                navController.navigate(
                                    AppRoute.Parking
                                ) {
                                    launchSingleTop = true
                                }
                            },

                            onAreas = {
                                navController.navigate(
                                    AppRoute.Areas
                                ) {
                                    launchSingleTop = true
                                }
                            },

                            onReports = {
                                navController.navigate(
                                    AppRoute.Reports
                                ) {
                                    launchSingleTop = true
                                }
                            },

                            onProfile = {
                                navController.navigate(
                                    AppRoute.Profile
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

                    composable<AppRoute.Parking>() {

                        ParkingScreen(
                            onReservationClick = {
                                navController.navigate(
                                    AppRoute.ParkingReservation
                                )
                            },

                            onHistoryClick = {
                                navController.navigate(
                                    AppRoute.ParkingHistory
                                )
                            },

                            onDestinationClick = navigateToDestination
                        )
                    }

                    composable<AppRoute.ParkingReservation>() {

                        ReservationScreen(
                            onBackClick = {
                                navController.popBackStack()
                            },

                            onDestinationClick = navigateToDestination
                        )
                    }

                    composable<AppRoute.ParkingHistory>() {

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

                    composable<AppRoute.Reports>() {

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
                                    AppRoute.CreateReport
                                )
                            },

                            onReportClick = {
                                navController.navigate(
                                    AppRoute.ReportDetail
                                )
                            }
                        )
                    }

                    composable<AppRoute.CreateReport>() {

                        CreateReportScreen(
                            state = reportState,
                            onIntent = reportViewModel::onIntent
                        )
                    }

                    composable<AppRoute.ReportDetail>() {

                        ReportDetailScreen(
                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }

                    // ---------------------------------------------------------
                    // ÁREAS COMUNES
                    // ---------------------------------------------------------

                    composable<AppRoute.Areas>() {

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
                                    AppRoute.AreaAvailability
                                )
                            },

                            onDestinationClick = navigateToDestination
                        )
                    }

                    composable<AppRoute.AreaAvailability>() {

                        AreaAvailabilityScreen(
                            state = commonAreaState,
                            onIntent = commonAreaViewModel::onIntent,

                            onContinueClick = {
                                navController.navigate(
                                    AppRoute.ConfirmAreaReservation
                                )
                            }
                        )
                    }

                    composable<AppRoute.ConfirmAreaReservation>() {

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

                    composable<AppRoute.Announcements>() {

                        AnnouncementsScreen(
                            state = announcementsState,
                            onIntent = announcementsViewModel::onIntent,

                            onAnnouncement = { id ->
                                navController.navigate(
                                    AppRoute.AnnouncementDetail(id.toString())
                                ) {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }

                    // ---------------------------------------------------------
                    // DETALLE DE ANUNCIO
                    // ---------------------------------------------------------

                    composable<AppRoute.AnnouncementDetail> { entry ->

                        val announcementId =
                            entry.toRoute<AppRoute.AnnouncementDetail>().announcementId

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
                                        AppRoute.Announcements,
                                        false
                                    )

                                if (!returnedToList) {

                                    navController.navigate(
                                        AppRoute.Announcements
                                    ) {

                                        popUpTo(
                                            AppRoute.Home
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

                    composable<AppRoute.Profile>() {

                        ProfileScreen(
                            state = profileState,
                            onIntent = profileViewModel::onIntent,

                            onSettings = {
                                navController.navigate(
                                    AppRoute.Settings
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

                    composable<AppRoute.Settings>() {

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