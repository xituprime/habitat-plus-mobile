package com.habitatplus.app.navigation

object Routes{

    const val HOME = "home"

    const val RESERVATION = "reservation/{parkingId}"

    fun reservation (parkingId: String): String{
        return "reservation/$parkingId"
    }

}