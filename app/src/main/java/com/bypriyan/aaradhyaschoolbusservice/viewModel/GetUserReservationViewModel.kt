package com.bypriyan.aaradhyaschoolbusservice.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bypriyan.aaradhyaschoolbusservice.api.ReservationResponse
import com.bypriyan.aaradhyaschoolbusservice.repo.GetUserReservationRepositry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetUserReservationViewModel @Inject constructor(private val repository: GetUserReservationRepositry) : ViewModel() {

    private val _reservations = MutableLiveData<Result<ReservationResponse>>()
    val reservations: LiveData<Result<ReservationResponse>> get() = _reservations

    fun fetchReservations(userId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getReservations(userId)
                _reservations.postValue(Result.success(response))
            } catch (e: Exception) {
                _reservations.postValue(Result.failure(e))
            }
        }
    }
}