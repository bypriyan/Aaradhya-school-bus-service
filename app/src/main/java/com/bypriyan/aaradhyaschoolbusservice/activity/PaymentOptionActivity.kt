package com.bypriyan.aaradhyaschoolbusservice.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityPaymentOptionBinding
import com.bypriyan.aaradhyaschoolbusservice.viewModel.ReservationViewModel
import com.bypriyan.bustrackingsystem.utility.Constants
import com.bypriyan.bustrackingsystem.utility.PreferenceManager
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.collections.contains
import kotlin.text.toInt
import kotlin.times

@AndroidEntryPoint
class PaymentOptionActivity : AppCompatActivity(), PaymentResultListener {

    lateinit var binding: ActivityPaymentOptionBinding
    private val viewModel: ReservationViewModel by viewModels()
    lateinit var userId: String

    lateinit var pickupLocation: String
    lateinit var dropLocation: String
    lateinit var pickupLatitude: String
    lateinit var pickupLongitude: String
    lateinit var dropLatitude: String
    lateinit var dropLongitude: String
    private var mode: String = "DEFAULT" // Provide a default value to avoid uninitialized acces
    lateinit var paymentId: String

    @Inject
    lateinit var preferenceManager: PreferenceManager

    val slabs = listOf(
        Slab(0.0..1.0, 3800, 3800, 2850, 10450),       // 0.0 to 1.0 km
        Slab(1.0..2.0, 4600, 4600, 3450, 12650),       // 1.0 to 2.0 km
        Slab(2.0..3.0, 5000, 5000, 3750, 13750),       // 2.0 to 3.0 km
        Slab(3.0..5.0, 6360, 6360, 4770, 17400),       // 3.0 to 5.0 km
        Slab(5.0..8.0, 8000, 8000, 6000, 22000),       // 5.0 to 8.0 km
        Slab(8.0..11.0, 10000, 10000, 7500, 27500),    // 8.0 to 11.0 km
        Slab(11.0..15.0, 11600, 11600, 8700, 31900)    // 11.0 to 15.0 km
    )

    private var firstInstallmentPrice = 0
    private var secondInstallmentPrice = 0
    private var thirdInstallmentPrice = 0
    private var totalPrice = 0
    var newTotal = 0
    private var installmentStatus = 0
    private var isFullPaymentDone: Boolean = false // Flag to track full payment
    private var paidAmount =0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentOptionBinding.inflate(layoutInflater)
        setContentView(binding.root)


        mode = intent.getStringExtra("MODE") ?: ""
        isFullPaymentDone = preferenceManager.getBoolean(Constants.KEY_FULL_PAYMENT_DONE)
        // Fetch saved installment status
        installmentStatus = preferenceManager.getString("installment_status")?.toInt() ?: 0

        val totalDistance = intent.getFloatExtra("TOTAL_DISTANCE", 0f)
        val prices = calculatePrices(totalDistance.toDouble(), mode)

        Log.d("juhu", "onCreate: $prices")

        val distanceText = "Total Distance: %.2f km".format(totalDistance)
        userId = preferenceManager.getString(Constants.KEY_USER_ID) ?: ""
        preferenceManager.putString(Constants.KEY_TOTAL_FEES, totalPrice.toString())

