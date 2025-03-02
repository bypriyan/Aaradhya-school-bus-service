package com.bypriyan.aaradhyaschoolbusservice.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bypriyan.aaradhyaschoolbusservice.repo.PdfRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PdfViewModel @Inject constructor(
    private val pdfRepository: PdfRepository
) : ViewModel() {

    private val _pdfState = MutableStateFlow<Result<String>?>(null)
    val pdfState: StateFlow<Result<String>?> = _pdfState

    fun generatePdf(
        receiptNo: String,
        date: String,
        studentName: String,
        address: String,
        mobileNo: String,
        amount: String,
        std: String,
        totalFees: String,
        monthFrom: String,
        monthTo: String
    ) {
        viewModelScope.launch {
            try {
                val filePath = pdfRepository.createPdf(
                    receiptNo, date, studentName, address, mobileNo, amount, std,totalFees,monthFrom,monthTo
                )
                _pdfState.value = Result.success(filePath)
            } catch (e: Exception) {
                _pdfState.value = Result.failure(e)
            }
        }
    }
}
