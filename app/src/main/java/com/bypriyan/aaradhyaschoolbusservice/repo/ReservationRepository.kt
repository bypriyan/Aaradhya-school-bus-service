package com.bypriyan.aaradhyaschoolbusservice.repo

import com.android.volley.Response
import com.bypriyan.aaradhyaschoolbusservice.api.ApiResponse
import com.bypriyan.aaradhyaschoolbusservice.api.ApiServiceAddUserPlan
import com.bypriyan.aaradhyaschoolbusservice.apiResponce.ReservationResponse
import javax.inject.Inject

class ReservationRepository @Inject constructor(private val apiService: ApiServiceAddUserPlan) {

    suspend fun createReservation(
        userId: String, pickupLocation: String, dropLocation: String,
        pickupLatitude: String, pickupLongitude: String, dropLatitude: String,
        dropLongitude: String, paid: String, totalAmount: String,
        installmentPaid: String, plan: String
    ): ApiResponse {
        return apiService.createReservation(
            userId, pickupLocation, dropLocation, pickupLatitude, pickupLongitude,
            dropLatitude, dropLongitude, paid, totalAmount, installmentPaid, plan
        )
    }
}