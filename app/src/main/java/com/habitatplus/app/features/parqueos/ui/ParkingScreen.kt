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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.habitatplus.app.core.constants.AppConstants
import com.habitatplus.app.features.parqueos.viewmodel.ParkingViewModel

@Composable
fun ParkingScreen(
    viewModel: ParkingViewModel = viewModel()
){
    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        item {
            Text(
                text = "Mis parqueos",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        items(state.residentParkings){ parking ->

            ResidentParkingCard(
                parking = parking
            )
        }

        item {

            Text(
                modifier = Modifier.padding(top = 24.dp),
                text = "Parqueos visitantes",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        items(state.visitorParkings){ parking ->

            VisitorParkingCard(
                parking = parking
            )
        }

        item{

            Text(
                modifier = Modifier.padding(top = 24.dp),
                text = "Actividad reciente",
                style = MaterialTheme.typography.headlineSmall
                )
        }

        items(state.recentHistory){ history ->

            HistoryCard(
                history = history
            )
        }
    }
}
