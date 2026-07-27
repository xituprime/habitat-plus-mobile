package com.habitatplus.app.features.parqueos.intent

sealed interface ParkingIntent{

    data object LoadParkings : ParkingIntent

}