
package com.habitatplus.app.features.admin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.habitatplus.app.features.admin.data.AdminParkingRecord
import com.habitatplus.app.features.admin.data.AdminParkingRepository
import com.habitatplus.app.features.admin.data.AdminResidentRepository
import com.habitatplus.app.features.admin.data.PendingResident
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminAreaRecord(
    val id: String, val residentName: String, val apartment: String,
    val areaId: String, val date: String, val startTime: String,
    val endTime: String, val status: String
)

data class AdminReportRecord(
    val id: String, val userId: String, val category: String,
    val location: String, val description: String, val status: String
)

data class AdminResidentsState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val residents: List<PendingResident> = emptyList(),
    val error: String? = null,
    val message: String? = null,
    val parkingReservations: List<AdminParkingRecord> = emptyList(),
    val isLoadingParking: Boolean = false,
    val areaReservations: List<AdminAreaRecord> = emptyList(),
    val isLoadingAreas: Boolean = false,
    val reports: List<AdminReportRecord> = emptyList(),
    val isLoadingReports: Boolean = false,
    val parkingSpaces: List<String> = emptyList(),
    val areaCatalog: List<String> = emptyList(),
    val isLoadingAreaCatalog: Boolean = false,
    val areaCatalogError: String? = null,
    val isLoadingCatalog: Boolean = false,
    val residentsError: String? = null,
    val parkingError: String? = null,
    val areasError: String? = null,
    val reportsError: String? = null,
    val catalogError: String? = null
)

class AdminResidentsViewModel : ViewModel() {

    private val repository = AdminResidentRepository()
    private val parkingRepository = AdminParkingRepository()
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _state = MutableStateFlow(AdminResidentsState())
    val state: StateFlow<AdminResidentsState> = _state.asStateFlow()

    private var sessionVersion = 0

    private suspend fun verifyAdmin() {
        val user = auth.currentUser ?: error("Debes iniciar sesión.")
        val token = user.getIdToken(true).await()
        check(token.claims["admin"] == true) {
            "Se requiere permiso de administrador."
        }
    }

    fun refreshAdminSession() {
        val uid = auth.currentUser?.uid ?: return
        val version = sessionVersion
        viewModelScope.launch {
            try {
                verifyAdmin()
                if (auth.currentUser?.uid == uid && version == sessionVersion) {
                    _state.value = _state.value.copy(error = null)
                }
            } catch (e: Exception) {
                if (auth.currentUser?.uid == uid && version == sessionVersion) {
                    _state.value = _state.value.copy(error = e.localizedMessage ?: "No se pudo verificar la sesión administrativa.")
                }
            }
        }
    }

