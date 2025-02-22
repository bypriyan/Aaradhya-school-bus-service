package com.bypriyan.aaradhyaschoolbusservice.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityTermsAndConditionBinding

class TermsAndConditionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTermsAndConditionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Correct View Binding Initialization
        binding = ActivityTermsAndConditionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnConfirm.setOnClickListener(){
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
