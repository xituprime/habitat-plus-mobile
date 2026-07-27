package com.habitatplus.app.features.parqueos.viewmodel

import androidx.lifecycle.ViewModel
import com.habitatplus.app.features.parqueos.state.ParkingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ParkingViewModel {

    private val _state = MutableStateFlow(ParkingState())

    val state: StateFlow<ParkingState> = _state.asStateFlow()

}
