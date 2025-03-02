package com.bypriyan.aaradhyaschoolbusservice.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityCheckOutBinding
import com.bypriyan.bustrackingsystem.utility.PreferenceManager

class DashBoard1Activity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckOutBinding
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCheckOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceManager = PreferenceManager(this)

        val paidAmount = preferenceManager.getString("paid_amount") ?: "0"
        val installmentStatus = preferenceManager.getString("installment_status") ?: "0"
        binding.paidNextAmount.text = " Payment Status:"

        binding.SlabStructureBtn.setOnClickListener {
            val intent = Intent(this, SlabActivity::class.java)
            intent.putExtra("hideButton", true)  // Send the extra to hide the button
            startActivity(intent)
        }

        binding.CheckOutAct.setOnClickListener {
            startActivity(Intent(this, CheckOut1::class.java))
        }

        binding.DownloadRecieptBtn.setOnClickListener {
            // Implement receipt download logic
        }

        binding.paidNextAmount.setOnClickListener {
            startActivity(Intent(this, PaymentOptionActivity::class.java))
        }
    }
}
