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
import com.habitatplus.app.features.anuncios.ui.AnnouncementDetailRoute
import com.habitatplus.app.features.auth.ui.SplashScreen
import com.habitatplus.app.features.auth.ui.SplashViewModel
import com.habitatplus.app.features.auth.ui.SplashDestination
import com.habitatplus.app.features.auth.ui.LoginScreen
import com.habitatplus.app.features.auth.ui.LoginViewModel
import com.habitatplus.app.features.auth.ui.RegisterScreen
import com.habitatplus.app.features.auth.ui.RegisterViewModel
import com.habitatplus.app.features.dashboard.intent.DashboardIntent
import com.habitatplus.app.features.parqueos.viewmodel.ParkingViewModel
import com.habitatplus.app.features.parqueos.intent.ParkingIntent

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
    val parkingViewModel: ParkingViewModel = viewModel()
    val parkingState by parkingViewModel.state.collectAsState()

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
        when (destination) {
            HabitatDestination.HOME -> navController.navigate(AppRoute.Home) {
                popUpTo<AppRoute.Home> { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
            HabitatDestination.PARKING -> navController.navigate(AppRoute.Parking) {
                popUpTo<AppRoute.Home> { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
            HabitatDestination.REPORTS -> navController.navigate(AppRoute.Reports) {
                popUpTo<AppRoute.Home> { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
            HabitatDestination.AREAS -> navController.navigate(AppRoute.Areas) {
                popUpTo<AppRoute.Home> { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
            HabitatDestination.PROFILE -> navController.navigate(AppRoute.Profile) {
                popUpTo<AppRoute.Home> { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
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
                    startDestination = AppRoute.Splash,
                    modifier = Modifier.fillMaxSize()
                ) {

                    composable<AppRoute.Splash> {

                        val splashViewModel: SplashViewModel = viewModel()
                        val splashState by splashViewModel.state.collectAsState()

                        SplashScreen(
                            state = splashState,
                            onEvent = splashViewModel::onEvent,
                            onNavigate = { destination ->
                                when (destination) {
                                    SplashDestination.LOGIN -> {
                                        navController.navigate(AppRoute.Login) {
                                            popUpTo<AppRoute.Splash> {
                                                inclusive = true
                                            }
                                            launchSingleTop = true
                                        }
                                    }

                                    SplashDestination.HOME -> {
                                        navController.navigate(AppRoute.Home) {
                                            popUpTo<AppRoute.Splash> {
                                                inclusive = true
                                            }
                                            launchSingleTop = true
                                        }
                                    }
                                }
                            }
                        )
                    }

                    composable<AppRoute.Login> {

                        val loginViewModel: LoginViewModel = viewModel()
                        val loginState by loginViewModel.state.collectAsState()

                        LoginScreen(
                            state = loginState,
                            onEvent = loginViewModel::onEvent,
                            onRegisterClick = {
                                navController.navigate(AppRoute.Register)
                            },
                            onLoginSuccess = {
                                navController.navigate(AppRoute.Home) {
                                    popUpTo<AppRoute.Login> {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }

                    composable<AppRoute.Register> {

                        val registerViewModel: RegisterViewModel = viewModel()
                        val registerState by registerViewModel.state.collectAsState()

                        RegisterScreen(
                            state = registerState,
                            onEvent = registerViewModel::onEvent,
                            onLoginClick = {
                                navController.popBackStack()
                            },
                            onRegisterSuccess = {
                                navController.navigate(AppRoute.Home) {
                                    popUpTo<AppRoute.Login> {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                    // ---------------------------------------------------------
                    // DASHBOARD
                    // ---------------------------------------------------------

                    composable<AppRoute.Home>() {
                        LaunchedEffect(Unit) {
                            dashboardViewModel.onIntent(
                                DashboardIntent.LoadRemoteData
                            )
                        }
                        LaunchedEffect(
                            dashboardState.httpResult,
                            dashboardState.httpError
                        ) {
                            when {
                                dashboardState.httpResult != null -> {
                                    showMessage(
                                        "Conexión HTTP exitosa: datos recibidos."
                                    )
                                }

                                dashboardState.httpError != null -> {
                                    showMessage(
                                        "Error HTTP: ${dashboardState.httpError}"
                                    )
                                }
                            }
                        }
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


                    composable<AppRoute.Parking> {
                        ParkingScreen(
                            state = parkingState,
                            onIntent = parkingViewModel::onIntent,
                            onReservationClick = {
                                navController.navigate(
                                    AppRoute.ParkingReservation
                                )
                            },
                            onHistoryClick = {
                                navController.navigate(AppRoute.ParkingHistory)
                            },
                            onDestinationClick = navigateToDestination
                        )
                    }

                    composable<AppRoute.ParkingReservation> {
                        ReservationScreen(
                            state = parkingState,
                            onIntent = parkingViewModel::onIntent,

                            onBackClick = {
                                navController.popBackStack()
                            },

                            onReservationSuccess = {
                                navController.popBackStack()
                            },

                            onDestinationClick = navigateToDestination
                        )
                    }

                    composable<AppRoute.ParkingHistory> {
                        HistoryScreen(
                            state = parkingState,
                            onBackClick = {
                                navController.popBackStack()
                            },
                            onDestinationClick = navigateToDestination,
                            onIntent = parkingViewModel::onIntent
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

                            onReportClick = { report ->
                                reportViewModel.onIntent(
                                    ReportIntent.SelectReport(report.id)
                                )

                                navController.navigate(AppRoute.ReportDetail)
                            },

                            onDestinationClick = navigateToDestination
                        )
                    }

                    composable<AppRoute.CreateReport>() {
                        CreateReportScreen(
                            state = reportState,
                            onIntent = reportViewModel::onIntent,
                            onBackClick = {
                                navController.popBackStack()
                            },
                            onReportCreated = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable<AppRoute.ReportDetail> {
                        ReportDetailScreen(
                            state = reportState,
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
                            },
                            onBackClick = {
                                navController.popBackStack()
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

                        AnnouncementDetailRoute(
                            announcementId = announcementId,
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
                            onBackClick = {
                                navController.popBackStack()
                            },
                            onSignOut = {
                                navController.navigate(AppRoute.Login) {
                                    popUpTo<AppRoute.Home> {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
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

