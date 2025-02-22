package com.bypriyan.aaradhyaschoolbusservice.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bypriyan.aaradhyaschoolbusservice.R
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityPaymentOptionBinding

class PaymentOptionActivity : AppCompatActivity() {
    lateinit var binding : ActivityPaymentOptionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityPaymentOptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnConfirmPay.setOnClickListener(){
            startActivity(Intent(this, PaymentDoneActivity::class.java))
            finish()
        }
    }
}