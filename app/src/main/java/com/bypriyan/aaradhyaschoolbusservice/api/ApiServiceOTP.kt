package com.bypriyan.aaradhyaschoolbusservice.api

import com.bypriyan.aaradhyaschoolbusservice.apiResponce.ApiResponceSendOTP
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiServiceOTP {
    @POST("api/v1/sendOTP")
    suspend fun sendOtp(@Body request: Map<String, String>): Response<ApiResponceSendOTP>
}