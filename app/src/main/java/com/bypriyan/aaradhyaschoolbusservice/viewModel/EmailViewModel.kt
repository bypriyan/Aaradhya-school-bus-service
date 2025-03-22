package com.bypriyan.aaradhyaschoolbusservice.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bypriyan.aaradhyaschoolbusservice.repo.EmailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmailViewModel @Inject constructor(private val repository: EmailRepository) : ViewModel() {

    private val _otpLiveData = MutableLiveData<Result<String>>()
    val otpLiveData: LiveData<Result<String>> get() = _otpLiveData

    fun sendOtp(email: String) {
        viewModelScope.launch {
            try {
                val otp = repository.sendOtp(email)
                if (otp.isNotEmpty()) {
                    _otpLiveData.postValue(Result.success(otp))
                } else {
                    _otpLiveData.postValue(Result.failure(Exception("Failed to send OTP")))
                }
            } catch (e: Exception) {
                _otpLiveData.postValue(Result.failure(e))
            }
        }
    }
}
