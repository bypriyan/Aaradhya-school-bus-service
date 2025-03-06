package com.bypriyan.aaradhyaschoolbusservice.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bypriyan.aaradhyaschoolbusservice.apiResponce.ApiResponceRegisterUser
import com.bypriyan.aaradhyaschoolbusservice.apiResponce.RegisterResponse
import com.bypriyan.aaradhyaschoolbusservice.model.RegisterRequest
import com.bypriyan.aaradhyaschoolbusservice.repo.RegisterUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.opencensus.resource.Resource
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUserRepository: RegisterUserRepository
) : ViewModel() {

        private val _registerResponse = MutableLiveData<RegisterResponse>()
        val registerResponse: LiveData<RegisterResponse> = _registerResponse

        fun registerUser(
            fullName: String, email: String, studentClass: String, age: String,
            standard: String, year: String, fatherName: String, fatherNumber: String,
            motherName: String, motherNumber: String, password: String, imageFile: File?
        ) {
            viewModelScope.launch {

                val requestBodyMap = mapOf(
                    "full_name" to fullName.toPlainRequestBody(),
                    "email" to email.toPlainRequestBody(),
                    "class" to studentClass.toPlainRequestBody(),
                    "age" to age.toPlainRequestBody(),
                    "standard" to standard.toPlainRequestBody(),
                    "year" to year.toPlainRequestBody(),
                    "father_name" to fatherName.toPlainRequestBody(),
                    "father_number" to fatherNumber.toPlainRequestBody(),
                    "mother_name" to motherName.toPlainRequestBody(),
                    "mother_number" to motherNumber.toPlainRequestBody(),
                    "password" to password.toPlainRequestBody()
                )

                val imagePart = imageFile?.let {
                    val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("image", it.name, requestFile)
                }

                try {
                    val response = registerUserRepository.registerUser(
                        requestBodyMap["full_name"]!!, requestBodyMap["email"]!!,
                        requestBodyMap["class"]!!, requestBodyMap["age"]!!,
                        requestBodyMap["standard"]!!, requestBodyMap["year"]!!,
                        requestBodyMap["father_name"]!!, requestBodyMap["father_number"]!!,
                        requestBodyMap["mother_name"]!!, requestBodyMap["mother_number"]!!,
                        requestBodyMap["password"]!!, imagePart
                    )

                    Log.d("mains", "registerUser: $response")

                    if (response.isSuccessful && response.body() != null) {
                        _registerResponse.postValue(response.body()!!)
                    } else {
                    }
                } catch (e: Exception) {
                }
            }
        }
    }

    // Extension function for string conversion
    fun String.toPlainRequestBody(): RequestBody = this.toRequestBody("text/plain".toMediaType())
