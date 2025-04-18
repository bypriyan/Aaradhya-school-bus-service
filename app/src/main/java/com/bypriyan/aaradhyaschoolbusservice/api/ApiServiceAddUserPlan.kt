package com.bypriyan.aaradhyaschoolbusservice.api

import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT


interface ApiServiceAddUserPlan {
    @FormUrlEncoded
    @POST("payment.php")
    suspend fun createReservation(
        @Field("user_id") userId: String,
        @Field("pickup_location") pickupLocation: String,
        @Field("drop_location") dropLocation: String,
        @Field("pickup_latitude") pickupLatitude: String,
        @Field("pickup_longitude") pickupLongitude: String,
        @Field("drop_latitude") dropLatitude: String,
        @Field("drop_longitude") dropLongitude: String,
        @Field("paid") paid: String,
        @Field("total_amount") totalAmount: String,
        @Field("installment_paid") installmentPaid: String,
        @Field("plan") plan: String,
        @Field("payment_id") payment_id: String
    ): ApiResponse

}


data class ApiResponse(
    val status: String,
    val message: String? = null,
    val reservations: List<Reservation1>? = null
)

data class Reservation(
    val id: Int,
    val user_name: String,
    val standard: String,
    val classType: String,
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
    val pickup_route: String?,
    val drop_route: String?,
    val created_at: String
)
