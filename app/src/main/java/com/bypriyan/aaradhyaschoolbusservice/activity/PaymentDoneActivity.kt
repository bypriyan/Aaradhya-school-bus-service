package com.bypriyan.aaradhyaschoolbusservice.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bypriyan.aaradhyaschoolbusservice.R
import com.bypriyan.aaradhyaschoolbusservice.activity.LoginActivity
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityDasboardBinding
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityPaymentDoneBinding
import com.bypriyan.aaradhyaschoolbusservice.viewModel.PdfViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class PaymentDoneActivity : AppCompatActivity() {

    lateinit var binding: ActivityPaymentDoneBinding
    private val pdfViewModel: PdfViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityPaymentDoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtTransactionNumber.text = intent.getStringExtra("id")!!

        binding.btnDone.setOnClickListener(){
            startActivity(Intent(this, DasboardActivity::class.java))
            finish()
        }
        binding.btnDownload.setOnClickListener{
            checkPermissionsAndGeneratePdf()
        }
        observePdfGeneration()
    }

    private fun checkPermissionsAndGeneratePdf(
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            generatePdf(
                receiptNo,
                date,
                studentName,
                address,
                mobileNo,
                amount,
                std,
                totalFees,
                monthFrom,
                monthTo
            )
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                generatePdf(receiptNo, date, studentName,
                    address, mobileNo, amount, std,
                    totalFees, monthFrom, monthTo)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_CODE_STORAGE_PERMISSION
                )
            }
        }
    }

    private fun generatePdf(receiptNo: String,
                            date: String,
                            studentName: String,
                            address: String,
                            mobileNo: String,
                            amount: String,
                            std: String,
                            totalFees: String,
                            monthFrom: String,
                            monthTo: String) {
        pdfViewModel.generatePdf(
            receiptNo = "123456",
            date = "2023-09-15",
            studentName = "Priyanshu",
            address = "132 housere no",
            mobileNo = "123456789",
            amount = "5000",
            std = "lili",
            totalFees = "15000",
            monthFrom= "aug ",
            monthTo = "sept"
        )
    }

    private fun observePdfGeneration() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                pdfViewModel.pdfState.collect { result ->
                    result?.onSuccess { filePath ->
                        Toast.makeText(this@PaymentDoneActivity, "PDF Saved at: $filePath", Toast.LENGTH_LONG).show()
                    }?.onFailure { error ->
                        Toast.makeText(this@PaymentDoneActivity, "Error: ${error.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_STORAGE_PERMISSION = 1001
    }
}