package com.bypriyan.aaradhyaschoolbusservice.apiResponce

data class ReservationResponse(
    val status: String,
    val message: String,
    val body: ReservationBody
)

data class ReservationBody(
    val user_id: Int,
    val pickup_location: String,
    val drop_location: String,
    val pickup_latitude: String,
    val pickup_longitude: String,
    val drop_latitude: String,
    val drop_longitude: String,
    val paid: String,
    val total_amount: String,
    val installment_paid: String,
    val plan: String,
    val updated_at: String,
    val created_at: String,
    val id: Int
)