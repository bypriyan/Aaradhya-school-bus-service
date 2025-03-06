package com.bypriyan.aaradhyaschoolbusservice.api

import retrofit2.http.Body
import retrofit2.http.POST

interface ApiServiceToken {
    @POST("token.php")
    suspend fun insertOrUpdateToken(@Body request: TokenRequest): ApiResponseToken
}

data class ApiResponseToken(
    val status: String,
    val message: String,
    val fcmToken: String? = null
)

data class TokenRequest(
    val userId: String,
    val fcmToken: String
)