package com.bypriyan.aaradhyaschoolbusservice.repo

import com.bypriyan.aaradhyaschoolbusservice.api.ApiServiceRegisterUser
import com.bypriyan.aaradhyaschoolbusservice.apiResponce.ApiResponceRegisterUser
import com.bypriyan.aaradhyaschoolbusservice.apiResponce.ApiResponceSendOTP
import com.bypriyan.aaradhyaschoolbusservice.model.RegisterRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class RegisterUserRepository @Inject constructor(private val apiService: ApiServiceRegisterUser) {

    suspend fun registerUser(request: RegisterRequest): ApiResponceRegisterUser {
        val imagePart = request.imageUri?.let { path ->
            val file = File(path)
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("image", file.name, requestFile)
        }

        // Convert other fields to RequestBody
        val fullName = request.fullName.toRequestBody("text/plain".toMediaTypeOrNull())
        val email = request.email.toRequestBody("text/plain".toMediaTypeOrNull())
        val className = request.className.toRequestBody("text/plain".toMediaTypeOrNull())
        val password = request.password.toRequestBody("text/plain".toMediaTypeOrNull())
        val age = request.age.toRequestBody("text/plain".toMediaTypeOrNull())
        val standard = request.standard.toRequestBody("text/plain".toMediaTypeOrNull())
        val year = request.year.toRequestBody("text/plain".toMediaTypeOrNull())
        val fatherName = request.fatherName.toRequestBody("text/plain".toMediaTypeOrNull())
        val fatherNumber = request.fatherNumber.toRequestBody("text/plain".toMediaTypeOrNull())
        val motherName = request.motherName.toRequestBody("text/plain".toMediaTypeOrNull())
        val motherNumber = request.motherNumber.toRequestBody("text/plain".toMediaTypeOrNull())

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
            imagePart
        )
    }
}