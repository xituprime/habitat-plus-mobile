
package com.habitatplus.app.features.admin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.habitatplus.app.features.admin.data.AdminParkingRecord
import com.habitatplus.app.features.admin.data.AdminParkingRepository
import com.habitatplus.app.features.admin.data.AdminResidentRepository
import com.habitatplus.app.features.admin.data.PendingResident
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminResidentsState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val residents: List<PendingResident> = emptyList(),
    val error: String? = null,
    val message: String? = null,
    val parkingReservations: List<AdminParkingRecord> = emptyList(),
    val isLoadingParking: Boolean = false
)

class AdminResidentsViewModel : ViewModel() {

    private val repository = AdminResidentRepository()
    private val parkingRepository = AdminParkingRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _state = MutableStateFlow(AdminResidentsState())
    val state: StateFlow<AdminResidentsState> = _state.asStateFlow()

    private var sessionVersion = 0

    fun loadResidents() {
        val uid = auth.currentUser?.uid ?: return
        val version = sessionVersion

        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                error = null
            )

            try {
                val residents = repository.getPendingResidents()

                if (auth.currentUser?.uid != uid ||
                    version != sessionVersion
                ) return@launch

                _state.value = _state.value.copy(
                    isLoading = false,
                    residents = residents
                )
            } catch (e: Exception) {
                if (auth.currentUser?.uid == uid &&
                    version == sessionVersion
                ) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.localizedMessage
                            ?: "No se pudieron cargar los residentes."
                    )
                }
            }
        }
    }

    fun loadParkingReservations() {
        val uid = auth.currentUser?.uid ?: return
        val version = sessionVersion

        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoadingParking = true,
                error = null
            )

            try {
                val records = parkingRepository.getReservations()

                if (auth.currentUser?.uid != uid ||
                    version != sessionVersion
                ) return@launch

                _state.value = _state.value.copy(
                    isLoadingParking = false,
                    parkingReservations = records
                )
            } catch (e: Exception) {
                if (auth.currentUser?.uid == uid &&
                    version == sessionVersion
                ) {
                    _state.value = _state.value.copy(
                        isLoadingParking = false,
                        error = e.localizedMessage
                            ?: "No se pudieron cargar los parqueos."
                    )
                }
            }
        }
    }

    fun approve(uid: String, apartment: String) {
        if (_state.value.isSaving) return

        val adminUid = auth.currentUser?.uid ?: return
        val version = sessionVersion

        viewModelScope.launch {
            _state.value = _state.value.copy(
                isSaving = true,
                error = null,
                message = null
            )

            try {
                repository.approveResidence(uid, apartment)

                if (auth.currentUser?.uid != adminUid ||
                    version != sessionVersion
                ) return@launch

                _state.value = _state.value.copy(
                    isSaving = false,
                    message = "Residencia aprobada."
                )
                loadResidents()
            } catch (e: Exception) {
                if (auth.currentUser?.uid == adminUid &&
                    version == sessionVersion
                ) {
                    _state.value = _state.value.copy(
                        isSaving = false,
                        error = e.localizedMessage
                            ?: "No se pudo aprobar la residencia."
                    )
                }
            }
        }
    }

    fun reject(uid: String) {
        if (_state.value.isSaving) return

        val adminUid = auth.currentUser?.uid ?: return
        val version = sessionVersion

        viewModelScope.launch {
            _state.value = _state.value.copy(
                isSaving = true,
                error = null,
                message = null
            )

            try {
                repository.rejectResidence(uid)

                if (auth.currentUser?.uid != adminUid ||
                    version != sessionVersion
                ) return@launch

                _state.value = _state.value.copy(
                    isSaving = false,
                    message = "Solicitud rechazada."
                )
                loadResidents()
            } catch (e: Exception) {
                if (auth.currentUser?.uid == adminUid &&
                    version == sessionVersion
                ) {
                    _state.value = _state.value.copy(
                        isSaving = false,
                        error = e.localizedMessage
                            ?: "No se pudo rechazar la solicitud."
                    )
                }
            }
        }
    }

    fun clearSession() {
        sessionVersion++
        _state.value = AdminResidentsState()
    }
}
