package com.bypriyan.aaradhyaschoolbusservice.api

import com.bypriyan.aaradhyaschoolbusservice.apiResponce.ApiLoginResponse
import com.bypriyan.aaradhyaschoolbusservice.model.LoginUser
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiServiceLogin {
    @FormUrlEncoded
    @POST("login.php")
    suspend fun loginUser(@Field("email") email: String, @Field("password") password: String): ApiLoginResponse
}