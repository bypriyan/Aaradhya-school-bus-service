package com.bypriyan.aaradhyaschoolbusservice.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bypriyan.aaradhyaschoolbusservice.activity.PaymentDoneActivity
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityCheckOutBinding
import com.bypriyan.aaradhyaschoolbusservice.viewModel.PdfViewModel
import com.bypriyan.aaradhyaschoolbusservice.viewModel.UserViewModel
import com.bypriyan.bustrackingsystem.utility.Constants
import com.bypriyan.bustrackingsystem.utility.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class DashBoard1Activity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckOutBinding
    @Inject
    lateinit var preferenceManager: PreferenceManager
    private val pdfViewModel: PdfViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    lateinit var userId: String
    lateinit var token: String
    lateinit var token_type: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckOutBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val paidAmount = preferenceManager.getString("paid_amount") ?: "0"
        val installmentStatus = preferenceManager.getString("installment_status") ?: "0"
        binding.paidNextAmount.text = " Payment Status:"

       userId =  preferenceManager.getString(Constants.KEY_USER_ID)!!
       token= preferenceManager.getString(Constants.KEY_TOKEN)!!
        token_type = preferenceManager.getString(Constants.KEY_TOKEN_TYPE)!!
        Log.d("aaaa", "onCreate: $userId $token $token_type")

        userViewModel.getUserDetails(userId, token)

        binding.SlabStructureBtn.setOnClickListener {
            val intent = Intent(this, SlabActivity::class.java)
            intent.putExtra("hideButton", true)  // Send the extra to hide the button
            startActivity(intent)
        }

        binding.profileImage.setOnClickListener {
            userViewModel.userDetails.value?.let { userDetails ->
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
        }

        userViewModel.userDetails.observe(this) { userDetails ->
            // Update UI with user details
            Log.d("checks", "onCreate: $resources")
            binding.name.text = "Hi, ${userDetails.fullName}"
            loadImageWithGlide(Constants.KEY_IMAGE_PATH+userDetails.image)
            preferenceManager.putString(Constants.KEY_STANDARD, userDetails.standard)
            preferenceManager.putString(Constants.KEY_FULL_NAME, userDetails.fullName)
            // Save user details in SharedPreferences
            preferenceManager.putString(Constants.KEY_USER_ID, userDetails.id.toString())
            preferenceManager.putString(Constants.KEY_EMAIL, userDetails.email)
            preferenceManager.putString(Constants.KEY_USER_CLASS, userDetails.userClass)
            preferenceManager.putString(Constants.KEY_IMAGE, userDetails.image)
            preferenceManager.putString(Constants.KEY_YEAR, userDetails.year)
            preferenceManager.putString(Constants.KEY_FATHER_NAME, userDetails.fatherName)
            preferenceManager.putString(Constants.KEY_FATHER_NUMBER, userDetails.fatherNumber)
            preferenceManager.putString(Constants.KEY_MOTHER_NAME, userDetails.motherName)
            preferenceManager.putString(Constants.KEY_MOTHER_NUMBER, userDetails.motherNumber)
            preferenceManager.putString(Constants.KEY_EMAIL_VERIFIED_AT, userDetails.emailVerifiedAt)
            preferenceManager.putString(Constants.KEY_CREATED_AT, userDetails.createdAt)
            preferenceManager.putString(Constants.KEY_UPDATED_AT, userDetails.updatedAt)
            preferenceManager.putString(Constants.KEY_AGE, userDetails.age.toString())
            preferenceManager.putString(Constants.KEY_IS_APPROVED, userDetails.isApproved.toString())
            preferenceManager.putString(Constants.KEY_USER_TYPE, userDetails.userType)
            preferenceManager.putString(Constants.KEY_OTP, userDetails.otp)
            preferenceManager.putString(Constants.KEY_OTP_VERIFIED, userDetails.otpVerified.toString())
        }

        binding.CheckOutAct.setOnClickListener {
            startActivity(Intent(this, CheckOut1::class.java))
        }

        binding.DownloadRecieptBtn.setOnClickListener {
            // Implement receipt download logic
            logReceiptDetails(preferenceManager)
        }

        binding.paidNextAmount.setOnClickListener {
            startActivity(Intent(this, PaymentOptionActivity::class.java))
        }
        observePdfGeneration()
    }

    fun logReceiptDetails(preferenceManager: PreferenceManager) {
        val receiptNo = preferenceManager.getString(Constants.KEY_RECEIPT_NO)
        val date = preferenceManager.getString(Constants.KEY_DATE)
        val studentName = preferenceManager.getString(Constants.KEY_FULL_NAME)
        val address = "Pimpri Chinchwad, Oppo. to Alankapuram Society, Alandi Rd, Wadmukhwadi, Pune"
        val mobileNo = "+91 9766987118"
        val amount = preferenceManager.getString(Constants.KEY_AMOUNT)
        val std = preferenceManager.getString(Constants.KEY_STANDARD)
        val totalFees = preferenceManager.getString(Constants.KEY_TOTAL_FEES)
        val monthFrom = preferenceManager.getString(Constants.KEY_MONTH_FROM)
        val monthTo = preferenceManager.getString(Constants.KEY_MONTH_TO)

        Log.d("ReceiptDetails", """
        receiptNo: $receiptNo
        date: $date
        studentName: $studentName
        address: $address
        mobileNo: $mobileNo
        amount: $amount
        std: $std
        totalFees: $totalFees
        monthFrom: $monthFrom
        monthTo: $monthTo
    """.trimIndent())

        checkPermissionsAndGeneratePdf(
            receiptNo = receiptNo ?: "N/A",
            date = date ?: "N/A",
            studentName = studentName ?: "N/A",
            address = address,
            mobileNo = mobileNo,
            amount = amount ?: "N/A",
            std = std ?: "N/A",
            totalFees = totalFees ?: "N/A",
            monthFrom = monthFrom ?: "N/A",
            monthTo = monthTo ?: "N/A"
        )
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


        Log.d("TAGS", "checkPermissionsAndGeneratePdf: $receiptNo, $date, $studentName, $address, $mobileNo, $amount, $std, $totalFees, $monthFrom, $monthTo")
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
            receiptNo = receiptNo,
            date = date,
            studentName = studentName,
            address = address,
            mobileNo = mobileNo,
            amount = amount,
            std = std,
            totalFees = totalFees,
            monthFrom= monthFrom,
            monthTo = monthTo
        )
    }

    private fun observePdfGeneration() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                pdfViewModel.pdfState.collect { result ->
                    result?.onSuccess { filePath ->
                        Toast.makeText(this@DashBoard1Activity, "PDF Saved at: $filePath", Toast.LENGTH_LONG).show()
                    }?.onFailure { error ->
                        Toast.makeText(this@DashBoard1Activity, "Error: ${error.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_STORAGE_PERMISSION = 1001
    }

    private fun loadImageWithGlide(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl) // Load the image URL
            .into(binding.profileImage) // Set the image to the ImageView
    }
}
