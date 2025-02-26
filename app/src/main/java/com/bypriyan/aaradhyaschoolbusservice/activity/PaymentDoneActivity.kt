package com.bypriyan.aaradhyaschoolbusservice.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bypriyan.aaradhyaschoolbusservice.R
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityDasboardBinding
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityPaymentDoneBinding

class PaymentDoneActivity : AppCompatActivity() {
    lateinit var binding: ActivityPaymentDoneBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityPaymentDoneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        


        binding.btnDone.setOnClickListener(){
            startActivity(Intent(this, DasboardActivity::class.java))
            finish()
        }
    }
}