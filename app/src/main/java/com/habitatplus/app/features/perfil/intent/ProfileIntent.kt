
package com.habitatplus.app.features.perfil.intent

sealed interface ProfileIntent {

    // Cargar los datos del perfil.
    data object Load : ProfileIntent

    // Mostrar y cerrar detalles de residencia.
    data object ShowResidence : ProfileIntent

    data object DismissResidence : ProfileIntent

    // Iniciar la edicion del perfil.
    data object StartEditing : ProfileIntent

    // Editar nombre.
    data class ChangeName(
        val value: String
    ) : ProfileIntent

    // Editar telefono.
    data class ChangePhone(
        val value: String
    ) : ProfileIntent

    // Editar condominio.
    data class ChangeCondominium(
        val value: String
    ) : ProfileIntent

    // Editar torre.
    data class ChangeTower(
        val value: String
    ) : ProfileIntent

    // Editar apartamento.
    data class ChangeApartment(
        val value: String
    ) : ProfileIntent

    // Guardar o cancelar la edicion.
    data object SaveChanges : ProfileIntent

    data object CancelEditing : ProfileIntent

    // Cerrar mensaje de confirmacion.
    data object DismissSuccess : ProfileIntent

    // Navegacion a Configuracion.
    data object OpenSettings : ProfileIntent

    data object SettingsNavigationHandled : ProfileIntent
}
