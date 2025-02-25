package com.bypriyan.aaradhyaschoolbusservice.api

import com.bypriyan.aaradhyaschoolbusservice.apiResponce.ApiResponceUserDetails
import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiServiceUserDetails {
    @POST("getuser")
    suspend fun getUserDetails(
        @Header("Authorization") token: String,
        @Body request: UserRequest
    ): ApiResponceUserDetails
}

data class UserRequest(
    @SerializedName("user_id") val userId: String
)