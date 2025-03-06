package com.bypriyan.aaradhyaschoolbusservice.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bypriyan.aaradhyaschoolbusservice.api.UserResponse
import com.bypriyan.aaradhyaschoolbusservice.apiResponce.ApiResponceUserDetails
import com.bypriyan.aaradhyaschoolbusservice.apiResponce.UserDetails
import com.bypriyan.aaradhyaschoolbusservice.repo.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _user = MutableLiveData<UserResponse?>()
    val user: LiveData<UserResponse?> get() = _user

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun fetchUser(userId: String) {
        viewModelScope.launch {
            try {
                val response = userRepository.getUser(userId)
                _user.postValue(response)
                Log.d("ccss", "fetchUser: $response ")

            } catch (e: Exception) {
                _error.postValue("Exception: ${e.message}")
            }
        }
    }

}