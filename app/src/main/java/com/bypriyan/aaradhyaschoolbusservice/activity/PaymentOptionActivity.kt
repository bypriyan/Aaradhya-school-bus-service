package com.bypriyan.aaradhyaschoolbusservice.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityPaymentOptionBinding
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject



class PaymentOptionActivity : AppCompatActivity(), PaymentResultListener {
    lateinit var binding: ActivityPaymentOptionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPaymentOptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val totalDistance = intent.getFloatExtra("TOTAL_DISTANCE", 0f)
        val mode = intent.getStringExtra("MODE") ?: ""
        val distanceText = "Total Distance: %.2f km".format(totalDistance)
        binding.txtTotalAmount.text = distanceText

        // Calculate price based on distance slabs
        val price = when {
            totalDistance <= 1 -> 3800
            totalDistance <= 2 -> 4600
            totalDistance <= 3 -> 5000
            totalDistance <= 5 -> 6360
            totalDistance <= 8 -> 8000
            totalDistance <= 11 -> 10000
            totalDistance <= 15 -> 11600
            else -> 0 // Default case if distance exceeds available slabs
        }

        binding.txtTotalAmountPay.text = price.toString()

        binding.btnConfirmPay.setOnClickListener {
            startPayment(price)
        }
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

    override fun onPaymentSuccess(razorpayPaymentID: String?) {
        Toast.makeText(this, "Payment Successful: $razorpayPaymentID", Toast.LENGTH_SHORT).show()
    }

    override fun onPaymentError(code: Int, response: String?) {
        Toast.makeText(this, "Payment Failed: $response", Toast.LENGTH_SHORT).show()
    }
}