        if (userId.isEmpty()) {
            Log.e("PaymentOptionActivity", "User ID or Token is missing!")
            Toast.makeText(this, "User authentication failed!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Handle ONLY_DROP mode
        if (mode == "ONLY_DROP") {
            dropLocation = intent.getStringExtra("DROP_LOCATION") ?: ""
            dropLatitude = intent.getDoubleExtra("DROP_LATITUDE", 0.0).toString()
            dropLongitude = intent.getDoubleExtra("DROP_LONGITUDE", 0.0).toString()
            pickupLocation = "N/A"
        } else {
            pickupLocation = intent.getStringExtra("PICKUP_LOCATION") ?: ""
            dropLocation = intent.getStringExtra("DROP_LOCATION") ?: ""
            pickupLatitude = intent.getDoubleExtra("PICKUP_LATITUDE", 0.0).toString()
            pickupLongitude = intent.getDoubleExtra("PICKUP_LONGITUDE", 0.0).toString()
            dropLatitude = intent.getDoubleExtra("DROP_LATITUDE", 0.0).toString()
            dropLongitude = intent.getDoubleExtra("DROP_LONGITUDE", 0.0).toString()
        }


        // Create reservation map with all values as Strings
        viewModel.responseMessage.observe(this, Observer { response ->
            Log.d("payss", "onCreate: $response")
            var intent = Intent(this, PaymentDoneActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra("id", paymentId.toString())
            intent.putExtra("paid", paidAmount.toString())
            startActivity(intent)
            finish()
            isPaymentLoading(false)
        })

        prices?.let{
            firstInstallmentPrice = it[0].split(": ")[1].toInt()
            secondInstallmentPrice = it[1].split(": ")[1].toInt()
            thirdInstallmentPrice = it[2].split(": ")[1].toInt()
            totalPrice = it[3].split(": ")[1].toInt()

            // Show total amount initially
            binding.totalCostTv.text = "PAY  " + "₹$totalPrice"
            binding.allTotalPriseTv.text = "₹$totalPrice"

            if(isFullPaymentDone){
                installmentStatus= 4
            }
            // Check installment status and update UI
            if (installmentStatus ==0) {
                binding.showtxt.text="Pay First Installment"
            }
            if (installmentStatus >= 1) {
                binding.firstInstallmentTv.text = "Paid ₹$firstInstallmentPrice"
                binding.showtxt.text = "Pay Second Installment"

            } else {
                binding.firstInstallmentTv.text = "₹$firstInstallmentPrice"

            }

            if (installmentStatus >= 2) {
                binding.SecondInstallmentTv.text = "Paid ₹$secondInstallmentPrice"
                binding.showtxt.text = "Pay Third Installment"
            } else {

                binding.SecondInstallmentTv.text = "₹$secondInstallmentPrice"
            }

            if (installmentStatus >= 3) {
                binding.thirdInstallmentTv.text = "Paid ₹$thirdInstallmentPrice"
                binding.showtxt.text = "you have paid all of your installments"

            } else {
                binding.thirdInstallmentTv.text = "₹$thirdInstallmentPrice"

            }
            // Disable the full payment option if installments have been paid
            if (installmentStatus > 0) {
                binding.totalCostTv.isEnabled = false
                binding.totalCostTv.alpha = 0.5f // To indicate the button is disabled
            }
            if(installmentStatus>3){
                binding.continueBtn.isEnabled = false
                binding.continueBtn.alpha = 0.5f // To indicate the button is disabled
            }
        }

        if(installmentStatus==4){
            binding.totalCostTv.isEnabled = false
            binding.totalCostTv.alpha = 0.5f
            binding.continueBtn.isEnabled = false
            binding.continueBtn.alpha = 0.5f
            binding.fullAmtTxt.text="You had paid full amount succesfully"
        }

        // Full payment button
        binding.totalCostTv.setOnClickListener {
            paidAmount = totalPrice
            startPayment(totalPrice) // Pay total amount
            installmentStatus=4
            preferenceManager.putString("installment_status", "4") // Save status persistently


        }

        if (installmentStatus == 0) {
            binding.continueBtn.text= "pay "+"₹$firstInstallmentPrice"
        } else if (installmentStatus == 1) {
            binding.continueBtn.text= "pay "+"₹$secondInstallmentPrice"

        } else if (installmentStatus == 2) {
            binding.continueBtn.text= "pay "+"₹$thirdInstallmentPrice"
        }
        else
        {
            binding.continueBtn.text= "Installment Completed"
            binding.continueBtn.isEnabled= false
            binding.continueBtn.alpha = 0.5f // To indicate the button is disabled
        }

        // EMI first installment button
        binding.continueBtn.setOnClickListener {
            paidAmount = firstInstallmentPrice
            installmentStatus=1
            startPayment(firstInstallmentPrice) // Pay only first installment
        }

        binding.backBtn.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
        //back pressed
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

//    private fun startPayment(amount: Int) {
//        isPaymentLoading(true)
//        val checkout = Checkout()
//        checkout.setKeyID("rzp_live_aK2ZZ0IwvS5LCe")
////rzp_test_NECKQH8SMMRhJ6      rzp_live_aK2ZZ0IwvS5LCe
//        try {
//            val options = JSONObject()
//            options.put("name", "Aaradhya school bus service")
//            options.put("description", "Bus FEE")
//            options.put("currency", "INR")
//            options.put("amount", amount * 100) // Convert to paise
//            options.put("prefill.email", "user@example.com")
//            options.put("prefill.contact", "9876543210")
//            checkout.open(this, options)
//        } catch (e: Exception) {
//            Toast.makeText(this, "Error in Payment: ${e.message}", Toast.LENGTH_SHORT).show()
//            e.printStackTrace()
//        }
//    }

    private fun startPayment(amount: Int) {
        isPaymentLoading(true)

        val url = "https://bypriyan.com/busApi/create_order.php"
        val jsonObject = JSONObject().apply {
            put("amount", amount)
            put("currency", "INR")
        }

        val request = JsonObjectRequest(Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    val orderId = response.getString("id") // ✅ Extracting the Order ID from API response
                    Log.d("payment", "startPayment: $response")
                    val checkout = Checkout()
                    checkout.setKeyID("rzp_live_aK2ZZ0IwvS5LCe")

                    val options = JSONObject().apply {
                        put("name", "Aaradhya school bus service")
                        put("description", "Bus FEE")
                        put("currency", "INR")
                        put("amount", amount * 100) // Convert to paise
                        put("order_id", orderId) // ✅ Passing the order ID to Razorpay
                        put("prefill.email", preferenceManager.getString(Constants.KEY_EMAIL))
                        put("prefill.contact", preferenceManager.getString(Constants.KEY_FATHER_NUMBER))
                    }

                    checkout.open(this, options)
                } catch (e: Exception) {
                    Toast.makeText(this, "Error in Payment: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error creating order: ${error.message}", Toast.LENGTH_SHORT).show()
            })

        Volley.newRequestQueue(this).add(request)
    }


    fun calculatePrices(distance: Double, mode: String): Array<String>? {
        val slab = slabs.find { distance in it.range }
        return slab?.let {
            var firstInstallment = it.firstInstallment
            var secondInstallment = it.secondInstallment
            var thirdInstallment = it.thirdInstallment
            var yearly = it.yearly

            // Apply 30% discount if mode is ONLY_DROP
            val discountFactor = if (mode == "ONLY_DROP") 0.7 else 1.0
            firstInstallment = (firstInstallment * discountFactor).toInt()
            secondInstallment = (secondInstallment * discountFactor).toInt()
            thirdInstallment = (thirdInstallment * discountFactor).toInt()
            yearly = (yearly * discountFactor).toInt()

            arrayOf(
                "1st Installment: $firstInstallment",
                "2nd Installment: $secondInstallment",
                "3rd Installment: $thirdInstallment",
                "Yearly: $yearly"
            )
        }
    }

    override fun onPaymentSuccess(razorpayPaymentID: String?) {

        preferenceManager.putString(Constants.KEY_AMOUNT,paidAmount.toString())
        preferenceManager.putString(Constants.KEY_TOTAL_FEES, totalPrice.toString())
        if(totalPrice==paidAmount){
            preferenceManager.putString(Constants.KEY_MONTH_FROM, "April")
            preferenceManager.putString(Constants.KEY_MONTH_TO, "March")
        }else{
            preferenceManager.putString(Constants.KEY_MONTH_FROM, "April")
            preferenceManager.putString(Constants.KEY_MONTH_TO, "August")
        }
        try {
            if (razorpayPaymentID.isNullOrEmpty()) {
                Toast.makeText(this, "Payment ID is null!", Toast.LENGTH_SHORT).show()
                return
            }

            if (userId.isEmpty()) {
                Log.e("PaymentOptionActivity", "User ID or Token is missing!")
                return
            }

            paymentId = razorpayPaymentID

            val finalPickupLocation = if (::pickupLocation.isInitialized) pickupLocation else "N/A"
            val finalPickupLatitude = if (::pickupLatitude.isInitialized) pickupLatitude else "0.0"
            val finalPickupLongitude = if (::pickupLongitude.isInitialized) pickupLongitude else "0.0"


            viewModel.createReservation(
                userId, finalPickupLocation, dropLocation, finalPickupLatitude, finalPickupLongitude,
                dropLatitude, dropLongitude, paidAmount.toString(), totalPrice.toString(),
                (installmentStatus).toString(), mode, razorpayPaymentID
            )

            // Update the installment status and payment status in PreferenceManager
            preferenceManager.putString("installment_status", (installmentStatus).toString())
            preferenceManager.putBoolean(Constants.PAYMENT_STATUS, true)

            //recipt
            preferenceManager.putString(Constants.KEY_RECEIPT_NO, paymentId)
            preferenceManager.putString(Constants.KEY_DATE, getCurrentDate())

            // Update the UI for paid installment
            if (installmentStatus == 0) {
                binding.firstInstallmentTv.text = "Paid ₹$firstInstallmentPrice"
            } else if (installmentStatus == 1) {
                binding.SecondInstallmentTv.text = "Paid ₹$secondInstallmentPrice"
            } else if (installmentStatus == 2) {
                binding.thirdInstallmentTv.text = "Paid ₹$thirdInstallmentPrice"
            }
        } catch (e: Exception) {
            Log.e("PaymentOptionActivity", "Error in onPaymentSuccess: ${e.message}")
            isPaymentLoading(false)

        }
    }

    private fun isPaymentLoading(loading: Boolean){
        if(loading){
            binding.paymentLoading.visibility = View.VISIBLE
        }else{
            binding.paymentLoading.visibility = View.GONE
        }
    }


    override fun onPaymentError(code: Int, response: String?) {
        Toast.makeText(this, "Payment Failed: $response", Toast.LENGTH_SHORT).show()
        isPaymentLoading(false)
    }

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }
}

data class Slab(
    val range: ClosedRange<Double>,
    val firstInstallment: Int,
    val secondInstallment: Int,
    val thirdInstallment: Int,
    val yearly: Int
)