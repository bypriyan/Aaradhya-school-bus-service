package com.bypriyan.aaradhyaschoolbusservice.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bypriyan.aaradhyaschoolbusservice.apiResponce.ApiResponceRegisterUser
import com.bypriyan.aaradhyaschoolbusservice.model.RegisterRequest
import com.bypriyan.aaradhyaschoolbusservice.repo.RegisterUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class RegisterUserViewModel @Inject constructor(
    private val registerRepository: RegisterUserRepository
) : ViewModel() {

    private val _registerResponse = MutableLiveData<Result<ApiResponceRegisterUser>>()
    val registerResponse: LiveData<Result<ApiResponceRegisterUser>> get() = _registerResponse

    fun registerUser(request: RegisterRequest) {
        viewModelScope.launch {
            try {
                val response = registerRepository.registerUser(request)
                _registerResponse.value = Result.success(response)
            } catch (e: Exception) {
                _registerResponse.value = Result.Failure(e)
            }
        }
    }
}