package com.bypriyan.aaradhyaschoolbusservice.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bypriyan.aaradhyaschoolbusservice.apiResponce.ApiResponceSendOTP
import com.bypriyan.aaradhyaschoolbusservice.repo.OTPRespositry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OTPViewModel @Inject constructor(private val repository: OTPRespositry) : ViewModel() {

    private val _otpResponse = MutableLiveData<Result<ApiResponceSendOTP>?>()
    val otpResponse: LiveData<Result<ApiResponceSendOTP>?> get() = _otpResponse

    fun sendOtp(email: String) {
        viewModelScope.launch {
            _otpResponse.postValue(repository.sendOtp(email))
        }
    }
}