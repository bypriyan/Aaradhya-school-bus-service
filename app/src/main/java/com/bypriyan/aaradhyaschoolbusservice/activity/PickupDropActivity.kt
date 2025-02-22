package com.bypriyan.aaradhyaschoolbusservice.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bypriyan.aaradhyaschoolbusservice.R
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityPickupDropBinding

class PickupDropActivity : AppCompatActivity() {
    lateinit var  binding : ActivityPickupDropBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPickupDropBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnPayment.setOnClickListener(){
            startActivity(Intent(this, PaymentOptionActivity::class.java))
        }
    }
}