    fun loadAreaCatalog() {
        val uid = auth.currentUser?.uid ?: return
        val version = sessionVersion
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingAreaCatalog = true, areaCatalogError = null)
            try {
                verifyAdmin()
                val docs = firestore.collection("commonAreas").get().await()
                if (auth.currentUser?.uid == uid && version == sessionVersion) {
                    _state.value = _state.value.copy(
                        isLoadingAreaCatalog = false,
                        areaCatalog = docs.documents.map { d ->
                            "${d.id} - ${d.getString("name").orEmpty()}"
                        }.sorted()
                    )
                }
            } catch (e: Exception) {
                if (auth.currentUser?.uid == uid && version == sessionVersion) {
                    _state.value = _state.value.copy(isLoadingAreaCatalog = false,
                        areaCatalogError = e.localizedMessage ?: "No se pudo consultar el catálogo de áreas.")
                }
            }
        }
    }

    fun addCommonArea(idInput: String, nameInput: String, descriptionInput: String) {
        val id = idInput.trim().lowercase(java.util.Locale.ROOT)
        val name = nameInput.trim()
        val description = descriptionInput.trim()
        if (!Regex("[a-z0-9-]{3,40}").matches(id) || name.length !in 3..80 || description.length !in 3..300) {
            _state.value = _state.value.copy(areaCatalogError = "Código (3-40 caracteres), nombre o descripción inválidos.")
            return
        }
        val uid = auth.currentUser?.uid ?: return
        val version = sessionVersion
        viewModelScope.launch {
            try {
                verifyAdmin()
                val ref = firestore.collection("commonAreas").document(id)
                firestore.runTransaction { tx ->
                    if (tx.get(ref).exists()) error("Ya existe un área con ese código.")
                    tx.set(ref, mapOf("id" to id, "name" to name,
                        "description" to description, "locationType" to "Interior",
                        "availabilityText" to "Turnos de 1 hora",
                        "features" to emptyList<String>(), "status" to "AVAILABLE"))
                }.await()
                if (auth.currentUser?.uid == uid && version == sessionVersion) {
                    _state.value = _state.value.copy(message = "Área $name creada.", areaCatalogError = null)
                    loadAreaCatalog()
                }
            } catch (e: Exception) {
                if (auth.currentUser?.uid == uid && version == sessionVersion) {
                    _state.value = _state.value.copy(areaCatalogError = e.localizedMessage ?: "No se pudo crear el área.")
                }
            }
        }
    }

    fun loadResidents() {
        val uid = auth.currentUser?.uid ?: return
        val version = sessionVersion

        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                residentsError = null
            )

            try {
                verifyAdmin()
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
                        residentsError = e.localizedMessage
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
                parkingError = null
            )

            try {
                verifyAdmin()
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
                        parkingError = e.localizedMessage
                            ?: "No se pudieron cargar los parqueos."
                    )
                }
            }
        }
    }

    fun loadAreaReservations() {
        val uid = auth.currentUser?.uid ?: return
        val version = sessionVersion
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingAreas = true, areasError = null)
            try {
                verifyAdmin()
                val docs = firestore.collection("commonAreaReservations").get().await().documents
                val users = mutableMapOf<String, Pair<String, String>>()
                val result = mutableListOf<AdminAreaRecord>()
                for (doc in docs) {
                    val residentUid = doc.getString("userId").orEmpty()
                    if (residentUid.isBlank()) continue
                    val info = users.getOrPut(residentUid) {
                        "" to ""
                    }
                    val userInfo = if (info.first.isNotBlank()) info else {
                        val user = firestore.collection("users").document(residentUid).get().await()
                        val name = listOf(user.getString("name"), user.getString("lastName"))
                            .filterNotNull().joinToString(" ").trim().ifBlank { "Residente" }
                        (name to user.getString("apartment").orEmpty()).also { users[residentUid] = it }
                    }
                    result.add(AdminAreaRecord(doc.id, userInfo.first, userInfo.second,
                        doc.getString("areaId").orEmpty(), doc.getString("date").orEmpty(),
                        doc.getString("startTime").orEmpty(), doc.getString("endTime").orEmpty(),
                        doc.getString("status").orEmpty()))
                }
                if (auth.currentUser?.uid == uid && version == sessionVersion) {
                    _state.value = _state.value.copy(isLoadingAreas = false,
                        areaReservations = result.sortedWith(compareByDescending<AdminAreaRecord> { it.date }.thenBy { it.startTime }))
                }
            } catch (e: Exception) {
                if (auth.currentUser?.uid == uid && version == sessionVersion)
                    _state.value = _state.value.copy(isLoadingAreas = false,
                        areasError = e.localizedMessage ?: "No se pudieron consultar áreas comunes.")
            }
        }
    }

    fun loadReports() {
        val uid = auth.currentUser?.uid ?: return
        val version = sessionVersion
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingReports = true, reportsError = null)
            try {
                verifyAdmin()
                val docs = firestore.collection("reports").get().await().documents
                if (auth.currentUser?.uid != uid || version != sessionVersion) return@launch
                _state.value = _state.value.copy(
                    isLoadingReports = false,
                    reports = docs.map { d ->
                        AdminReportRecord(d.id, d.getString("userId").orEmpty(),
                            d.getString("category").orEmpty(), d.getString("location").orEmpty(),
                            d.getString("description").orEmpty(), d.getString("status").orEmpty())
                    }
                )
            } catch (e: Exception) {
                if (auth.currentUser?.uid == uid && version == sessionVersion)
                    _state.value = _state.value.copy(isLoadingReports = false, reportsError = e.localizedMessage)
            }
        }
    }

    fun changeReportStatus(reportId: String, status: String) {
        if (status !in setOf("PENDING", "IN_PROGRESS", "RESOLVED")) return
        val uid = auth.currentUser?.uid ?: return
        val version = sessionVersion
        viewModelScope.launch {
            try {
                verifyAdmin()
                firestore.collection("reports").document(reportId).update(
                    mapOf("status" to status, "lastUpdate" to java.text.SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss", java.util.Locale.US).format(java.util.Date()))
                ).await()
                if (auth.currentUser?.uid == uid && version == sessionVersion) loadReports()
            } catch (e: Exception) {
                if (auth.currentUser?.uid == uid && version == sessionVersion)
                    _state.value = _state.value.copy(error = e.localizedMessage)
            }
        }
    }

    fun loadParkingCatalog() {
        val uid = auth.currentUser?.uid ?: return
        val version = sessionVersion
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingCatalog = true, catalogError = null)
            try {
                verifyAdmin()
                val docs = firestore.collection("parkingSpaces").get().await().documents
                if (auth.currentUser?.uid == uid && version == sessionVersion)
                    _state.value = _state.value.copy(isLoadingCatalog = false,
                        parkingSpaces = docs.map { it.id }.sorted())
            } catch (e: Exception) {
                if (auth.currentUser?.uid == uid && version == sessionVersion)
                    _state.value = _state.value.copy(isLoadingCatalog = false, catalogError = e.localizedMessage)
            }
        }
    }

    fun addParkingSpace(code: String) {
        val id = code.trim().uppercase(java.util.Locale.ROOT)
        if (!Regex("V-[0-9]{2}").matches(id)) {
            _state.value = _state.value.copy(error = "Usa el formato V-06.")
            return
        }
        val uid = auth.currentUser?.uid ?: return
        val version = sessionVersion
        viewModelScope.launch {
            try {
                verifyAdmin()
                firestore.collection("parkingSpaces").document(id).set(
                    mapOf("id" to id, "type" to "VISITOR", "status" to "AVAILABLE", "enabled" to true)
                ).await()
                if (auth.currentUser?.uid == uid && version == sessionVersion) {
                    _state.value = _state.value.copy(message = "Parqueo $id creado.", error = null)
                    loadParkingCatalog()
                }
            } catch (e: Exception) {
                if (auth.currentUser?.uid == uid && version == sessionVersion)
                    _state.value = _state.value.copy(error = e.localizedMessage)
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
