package com.bypriyan.aaradhyaschoolbusservice.repo

import com.bypriyan.aaradhyaschoolbusservice.api.ApiResponseToken
import com.bypriyan.aaradhyaschoolbusservice.api.ApiServiceToken
import com.bypriyan.aaradhyaschoolbusservice.api.TokenRequest
import javax.inject.Inject

class TokenRespoitry @Inject constructor(private val apiService: ApiServiceToken) {

    suspend fun insertOrUpdateToken(userId: String, fcmToken: String): ApiResponseToken {
        return apiService.insertOrUpdateToken(TokenRequest(userId, fcmToken))
    }
}