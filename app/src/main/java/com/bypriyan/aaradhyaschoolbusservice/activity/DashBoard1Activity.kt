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
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bypriyan.aaradhyaschoolbusservice.activity.PaymentDoneActivity
import com.bypriyan.aaradhyaschoolbusservice.api.ReservationResponse
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityCheckOutBinding
import com.bypriyan.aaradhyaschoolbusservice.viewModel.GetUserReservationViewModel
import com.bypriyan.aaradhyaschoolbusservice.viewModel.PdfViewModel
import com.bypriyan.aaradhyaschoolbusservice.viewModel.TokenViewModel
import com.bypriyan.aaradhyaschoolbusservice.viewModel.UserViewModel
import com.bypriyan.bustrackingsystem.utility.Constants
import com.bypriyan.bustrackingsystem.utility.PreferenceManager
import com.google.firebase.messaging.FirebaseMessaging
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
    private val getUserReservationViewModel: GetUserReservationViewModel by viewModels()
    lateinit var userId: String
    private val tokenViewModel: TokenViewModel by viewModels()

    // Global variables
    lateinit var totalAmount: String
    lateinit var amountPaid: String
    lateinit var plan: String
    lateinit var installmentPaid: String
    lateinit var pickupLocation: String
    lateinit var dropLocation: String
    lateinit var pickupRoute: String
    lateinit var dropRoute: String
    lateinit var mobileNum1: String
    lateinit var mobileNum2: String

    lateinit var paymentDate: String
    lateinit var paymentId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

       userId =  preferenceManager.getString(Constants.KEY_USER_ID)!!
        userViewModel.fetchUser(userId)
        getUserReservationViewModel.fetchReservations(userId)

        userViewModel.user.observe(this) { userDetails ->
            userDetails?.data?.let { data ->
                Log.d("TAGss", "onCreate: $data")
                loadImageWithGlide(Constants.KEY_IMAGE_PATH+data.image_url)
                binding.name.text = data.full_name
                uploadToken(data.id.toString())
                preferenceManager.apply {
                    putString(Constants.KEY_FULL_NAME, data.full_name ?: "")
                    putString(Constants.KEY_EMAIL, data.email ?: "")
                    putString(Constants.KEY_USER_CLASS, data.`class`?: "")
                    putString(Constants.KEY_IMAGE, data.image_url ?: "")
                    putString(Constants.KEY_YEAR, data.year ?: "")
                    putString(Constants.KEY_STANDARD, data.standard ?: "")
                    putString(Constants.KEY_AGE, data.age.toString() ?: "")

                    putString(Constants.KEY_FATHER_NAME, data.guardians[0].name)
                    putString(Constants.KEY_FATHER_NUMBER, data.guardians[0].phone_number)

                    putString(Constants.KEY_MOTHER_NAME, data.guardians[1].name)
                    putString(Constants.KEY_MOTHER_NUMBER, data.guardians[1].phone_number)

                    putString(Constants.KEY_GUARDIAN_NAME, data.guardians[2].name)
                    putString(Constants.KEY_GUARDIAN_PHONE, data.guardians[2].phone_number)
                }
            } ?: run {
                Log.e("UserDetails", "userDetails or data is null")
            }
        }


        // Observe LiveData
        getUserReservationViewModel.reservations.observe(this, Observer { result ->
            result.onSuccess { response ->
                saveReservationDetails(response)
            }.onFailure { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })

        binding.signOut.setOnClickListener {
            preferenceManager.clear()
            preferenceManager.putBoolean(Constants.KEY_IS_ONBORDING_SCREEN_SEEN, true)
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

//        userViewModel.getUserDetails(userId, token)

        binding.SlabStructureBtn.setOnClickListener {
            val intent = Intent(this, SlabActivity::class.java)
            intent.putExtra("hideButton", true)  // Send the extra to hide the button
            startActivity(intent)
        }

        binding.name.text = preferenceManager.getString(Constants.KEY_FULL_NAME)
        loadImageWithGlide(Constants.KEY_IMAGE_PATH+preferenceManager.getString(Constants.KEY_IMAGE))

        binding.profileImage.setOnClickListener {
//            userViewModel.userDetails.value?.let { userDetails ->
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
//            }
        }

        binding.CheckOutAct.setOnClickListener {
            startActivity(Intent(this, CheckOut1::class.java))
        }

        binding.DownloadRecieptBtn.setOnClickListener {
            // Implement receipt download logic
            logReceiptDetails(preferenceManager)
        }

        pdfViewModel.pdfState.observe(this, Observer { result ->
            result?.onSuccess { filePath ->
                Toast.makeText(this@DashBoard1Activity, "PDF Saved at: $filePath", Toast.LENGTH_LONG).show()
            }?.onFailure {
                Toast.makeText(this@DashBoard1Activity, "Error", Toast.LENGTH_LONG).show()
            }
        })


        binding.paidNextAmount.setOnClickListener {
            var intent = Intent(this, PaymentNextTimeActivity::class.java)
            intent.putExtra(Constants.KEY_TOTAL_AMOUNT, totalAmount)
            intent.putExtra(Constants.KEY_AMOUNT_PAID, amountPaid)
            startActivity(intent)
        }

        binding.signOut.setOnClickListener {
            preferenceManager.clear()
            preferenceManager.putBoolean(Constants.KEY_IS_ONBORDING_SCREEN_SEEN, true)
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() // Finish current activity

        }
    }

    fun logReceiptDetails(preferenceManager: PreferenceManager) {
        val date = preferenceManager.getString(Constants.KEY_DATE)
        val studentName = preferenceManager.getString(Constants.KEY_FULL_NAME)
        val address = "Pimpri Chinchwad, Oppo. to Alankapuram Society, Alandi Rd, Wadmukhwadi, Pune"
        val mobileNo = "+91 9766987118"
        val amount = preferenceManager.getString(Constants.KEY_AMOUNT_PAID)
        val std = preferenceManager.getString(Constants.KEY_STANDARD)
        val totalFees = preferenceManager.getString(Constants.KEY_TOTAL_AMOUNT)

        val monthFrom = preferenceManager.getString(Constants.KEY_MONTH_FROM)
        val monthTo = preferenceManager.getString(Constants.KEY_MONTH_TO)
        val receiptNo = preferenceManager.getString(Constants.KEY_RECEIPT_NO)

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



    companion object {
        private const val REQUEST_CODE_STORAGE_PERMISSION = 1001
    }

    private fun loadImageWithGlide(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl) // Load the image URL
            .into(binding.profileImage) // Set the image to the ImageView
    }

    private fun uploadToken(userId: String){
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM_TOKEN", "FCM Token: $token")
                tokenViewModel.insertOrUpdateToken(userId, token)
            } else {
                Log.e("FCM_TOKEN", "Failed to get FCM token", task.exception)
            }
        }
    }

    private fun saveReservationDetails(response: ReservationResponse) {
        totalAmount = response.reservations?.get(0)?.total_amount.toString()
        amountPaid = response.reservations?.get(0)?.amount_paid.toString()
        plan = response.reservations?.get(0)?.plan.toString()
        installmentPaid = response.reservations?.get(0)?.installment_paid.toString()
        pickupLocation = response.reservations?.get(0)?.pickup_location.toString()
        dropLocation = response.reservations?.get(0)?.drop_location.toString()
        pickupRoute = response.reservations?.get(0)?.pickup_route.toString()
        dropRoute = response.reservations?.get(0)?.drop_route.toString()
        mobileNum1 = response.reservations?.get(0)?.mobileNum1.toString()
        mobileNum2 = response.reservations?.get(0)?.mobileNum2.toString()

        paymentDate = response.reservations?.get(0)?.payment_date.toString()
        paymentId = response.reservations?.get(0)?.payment_id.toString()

        binding.PickupRouteTv.text = pickupRoute?:"waiting..."
        binding.DropRouteTv.text = dropRoute?:"waiting..."
        binding.mob1Tv.text = mobileNum1
        binding.mob2Tv.text = mobileNum2

        if(totalAmount==amountPaid){
            preferenceManager.putString(Constants.KEY_MONTH_FROM, "April")
            preferenceManager.putString(Constants.KEY_MONTH_TO, "March")
        }else{
            preferenceManager.putString(Constants.KEY_MONTH_FROM, "April")
            preferenceManager.putString(Constants.KEY_MONTH_TO, "August")
        }

        preferenceManager.apply {
            putString(Constants.KEY_TOTAL_AMOUNT, totalAmount)
            putString(Constants.KEY_AMOUNT_PAID, amountPaid)
            putString(Constants.KEY_PLAN, plan)
            putString(Constants.KEY_INSTALLMENT_PAID, installmentPaid)
            putString(Constants.KEY_PICKUP_LOCATION, pickupLocation)
            putString(Constants.KEY_DROP_LOCATION, dropLocation)
            putString(Constants.KEY_PICKUP_ROUTE, pickupRoute)
            putString(Constants.KEY_DROP_ROUTE, dropRoute)
            putString(Constants.KEY_MOBILE_NUM1, mobileNum1)
            putString(Constants.KEY_MOBILE_NUM2, mobileNum2)
            putString(Constants.KEY_DATE, paymentDate)
            putString(Constants.KEY_RECEIPT_NO, paymentId)
        }
    }
}


