package com.habitatplus.app.features.parqueos.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.habitatplus.app.core.constants.AppConstants
import com.habitatplus.app.features.parqueos.viewmodel.ParkingViewModel
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.tooling.preview.Preview
import com.habitatplus.app.ui.theme.HabitatPlusTheme
import androidx.compose.material3.FloatingActionButton
import com.habitatplus.app.features.parqueos.model.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingScreen(
    viewModel: ParkingViewModel = viewModel()
){
    val state by viewModel.state.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Parqueos")
                }
            )
        },

        floatingActionButton = {

            if (state.userRole == UserRole.ADMIN) {

                FloatingActionButton(
                    onClick = {
                        // Más adelante enviaremos ParkingIntent.AddParking
                    }
                ) {
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }

            }

        }
    ) { innerPadding ->
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
    ) {

        if (state.userRole == UserRole.RESIDENT) {

            item {
                Text(
                    text = "Mis parqueos",
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            items(state.residentParkings) { parking ->
                ResidentParkingCard(
                    parking = parking
                )
            }

        } else {

            item {
                Text(
                    text = "Parqueos residentes",
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            item {
                AdminParkingSummaryCard(
                    registeredParkings = state.registeredResidentParkings
                )
            }
        }

        item {

            Text(
                modifier = Modifier.padding(top = 24.dp),
                text = "Parqueos visitantes",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        items(state.visitorParkings) { parking ->

            VisitorParkingCard(
                parking = parking
            )
        }

        item {

            Text(
                modifier = Modifier.padding(top = 24.dp),
                text = "Actividad reciente",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        items(state.recentHistory) { history ->

            HistoryCard(
                history = history
            )
        }
    }
    }
}

@Preview(showBackground = true)
@Composable
private fun ParkingScreenPreview() {
    HabitatPlusTheme {
        ParkingScreen()
    }
}
