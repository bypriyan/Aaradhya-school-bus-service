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
class RegisterUserViewModel @Inject constructor(private val repository: RegisterUserRepository) : ViewModel() {

    private val _responseMessage = MutableLiveData<ApiResponceRegisterUser>()
    val responseMessage: LiveData<ApiResponceRegisterUser> = _responseMessage

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
        guardianName: RequestBody,
        guardianNumber: RequestBody,
        password: RequestBody,
        image: MultipartBody.Part
    ) {
        viewModelScope.launch {
            try {
                val response = repository.registerUser(
                    fullName, email, userClass, age, standard, year,
                    fatherName, fatherNumber, motherName, motherNumber,
                    guardianName, guardianNumber, password, image
                )
                Log.d("ara", "registerUser: $response")
                _responseMessage.postValue(response)
            } catch (e: Exception) {
                _responseMessage.postValue(ApiResponceRegisterUser("error", e.message ?: "Unknown error",null))
            }
        }
    }
}