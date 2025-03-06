package com.bypriyan.aaradhyaschoolbusservice.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityPaymentDoneBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PaymentDoneActivity : AppCompatActivity() {

    lateinit var binding: ActivityPaymentDoneBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityPaymentDoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtTransactionNumber.text = intent.getStringExtra("id")!!

        binding.btnDone.setOnClickListener(){
            startActivity(Intent(this, DashBoard1Activity::class.java))
            finish()
        }

        binding.btnDownload.setOnClickListener{

        }

    }


}