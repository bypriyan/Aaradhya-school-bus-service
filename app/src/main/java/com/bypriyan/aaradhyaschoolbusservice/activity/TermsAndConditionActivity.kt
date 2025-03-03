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

        // Correct View Binding Initialization
        binding = ActivityTermsAndConditionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnConfirm.setOnClickListener {
            // Check if all checkboxes are selected
            if (areAllCheckboxesChecked()) {
                // All checkboxes are selected, proceed to next activity
                startActivity(Intent(this, LoginActivity::class.java))
                finish() // Close current activity
            } else {
                // Show a message to the user to select all checkboxes
                Toast.makeText(this, "Please confirm all terms and conditions.", Toast.LENGTH_SHORT).show()
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
