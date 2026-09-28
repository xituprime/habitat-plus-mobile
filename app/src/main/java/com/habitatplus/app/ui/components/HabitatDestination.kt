package com.habitatplus.app.ui.components

import androidx.annotation.DrawableRes
import com.habitatplus.app.R

enum class HabitatDestination(
    val label: String,
    @DrawableRes val iconRes: Int
) {
    HOME(
        label = "Inicio",
        iconRes = R.drawable.ic_home
    ),

    PARKING(
        label = "Parqueos",
        iconRes = R.drawable.ic_parking
    ),

    REPORTS(
        label = "Reportes",
        iconRes = R.drawable.ic_reports
    ),

    AREAS(
        label = "Áreas",
        iconRes = R.drawable.ic_areas
    ),

    PROFILE(
        label = "Perfil",
        iconRes = R.drawable.ic_profile
    )
}