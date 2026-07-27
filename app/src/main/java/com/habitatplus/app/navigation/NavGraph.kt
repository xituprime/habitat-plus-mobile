package com.habitatplus.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.habitatplus.app.features.parqueos.ui.ParkingScreen

@Composable
fun NavGraph(){

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ){
        composable(Routes.HOME) {
            ParkingScreen()
        }
    }
}