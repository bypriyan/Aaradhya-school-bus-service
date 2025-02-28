package com.bypriyan.aaradhyaschoolbusservice.api


import com.bypriyan.aaradhyaschoolbusservice.apiResponce.ApiResponceRegisterUser
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiServiceRegisterUser {
    @Multipart
    @POST("register")
    suspend fun registerUser(
        @Part("fullName") fullName: RequestBody,
        @Part("email") email: RequestBody,
        @Part("className") className: RequestBody,
        @Part("password") password: RequestBody,
        @Part("age") age: RequestBody,
        @Part("standard") standard: RequestBody,
        @Part("year") year: RequestBody,
        @Part("fatherName") fatherName: RequestBody,
        @Part("fatherNumber") fatherNumber: RequestBody,
        @Part("motherName") motherName: RequestBody,
        @Part("motherNumber") motherNumber: RequestBody,
        @Part imageUri: MultipartBody.Part?
    ): ApiResponceRegisterUser
}