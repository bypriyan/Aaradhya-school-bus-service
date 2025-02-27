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
        @Part("full_name") fullName: RequestBody,
        @Part("email") email: RequestBody,
        @Part("class") className: RequestBody,
        @Part("password") password: RequestBody,
        @Part("age") age: RequestBody,
        @Part("standard") standard: RequestBody,
        @Part("year") year: RequestBody,
        @Part("father_name") fatherName: RequestBody,
        @Part("father_number") fatherNumber: RequestBody,
        @Part("mother_name") motherName: RequestBody,
        @Part("mother_number") motherNumber: RequestBody,
        @Part image: MultipartBody.Part? // This is nullable
    ): ApiResponceRegisterUser
}