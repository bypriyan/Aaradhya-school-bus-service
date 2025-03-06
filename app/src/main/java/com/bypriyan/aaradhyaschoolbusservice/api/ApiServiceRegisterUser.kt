package com.bypriyan.aaradhyaschoolbusservice.api


import com.bypriyan.aaradhyaschoolbusservice.apiResponce.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiServiceRegisterUser {

    @Multipart
    @POST("regApi/reg.php")
    suspend fun registerUser(
        @Header("User-Agent") userAgent: String = "AndroidApp",
        @Part("full_name") fullName: RequestBody,
        @Part("email") email: RequestBody,
        @Part("class") studentClass: RequestBody,
        @Part("age") age: RequestBody,
        @Part("standard") standard: RequestBody,
        @Part("year") year: RequestBody,
        @Part("father_name") fatherName: RequestBody,
        @Part("father_number") fatherNumber: RequestBody,
        @Part("mother_name") motherName: RequestBody,
        @Part("mother_number") motherNumber: RequestBody,
        @Part("password") password: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<RegisterResponse>

}