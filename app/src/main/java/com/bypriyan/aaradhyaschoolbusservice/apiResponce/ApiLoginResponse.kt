package com.bypriyan.aaradhyaschoolbusservice.apiResponce

data class ApiLoginResponse (
    val status: String,
    val message: String,
    val id: Int?,
    val reservations: List<ReservationLogin>?
)

data class ReservationLogin(
    val reservationId: Int,
    val createdAt: String,
    val amountPaid: String,
    val plan: String,
    val installmentPaid: String,
    val totalAmount: String,
    val pickupLocation: String,
    val pickupLatitude: String,
    val pickupLongitude: String,
    val dropLocation: String,
    val dropLatitude: String,
    val dropLongitude: String
)