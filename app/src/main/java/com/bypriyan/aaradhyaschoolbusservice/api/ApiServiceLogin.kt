package com.bypriyan.aaradhyaschoolbusservice.api

import com.bypriyan.aaradhyaschoolbusservice.apiResponce.ApiLoginResponse
import com.bypriyan.aaradhyaschoolbusservice.model.LoginUser
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiServiceLogin {
    @POST("api/v1/login")
    suspend fun loginUser(@Body request: LoginUser): ApiLoginResponse
}