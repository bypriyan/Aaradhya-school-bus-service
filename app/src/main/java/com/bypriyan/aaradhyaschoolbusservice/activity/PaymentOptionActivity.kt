package com.bypriyan.aaradhyaschoolbusservice.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityPaymentOptionBinding
import com.bypriyan.aaradhyaschoolbusservice.viewModel.ReservationViewModel
import com.bypriyan.bustrackingsystem.utility.Constants
import com.bypriyan.bustrackingsystem.utility.PreferenceManager
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class PaymentOptionActivity : AppCompatActivity(), PaymentResultListener {

    lateinit var binding: ActivityPaymentOptionBinding
    private val viewModel: ReservationViewModel by viewModels()
    lateinit var userId: String
    lateinit var token: String
    lateinit var token_type: String

    lateinit var pickupLocation: String
    lateinit var dropLocation: String
    lateinit var pickupLatitude: String
    lateinit var pickupLongitude: String
    lateinit var dropLatitude: String
    lateinit var dropLongitude: String
    lateinit var mode: String
    lateinit var paymentId: String

    @Inject
    lateinit var preferenceManager: PreferenceManager

    val slabs = listOf(
        Slab(0.0..1.0, 3800, 3800, 2850, 10450),
        Slab(1.1..2.0, 4600, 4600, 3450, 12650),
        Slab(2.1..3.0, 5000, 5000, 3750, 13750),
        Slab(3.1..5.0, 6360, 6360, 4770, 17400),
        Slab(5.1..8.0, 8000, 8000, 6000, 22000),
        Slab(8.1..11.0, 10000, 10000, 7500, 27500),
        Slab(11.1..15.0, 11600, 11600, 8700, 31900)
    )

    private var firstInstallmentPrice = 0
    private var secondInstallmentPrice = 0
    private var thirdInstallmentPrice = 0
    private var totalPrice = 0
    var newTotal = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentOptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val totalDistance = intent.getFloatExtra("TOTAL_DISTANCE", 0f)
        val prices = calculatePrices(totalDistance.toDouble())

        mode = intent.getStringExtra("MODE") ?: ""
        val distanceText = "Total Distance: %.2f km".format(totalDistance)
        userId = preferenceManager.getString(Constants.KEY_USER_ID)!!
        token = preferenceManager.getString(Constants.KEY_TOKEN)!!

         pickupLocation = intent.getStringExtra("PICKUP_LOCATION")!!
         dropLocation = intent.getStringExtra("DROP_LOCATION")!!
         pickupLatitude = intent.getDoubleExtra("PICKUP_LATITUDE", 0.0).toString()!!
         pickupLongitude = intent.getDoubleExtra("PICKUP_LONGITUDE", 0.0).toString()!!
         dropLatitude = intent.getDoubleExtra("DROP_LATITUDE", 0.0).toString()!!
         dropLongitude = intent.getDoubleExtra("DROP_LONGITUDE", 0.0).toString()!!

        // Create reservation map with all values as Strings
        viewModel.reservationResponse.observe(this, Observer { response ->
            // Handle the response
            Log.d("payss", "onCreate: $response")
            var intent =Intent(this, PaymentDoneActivity::class.java)
            intent.putExtra("id", paymentId)
            startActivity(intent)
            finish()
        })

        prices?.let {
            firstInstallmentPrice = it[0].split(": ")[1].toInt()
            secondInstallmentPrice = it[1].split(": ")[1].toInt()
            thirdInstallmentPrice = it[2].split(": ")[1].toInt()
            totalPrice = it[3].split(": ")[1].toInt()

            // Show total amount initially
            binding.totalCostTv.text = "PAY  " + "₹$totalPrice"
            binding.allTotalPriseTv.text = "₹$totalPrice"

            // Show first installment option for EMI
            binding.firstInstallmentTv.text = "₹$firstInstallmentPrice"
        }

        // Full payment button
        binding.totalCostTv.setOnClickListener {
            startPayment(totalPrice) // Pay total amount
        }

        binding.continueBtn.text = "Pay  " + "$firstInstallmentPrice"
        // EMI first installment button
        binding.continueBtn.setOnClickListener {
            startPayment(firstInstallmentPrice) // Pay only first installment
        }
    }


    private fun updateTotalPrice() {
        newTotal = 0

        // Update total price TextView
        binding.allTotalPriseTv.text = "₹$newTotal"
        binding.totalCostTv.text = "₹$newTotal"
    }

    private fun startPayment(amount: Int) {
        val checkout = Checkout()
        checkout.setKeyID("rzp_test_NECKQH8SMMRhJ6")

        try {
            val options = JSONObject()
            options.put("name", "Your App Name")
            options.put("description", "Test Payment")
            options.put("currency", "INR")
            options.put("amount", amount * 100) // Convert to paise
            options.put("prefill.email", "user@example.com")
            options.put("prefill.contact", "9876543210")
            checkout.open(this, options)
        } catch (e: Exception) {
            Toast.makeText(this, "Error in Payment: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    fun calculatePrices(distance: Double): Array<String>? {
        val slab = slabs.find { distance in it.range }
        return slab?.let {
            arrayOf(
                "1st Installment: ${it.firstInstallment}",
                "2nd Installment: ${it.secondInstallment}",
                "3rd Installment: ${it.thirdInstallment}",
                "Yearly: ${it.yearly}"
            )
        }
    }

    override fun onPaymentSuccess(razorpayPaymentID: String?) {
        val reservation = mapOf(
            "user_id" to userId,
            "pickup_location" to (pickupLocation ?: ""),
            "drop_location" to (dropLocation ?: ""),
            "pickup_latitude" to pickupLatitude.toString(),
            "pickup_longitude" to pickupLongitude.toString(),
            "drop_latitude" to dropLatitude.toString(),
            "drop_longitude" to dropLongitude.toString(),
            "paid" to firstInstallmentPrice.toString(),
            "total_amount" to totalPrice.toString(),
            "installment_paid" to "1",
            "plan" to mode,
            "payment_id" to (razorpayPaymentID ?: "")
        )
        paymentId = razorpayPaymentID ?: ""

        viewModel.storeReservation(token, reservation)
    }

    override fun onPaymentError(code: Int, response: String?) {
        Toast.makeText(this, "Payment Failed: $response", Toast.LENGTH_SHORT).show()
    }
}

data class Slab(
    val range: ClosedRange<Double>,
    val firstInstallment: Int,
    val secondInstallment: Int,
    val thirdInstallment: Int,
    val yearly: Int
)