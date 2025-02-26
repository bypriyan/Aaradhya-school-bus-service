package com.bypriyan.aaradhyaschoolbusservice.repo

import com.bypriyan.aaradhyaschoolbusservice.api.ApiServiceRegisterUser
import com.bypriyan.aaradhyaschoolbusservice.api.ApiServiceUserDetails
import com.bypriyan.aaradhyaschoolbusservice.api.UserRequest
import com.bypriyan.aaradhyaschoolbusservice.apiResponce.ApiResponceUserDetails
import javax.inject.Inject

class UserRepository @Inject constructor(private val apiService: ApiServiceUserDetails) {

    suspend fun getUserDetails(userId: String, token: String): ApiResponceUserDetails {
        return apiService.getUserDetails(
            token = "Bearer $token", // Add token type (e.g., Bearer)
            request = UserRequest(userId)
        )
    }
}