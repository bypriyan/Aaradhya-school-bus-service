package com.bypriyan.aaradhyaschoolbusservice.api

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServicesgetUserReservation {
    @GET("getUserDetails.php")
    suspend fun getReservations(@Query("user_id") userId: Int): ReservationResponse
}

data class ReservationResponse(
    val status: String,
    val reservations: List<Reservation1>?
)

data class Reservation1(
    val id: Int,
    val user_name: String,
    val standard: String,
    val className: String,
    val status: String,
    val amount_paid: String,
    val total_amount: String,
    val plan: String,
    val installment_paid: String,
    val pickup_location: String,
    val pickup_latitude: String,
    val pickup_longitude: String,
    val drop_location: String,
    val drop_latitude: String,
    val drop_longitude: String,
    val pickup_route: String,
    val drop_route: String,
    val pickup_route_id: Int,
    val drop_route_id: Int,
    val created_at: String
)