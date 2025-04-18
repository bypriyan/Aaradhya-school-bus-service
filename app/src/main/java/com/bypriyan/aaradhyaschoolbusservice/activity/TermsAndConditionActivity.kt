package com.bypriyan.aaradhyaschoolbusservice.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityTermsAndConditionBinding

class TermsAndConditionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTermsAndConditionBinding
    private var backPressedTime: Long = 0
    private val backPressThreshold: Long = 2000 // 2 seconds
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTermsAndConditionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnConfirm.setOnClickListener {
            if (areAllCheckboxesChecked()) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Please confirm all terms and conditions.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onBackPressed() {
        if (backPressedTime + backPressThreshold > System.currentTimeMillis()) {
            super.onBackPressed()
            finish()
        } else {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()
    }

    private fun areAllCheckboxesChecked(): Boolean {
        // Check if all checkboxes are checked
        return binding.checkNearestPickup.isChecked &&
                binding.checkNonRefundable.isChecked &&
                binding.checkNarrowLane.isChecked &&
                binding.checkPaymentFor11Month.isChecked &&
                binding.checkRTOGuideline.isChecked &&
                binding.checkGps.isChecked &&
                binding.ofiiceTime.isChecked
    }
}
