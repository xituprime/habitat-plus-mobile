package com.habitatplus.app.features.anuncios.data

import com.habitatplus.app.features.anuncios.model.Announcement

interface AnnouncementsRepository {
    fun getAnnouncements(): List<Announcement>

    fun getAnnouncementById(id: String): Announcement?
}

object FakeAnnouncementsRepository : AnnouncementsRepository {

    private val announcements = listOf(
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

    override fun getAnnouncements(): List<Announcement> {
        return announcements.toList()
    }

    override fun getAnnouncementById(id: String): Announcement? {
        return announcements.firstOrNull { it.id == id }
    }
}