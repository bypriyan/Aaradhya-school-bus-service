package com.bypriyan.aaradhyaschoolbusservice.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bypriyan.aaradhyaschoolbusservice.api.Reservation
import com.bypriyan.aaradhyaschoolbusservice.apiResponce.ReservationBody
import com.bypriyan.aaradhyaschoolbusservice.apiResponce.ReservationResponse
import com.bypriyan.aaradhyaschoolbusservice.repo.ReservationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReservationViewModel @Inject constructor(private val repository: ReservationRepository) : ViewModel() {

    private val _responseMessage = MutableLiveData<String>()
    val responseMessage: LiveData<String> = _responseMessage

    fun createReservation(
        userId: String, pickupLocation: String, dropLocation: String,
        pickupLatitude: String, pickupLongitude: String, dropLatitude: String,
        dropLongitude: String, paid: String, totalAmount: String,
        installmentPaid: String, plan: String
    ) {
        viewModelScope.launch {
            try {
                val response = repository.createReservation(
                    userId, pickupLocation, dropLocation, pickupLatitude, pickupLongitude,
                    dropLatitude, dropLongitude, paid, totalAmount, installmentPaid, plan
                )
                _responseMessage.postValue(response.message ?: "Reservation created successfully")
            } catch (e: Exception) {
                _responseMessage.postValue(e.localizedMessage)
            }
        }
    }

}