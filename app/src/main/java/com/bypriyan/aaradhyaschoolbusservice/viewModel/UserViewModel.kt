package com.bypriyan.aaradhyaschoolbusservice.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val _userDetails = MutableLiveData<UserDetails>()
    val userDetails: LiveData<UserDetails> get() = _userDetails

    fun getUserDetails(userId: String, token: String) {
        viewModelScope.launch {
            try {
                val response = userRepository.getUserDetails(userId, token)
                if(response.status == "success" && response.body != null){
                    _userDetails.value = response.body
                }
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }
}