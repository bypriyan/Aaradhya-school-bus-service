package com.bypriyan.aaradhyaschoolbusservice.repo

import com.android.volley.Response
import com.bypriyan.aaradhyaschoolbusservice.api.ApiServiceAddUserPlan
import com.bypriyan.aaradhyaschoolbusservice.api.ReservationRequest
import com.bypriyan.aaradhyaschoolbusservice.apiResponce.ReservationResponse
import javax.inject.Inject

class ReservationRepository @Inject constructor(private val apiService: ApiServiceAddUserPlan) {

    suspend fun storeReservation(token: String, reservation: Map<String, String>): Response<ReservationResponse> {
        return apiService.storeReservation(
            token = "Bearer $token",
            reservation
        )
    }
}