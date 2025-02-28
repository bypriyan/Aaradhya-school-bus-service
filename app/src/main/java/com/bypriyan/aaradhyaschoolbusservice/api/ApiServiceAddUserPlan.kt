package com.bypriyan.aaradhyaschoolbusservice.api

import com.bypriyan.aaradhyaschoolbusservice.apiResponce.ReservationResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiServiceAddUserPlan {
    @POST("storeReservation")
    suspend fun storeReservation(
        @Header("Authorization") token: String,
        @Body reservation: Map<String, String>
    ): ReservationResponse
}

data class ReservationRequest(
    val user_id: String,
    val pickup_location: String,
    val drop_location: String,
    val pickup_latitude: String,
    val pickup_longitude: String,
    val drop_latitude: String,
    val drop_longitude: String,
    val paid: String,
    val total_amount: String,
    val installment_paid: String,
    val plan: String
)