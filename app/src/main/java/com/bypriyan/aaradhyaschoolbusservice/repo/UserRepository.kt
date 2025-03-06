package com.bypriyan.aaradhyaschoolbusservice.repo

import android.util.Log
import com.bypriyan.aaradhyaschoolbusservice.api.ApiServiceRegisterUser
import com.bypriyan.aaradhyaschoolbusservice.api.ApiServiceUserDetails
import com.bypriyan.aaradhyaschoolbusservice.api.UserResponse
import com.bypriyan.aaradhyaschoolbusservice.apiResponce.ApiResponceUserDetails
import io.opencensus.resource.Resource
import javax.inject.Inject

class UserRepository @Inject constructor(private val apiService: ApiServiceUserDetails) {

    suspend fun getUser(userId: String): UserResponse {

        val response = apiService.getUser(userId)
        if (response.isSuccessful && response.body() != null) {
            return response.body()!!
        } else {
            return response.body()!!
        }

    }

    }
