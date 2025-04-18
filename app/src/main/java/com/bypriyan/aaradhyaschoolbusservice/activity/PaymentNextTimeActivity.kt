package com.bypriyan.aaradhyaschoolbusservice.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bypriyan.aaradhyaschoolbusservice.R
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityPaymentNextTimeBinding
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityPaymentOptionBinding
import com.bypriyan.bustrackingsystem.utility.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.floor

@AndroidEntryPoint
class PaymentNextTimeActivity : AppCompatActivity() {

    lateinit var binding: ActivityPaymentNextTimeBinding
    val slabs = listOf(
        Slab(0.0..1.0, 3800, 3800, 2850, 10450),
        Slab(1.1..2.0, 4600, 4600, 3450, 12650),
        Slab(2.1..3.0, 5000, 5000, 3750, 13750),
        Slab(3.1..5.0, 6360, 6360, 4770, 17400),
        Slab(5.1..8.0, 8000, 8000, 6000, 22000),
        Slab(8.1..11.0, 10000, 10000, 7500, 27500),
        Slab(11.1..15.0, 11600, 11600, 8700, 31900)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentNextTimeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var totalPrice = intent.getStringExtra(Constants.KEY_TOTAL_AMOUNT) ?: ""
        val paidAmount = intent.getStringExtra(Constants.KEY_AMOUNT_PAID) ?: ""
        val plan  = intent.getStringExtra(Constants.KEY_PLAN) ?: ""

        Log.d("slap", "onCreate: $totalPrice $paidAmount")

        if(plan == "ONLY_DROP"){
            totalPrice = floor(addFortyTwoPointEightSixPercent(totalPrice).toDouble()).toInt().toString()
            if(totalPrice.toInt()<=10450){
                val slab = getSlabByPrice((totalPrice.toInt()+2).toString())
                Log.d("slap", "onCreate: $totalPrice $slab")

                binding.firstInstallmentTv.text = "₹${decreaseThirtyPercent(slab?.firstInstallment.toString())}"
                binding.SecondInstallmentTv.text = "₹${decreaseThirtyPercent(slab?.secondInstallment.toString())}"
                binding.thirdInstallmentTv.text = "₹${decreaseThirtyPercent(slab?.thirdInstallment.toString())}"
                binding.allTotalPriseTv.text = "₹${decreaseThirtyPercent(slab?.yearly.toString())}"

                if(totalPrice == addFortyTwoPointEightSixPercent(paidAmount.toString())){
                    binding.linearLayout.visibility = View.GONE
                    binding.status.text = "All payment done"
                }else{
                    binding.linearLayout.visibility = View.VISIBLE
                    binding.status.text = "You have paid 1st installment"
                    binding.firstInstallmentTv.text =  "₹${decreaseThirtyPercent(slab?.firstInstallment.toString())}"
                    binding.showtxt.visibility = View.GONE
                }
            }else{
                val slab = getSlabByPrice(totalPrice)
                Log.d("slap", "onCreate: $totalPrice $slab")

                binding.firstInstallmentTv.text = "₹${decreaseThirtyPercent(slab?.firstInstallment.toString())}"
                binding.SecondInstallmentTv.text = "₹${decreaseThirtyPercent(slab?.secondInstallment.toString())}"
                binding.thirdInstallmentTv.text = "₹${decreaseThirtyPercent(slab?.thirdInstallment.toString())}"
                binding.allTotalPriseTv.text = "₹${decreaseThirtyPercent(slab?.yearly.toString())}"

                if(totalPrice == addFortyTwoPointEightSixPercent(paidAmount.toString())){
                    binding.linearLayout.visibility = View.GONE
                    binding.status.text = "All payment done"
                }else{
                    binding.linearLayout.visibility = View.VISIBLE
                    binding.status.text = "You have paid 1st installment"
                    binding.firstInstallmentTv.text =  "₹${decreaseThirtyPercent(slab?.firstInstallment.toString())}"
                    binding.showtxt.visibility = View.GONE
                }
            }


        }else{
            val slab = getSlabByPrice(totalPrice)
            binding.firstInstallmentTv.text = "₹${slab?.firstInstallment}"
            binding.SecondInstallmentTv.text = "₹${slab?.secondInstallment}"
            binding.thirdInstallmentTv.text = "₹${slab?.thirdInstallment}"
            binding.allTotalPriseTv.text = "₹${slab?.yearly}"

            if(totalPrice.toInt() == paidAmount.toInt()){
                binding.linearLayout.visibility = View.GONE
                binding.status.text = "All payment done"
            }else{
                binding.linearLayout.visibility = View.VISIBLE
                binding.status.text = "You have paid 1st installment"
                binding.firstInstallmentTv.text = "paid : ₹${slab?.firstInstallment}"
                binding.showtxt.visibility = View.GONE
            }
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

    fun getSlabByPrice(totalPrice: String): Slab? {
        return slabs.find { it.yearly == totalPrice.toIntOrNull() }
    }

    fun addFortyTwoPointEightSixPercent(value: String): String {
        return try {
            val number = value.toDouble()  // Convert string to double
            val increasedValue = number * 1.4286  // Add 42.86%
            "%.2f".format(increasedValue)  // Format to 2 decimal places
        } catch (e: NumberFormatException) {
            "Invalid number"
        }
    }

    fun decreaseFortyTwoPointEightSixPercent(value: String): String {
        return try {
            val number = value.toDouble()  // Convert string to double
            val decreasedValue = number * 0.5714  // Decrease by 42.86%
            "%.2f".format(decreasedValue)  // Format to 2 decimal places
        } catch (e: NumberFormatException) {
            "Invalid number"
        }
    }

    fun addThirtyPercent(value: String): String {
        return try {
            val number = value.toDouble()  // Convert string to double
            val increasedValue = number * 1.3  // Add 30%
            "%.2f".format(increasedValue)  // Format to 2 decimal places
        } catch (e: NumberFormatException) {
            "Invalid number"
        }
    }

    fun decreaseThirtyPercent(value: String): String {
        return try {
            val number = value.toDouble()  // Convert string to double
            val decreasedValue = number * 0.7  // Decrease by 30%
            "%.2f".format(decreasedValue)  // Format to 2 decimal places
        } catch (e: NumberFormatException) {
            "Invalid number"
        }
    }
}