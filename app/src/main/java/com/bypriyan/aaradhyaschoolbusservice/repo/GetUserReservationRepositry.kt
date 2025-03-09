package com.bypriyan.aaradhyaschoolbusservice.repo

import android.util.Log
import com.bypriyan.aaradhyaschoolbusservice.api.ApiServicesgetUserReservation
import com.bypriyan.aaradhyaschoolbusservice.api.ReservationResponse
import javax.inject.Inject
import kotlin.math.log

class GetUserReservationRepositry @Inject constructor(private val apiService: ApiServicesgetUserReservation) {

    suspend fun getReservations(userId: Int): ReservationResponse {
        return apiService.getReservations(userId)
    }
}