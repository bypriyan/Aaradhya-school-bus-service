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
        userClass: RequestBody,
        age: RequestBody,
        standard: RequestBody,
        year: RequestBody,
        fatherName: RequestBody,
        fatherNumber: RequestBody,
        motherName: RequestBody,
        motherNumber: RequestBody,
        password: RequestBody,
        image: MultipartBody.Part
    ) {
        viewModelScope.launch {
            try {
                val response = registerUserRepository.registerUser(
                    fullName, email, userClass, age, standard, year, fatherName, fatherNumber, motherName, motherNumber, password, image
                )
                if (response.status == "success") {
                    _registerResponse.value = response
                } else {
                    Log.e("RegisterViewModel", "Registration failed: ${response.message}")
                    _registerResponse.value = ApiResponceRegisterUser("error", response.message ?: "Unknown error", null)
                }
            } catch (e: Exception) {
                _registerResponse.value = ApiResponceRegisterUser("error", e.message ?: "Unknown error", null)
                Log.e("RegisterViewModel", "Registration failed: ${e.message}")
            }
        }
    }
}