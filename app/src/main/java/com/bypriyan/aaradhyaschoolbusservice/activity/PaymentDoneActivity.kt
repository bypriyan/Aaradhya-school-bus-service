package com.bypriyan.aaradhyaschoolbusservice.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityPaymentDoneBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class PaymentDoneActivity : AppCompatActivity() {

    lateinit var binding: ActivityPaymentDoneBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityPaymentDoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtTransactionNumber.text = intent.getStringExtra("id")!!
        val amount = intent.getStringExtra("paid")!!
        binding.successfulAmount.text = "Successfully paid $amount"
        binding.amount.text = "₹"+amount
        binding.txtdate.text = getCurrentDateTime()

        binding.btnDone.setOnClickListener(){
            startActivity(Intent(this, DashBoard1Activity::class.java))
            finish()
        }


    }

    fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }

}