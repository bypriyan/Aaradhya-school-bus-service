package com.bypriyan.aaradhyaschoolbusservice.api

import com.bypriyan.aaradhyaschoolbusservice.apiResponce.ApiResponceUserDetails
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiServiceUserDetails {
    @GET("getUser.php")
    suspend fun getUser(@Query("user_id") userId: String): Response<UserResponse>
}


data class UserResponse(
    val status: String,
    val data: User?
)

data class User(
    val id: Int,
    val full_name: String,
    val email: String,
    val `class`: String,
    val age: Int,
    val standard: String,
    val year: String,
    val image_url: String,
    val created_at: String,
    val guardians: List<Guardian>
)

data class Guardian(
    val guardian_type: String,
    val name: String,
    val phone_number: String
)
