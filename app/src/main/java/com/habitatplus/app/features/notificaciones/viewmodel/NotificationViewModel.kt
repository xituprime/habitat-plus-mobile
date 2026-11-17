
package com.habitatplus.app.features.notificaciones.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.habitatplus.app.features.notificaciones.data.NotificationRepository
import com.habitatplus.app.features.notificaciones.model.NotificationModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NotificationState(
    val isLoading: Boolean = true,
    val notifications: List<NotificationModel> = emptyList(),
    val error: String? = null
) {
    val unreadCount: Int
        get() = notifications.count { !it.isRead }
}

class NotificationViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()

    private val repository = NotificationRepository(application)

    private val _state = MutableStateFlow(NotificationState())

    val state: StateFlow<NotificationState> =
        _state.asStateFlow()

    private var notificationsJob: Job? = null
    private var observedUserId: String? = null

    private val authListener =
        FirebaseAuth.AuthStateListener { firebaseAuth ->
            val userId = firebaseAuth.currentUser?.uid

            if (userId != observedUserId) {
                loadNotifications()
            }
        }

    init {
        auth.addAuthStateListener(authListener)
        loadNotifications()
    }

    fun loadNotifications() {
        val userId = auth.currentUser?.uid

        notificationsJob?.cancel()
        notificationsJob = null
        observedUserId = userId

        if (userId.isNullOrBlank()) {
            _state.value = NotificationState(
                isLoading = false,
                error = "Debes iniciar sesión."
            )
            return
        }

        _state.value = NotificationState(isLoading = true)

        notificationsJob = viewModelScope.launch {
            try {
                repository.getNotifications(userId)
                    .collect { notifications ->

                        if (auth.currentUser?.uid == userId) {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    notifications = notifications,
                                    error = null
                                )
                            }
                        }
                    }
            } catch (exception: Exception) {
                if (auth.currentUser?.uid == userId) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "No se pudieron cargar las notificaciones."
                        )
                    }
                }
            }
        }
    }

    fun markAsRead(notificationId: String) {
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                if (auth.currentUser?.uid != userId) {
                    return@launch
                }

                repository.markAsRead(
                    userId = userId,
                    notificationId = notificationId
                )
            } catch (exception: Exception) {
                if (auth.currentUser?.uid == userId) {
                    _state.update {
                        it.copy(
                            error = "No se pudo marcar la notificación."
                        )
                    }
                }
            }
        }
    }

    fun markAllAsRead() {
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                if (auth.currentUser?.uid != userId) {
                    return@launch
                }

                repository.markAllAsRead(userId)
            } catch (exception: Exception) {
                if (auth.currentUser?.uid == userId) {
                    _state.update {
                        it.copy(
                            error = "No se pudieron actualizar las notificaciones."
                        )
                    }
                }
            }
        }
    }

    fun deleteNotification(notificationId: String) {
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                if (auth.currentUser?.uid != userId) {
                    return@launch
                }

                repository.deleteNotification(
                    userId = userId,
                    notificationId = notificationId
                )
            } catch (exception: Exception) {
                if (auth.currentUser?.uid == userId) {
                    _state.update {
                        it.copy(
                            error = "No se pudo eliminar la notificación."
                        )
                    }
                }
            }
        }
    }

    override fun onCleared() {
        auth.removeAuthStateListener(authListener)
        notificationsJob?.cancel()
        super.onCleared()
    }
}
