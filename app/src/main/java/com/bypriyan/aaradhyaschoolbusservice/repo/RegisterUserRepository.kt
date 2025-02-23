package com.bypriyan.aaradhyaschoolbusservice.repo

import com.bypriyan.aaradhyaschoolbusservice.api.ApiServiceRegisterUser
import com.bypriyan.aaradhyaschoolbusservice.apiResponce.ApiResponceRegisterUser
import com.bypriyan.aaradhyaschoolbusservice.apiResponce.ApiResponceSendOTP
import com.bypriyan.aaradhyaschoolbusservice.model.LoginRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RegisterUserRepository @Inject constructor(private val apiService: ApiServiceRegisterUser) {

    suspend fun registerUser(request: LoginRequest): Result<ApiResponceRegisterUser> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.registerUser(request)
                if (response.isSuccessful && response.body() != null && response.body()?.status == "success") {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}