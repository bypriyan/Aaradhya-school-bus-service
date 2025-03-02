package com.bypriyan.aaradhyaschoolbusservice.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bypriyan.aaradhyaschoolbusservice.api.ReservationRequest
import com.bypriyan.aaradhyaschoolbusservice.apiResponce.ReservationBody
import com.bypriyan.aaradhyaschoolbusservice.apiResponce.ReservationResponse
import com.bypriyan.aaradhyaschoolbusservice.repo.ReservationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReservationViewModel @Inject constructor(private val repository: ReservationRepository) : ViewModel() {

    private val _reservationResponse = MutableLiveData<ReservationResponse>()
    val reservationResponse: LiveData<ReservationResponse> get() = _reservationResponse

    fun storeReservation(token: String, reservation: Map<String, String>)
    {
        viewModelScope.launch {
            try {
                val response = repository.storeReservation(token, reservation)
                Log.d("respo", "storeReservation: $response")
                _reservationResponse.value = response
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}