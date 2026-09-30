package com.habitatplus.app.features.anuncios.viewmodel

import androidx.lifecycle.ViewModel
import com.habitatplus.app.features.anuncios.intent.AnnouncementsIntent
import com.habitatplus.app.features.anuncios.model.Announcement
import com.habitatplus.app.features.anuncios.model.AnnouncementFilter
import com.habitatplus.app.features.anuncios.state.AnnouncementsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.Normalizer
import java.util.Locale

class AnnouncementsViewModel : ViewModel() {

    private val _state = MutableStateFlow(AnnouncementsState())

    val state = _state.asStateFlow()

    init {
        onIntent(AnnouncementsIntent.Load)
    }

    fun onIntent(intent: AnnouncementsIntent) {
        when (intent) {
            AnnouncementsIntent.Load,
            AnnouncementsIntent.Refresh -> loadDemo()

            is AnnouncementsIntent.Search -> {
                _state.value = _state.value.copy(
                    query = intent.query
                )
                filterAnnouncements()
            }

            is AnnouncementsIntent.SelectFilter -> {
                _state.value = _state.value.copy(
                    filter = intent.filter
                )
                filterAnnouncements()
            }
        }
    }

    private fun loadDemo() {
        val announcements = listOf(
            Announcement(
                id = "agua",
                title = "Corte programado de agua por mantenimiento " +
                        "general de bombas",
                category = "Mantenimiento",
                date = "14 de octubre de 2026",
                publishedTime = "08:30 AM",
                serviceLabel = "Servicios Básicos",
                suspensionSchedule = "14:00 hrs – 18:00 hrs",
                suspensionDuration = "4 horas",
                summary = "Interrupción preventiva del suministro hídrico " +
                        "programada de 14:00 a 18:00 horas.",
                body = """
                    Estimados residentes:

                    Les informamos que se llevará a cabo el mantenimiento
                    semestral de las bombas de presión hidroneumáticas y
                    limpieza de cisternas centrales.

                    El suministro de agua potable será suspendido
                    temporalmente desde las 14:00 hasta las 18:00 horas.

                    Agradecemos tomar las previsiones necesarias
                    recolectando agua previamente.

                    Durante los trabajos, mantenga cerradas las llaves
                    de agua. La administración informará cuando el
                    servicio haya sido restablecido.

                    Gracias por su comprensión.
                """.trimIndent(),
                priority = true
            ),

            Announcement(
                id = "fumigacion",
                title = "Fumigación de áreas verdes comunes",
                category = "Mantenimiento",
                date = "10 de octubre de 2026",
                publishedTime = "09:00 AM",
                summary = "Jornada preventiva en jardines perimetrales, " +
                        "zona de juegos infantiles y pasillos.",
                body = """
                    Se realizará una jornada preventiva de fumigación
                    en jardines, zona de juegos y pasillos comunes.

                    Durante los trabajos, respete la señalización
                    y evite ingresar con niños o mascotas a las
                    áreas intervenidas.

                    La administración informará cuando las áreas
                    puedan utilizarse nuevamente.
                """.trimIndent()
            ),

            Announcement(
                id = "asamblea",
                title = "Asamblea ordinaria de propietarios",
                category = "Institucional",
                date = "05 de octubre de 2026",
                publishedTime = "10:15 AM",
                summary = "Convocatoria en el Salón Multiuso " +
                        "a las 19:30 horas.",
                body = """
                    Se convoca a los propietarios a la asamblea
                    ordinaria en el Salón Multiuso.

                    Se presentará el informe de administración
                    y se revisarán las propuestas de mantenimiento
                    de las áreas comunes.

                    Fecha: 20 de octubre.
                    Hora: 19:30.

                    Agradecemos su puntualidad.
                """.trimIndent()
            ),

            Announcement(
                id = "mascotas",
                title = "Reglamento de convivencia para mascotas",
                category = "Convivencia",
                date = "28 de septiembre de 2026",
                publishedTime = "11:00 AM",
                summary = "Recordatorio sobre correa, limpieza y uso " +
                        "responsable de las áreas comunes.",
                body = """
                    Recuerde utilizar correa al transitar con
                    su mascota por las áreas comunes.

                    Recoja los desechos y deposítelos en los
                    recipientes correspondientes.

                    Mantenga actualizada la información de su
                    mascota con la administración y respete
                    los espacios señalizados.
                """.trimIndent()
            )
        )

        _state.value = _state.value.copy(
            isLoading = false,
            error = null,
            announcements = announcements
        )

        filterAnnouncements()
    }

    private fun filterAnnouncements() {
        val current = _state.value
        val query = normalize(current.query.trim())

        val visible = current.announcements.filter { announcement ->

            val matchesCategory = when (current.filter) {
                AnnouncementFilter.ALL -> true

                AnnouncementFilter.PRIORITY ->
                    announcement.priority

                AnnouncementFilter.MAINTENANCE ->
                    announcement.category == "Mantenimiento"

                AnnouncementFilter.INSTITUTIONAL ->
                    announcement.category == "Institucional"

                AnnouncementFilter.COMMUNITY ->
                    announcement.category == "Convivencia"
            }

            val searchable = normalize(
                "${announcement.title} " +
                        "${announcement.summary} " +
                        announcement.category
            )

            matchesCategory &&
                    (query.isBlank() || query in searchable)
        }

        _state.value = current.copy(
            visibleAnnouncements = visible
        )
    }

    private fun normalize(value: String): String {
        return Normalizer.normalize(
            value,
            Normalizer.Form.NFD
        )
            .replace("\\p{M}+".toRegex(), "")
            .lowercase(Locale.ROOT)
    }
}