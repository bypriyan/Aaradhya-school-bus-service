package com.bypriyan.aaradhyaschoolbusservice.api

import com.bypriyan.aaradhyaschoolbusservice.apiResponce.ApiResponceRegisterUser
import com.bypriyan.aaradhyaschoolbusservice.apiResponce.ApiResponceSendOTP
import com.bypriyan.aaradhyaschoolbusservice.model.LoginRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiServiceRegisterUser {
    @POST("register")
    suspend fun registerUser(@Body request: LoginRequest): Response<ApiResponceRegisterUser>
}