package com.bypriyan.aaradhyaschoolbusservice.repo

import com.bypriyan.aaradhyaschoolbusservice.api.ApiServiceOTP
import com.bypriyan.aaradhyaschoolbusservice.apiResponce.ApiResponceSendOTP
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OTPRespositry @Inject constructor(private val apiService: ApiServiceOTP) {

    suspend fun sendOtp(email: String): Result<ApiResponceSendOTP> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.sendOtp(mapOf("email" to email))
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