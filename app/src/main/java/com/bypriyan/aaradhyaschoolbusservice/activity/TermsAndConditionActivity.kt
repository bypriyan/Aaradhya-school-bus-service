package com.bypriyan.aaradhyaschoolbusservice.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityTermsAndConditionBinding

class TermsAndConditionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTermsAndConditionBinding

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
