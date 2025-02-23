package com.bypriyan.aaradhyaschoolbusservice.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bypriyan.aaradhyaschoolbusservice.apiResponce.ApiResponceRegisterUser
import com.bypriyan.aaradhyaschoolbusservice.model.LoginRequest
import com.bypriyan.aaradhyaschoolbusservice.repo.RegisterUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class RegisterUserViewModel @Inject constructor(private val repository: RegisterUserRepository) : ViewModel() {

    private val _registerResponse = MutableLiveData<Result<ApiResponceRegisterUser>>()
    val registerResponse: LiveData<Result<ApiResponceRegisterUser>> get() = _registerResponse

    fun registerUser(request: LoginRequest) {
        viewModelScope.launch {
            try {
                val response = repository.registerUser(request)
                _registerResponse.postValue(response)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}