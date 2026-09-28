package com.habitatplus.app.navigation

import android.net.Uri

object ResidentRoutes {
    const val HOME = "resident_home"
    const val ANNOUNCEMENTS = "resident_announcements"
    const val ANNOUNCEMENT_DETAIL = "resident_announcement/{announcementId}"
    const val PROFILE = "resident_profile"
    const val SETTINGS = "resident_settings"

    fun announcement(id: String): String {
        return "resident_announcement/${Uri.encode(id)}"
    }
}