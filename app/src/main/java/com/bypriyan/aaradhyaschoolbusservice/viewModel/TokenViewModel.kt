package com.bypriyan.aaradhyaschoolbusservice.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bypriyan.aaradhyaschoolbusservice.api.ApiResponse
import com.bypriyan.aaradhyaschoolbusservice.api.ApiResponseToken
import com.bypriyan.aaradhyaschoolbusservice.repo.TokenRespoitry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TokenViewModel @Inject constructor(private val repository: TokenRespoitry) : ViewModel() {

    private val _tokenResponse = MutableLiveData<ApiResponseToken>()
    val tokenResponse: LiveData<ApiResponseToken> get() = _tokenResponse

    fun insertOrUpdateToken(userId: String, fcmToken: String) {
        viewModelScope.launch {
            val response = repository.insertOrUpdateToken(userId, fcmToken)
            if (response.status=="success") {
                _tokenResponse.value = response
            } else {
                // Handle error
            }
        }
    }
}