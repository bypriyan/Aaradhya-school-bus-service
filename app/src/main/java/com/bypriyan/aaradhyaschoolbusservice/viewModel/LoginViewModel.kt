package com.bypriyan.aaradhyaschoolbusservice.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bypriyan.aaradhyaschoolbusservice.apiResponce.ApiLoginResponse
import com.bypriyan.aaradhyaschoolbusservice.model.LoginUser
import com.bypriyan.aaradhyaschoolbusservice.repo.LoginUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUserRepository: LoginUserRepository
) : ViewModel() {

    private val _loginResponse = MutableLiveData<ApiLoginResponse?>()
    val loginResponse: LiveData<ApiLoginResponse?> get() = _loginResponse

    fun loginUser(email:String,password:String) {
        viewModelScope.launch {
            try {
                val response = loginUserRepository.loginUser(email,password)
                Log.d("login", "loginUser: $response")
                _loginResponse.value = response
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
                _loginResponse.value = null // Optionally, you can set an error state
            }
        }
    }
}