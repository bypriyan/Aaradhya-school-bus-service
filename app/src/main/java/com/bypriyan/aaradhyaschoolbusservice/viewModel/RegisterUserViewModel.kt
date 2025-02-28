package com.bypriyan.aaradhyaschoolbusservice.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bypriyan.aaradhyaschoolbusservice.apiResponce.ApiResponceRegisterUser
import com.bypriyan.aaradhyaschoolbusservice.model.RegisterRequest
import com.bypriyan.aaradhyaschoolbusservice.repo.RegisterUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUserRepository: RegisterUserRepository
) : ViewModel() {

    private val _registerResponse = MutableLiveData<ApiResponceRegisterUser>()
    val registerResponse: LiveData<ApiResponceRegisterUser> get() = _registerResponse

    fun registerUser(
        fullName: RequestBody,
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
        imageUri: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            try {
                val response = registerUserRepository.registerUser(
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
                Log.d("reg", "registerUser: $response")
                _registerResponse.value = response
            } catch (e: Exception) {
                Log.e("reg", "registerUser: ${e.message}")
            }
        }
    }
}