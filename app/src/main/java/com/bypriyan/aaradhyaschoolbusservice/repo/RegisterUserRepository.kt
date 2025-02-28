package com.bypriyan.aaradhyaschoolbusservice.repo

import com.bypriyan.aaradhyaschoolbusservice.api.ApiServiceRegisterUser
import com.bypriyan.aaradhyaschoolbusservice.apiResponce.ApiResponceRegisterUser
import com.bypriyan.aaradhyaschoolbusservice.apiResponce.ApiResponceSendOTP
import com.bypriyan.aaradhyaschoolbusservice.model.RegisterRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class RegisterUserRepository @Inject constructor(private val apiService: ApiServiceRegisterUser) {



    suspend fun registerUser(fullName: RequestBody,
                             email: RequestBody,
                             className: RequestBody,
                             password: RequestBody,
                             age: RequestBody,
                             standard: RequestBody,
                             year: RequestBody,
                             fatherName: RequestBody,
                             fatherNumber: RequestBody,
                             motherName: RequestBody,
                             motherNumber: RequestBody,
                             imageUri : MultipartBody.Part?): ApiResponceRegisterUser

    {

        // Make the API call
        return apiService.registerUser(
            fullName,
            email,
            className,
            password,
            age,
            standard,
            year,
            fatherName,
            fatherNumber,
            motherName,
            motherNumber,
            imageUri
        )
    }
}