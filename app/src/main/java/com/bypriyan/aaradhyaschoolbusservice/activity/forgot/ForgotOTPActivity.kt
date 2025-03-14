package com.bypriyan.aaradhyaschoolbusservice.activity.forgot

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bypriyan.aaradhyaschoolbusservice.R
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityForgotEmailBinding
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityForgotOtpactivityBinding
import com.bypriyan.bustrackingsystem.utility.Constants

class ForgotOTPActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotOtpactivityBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var otp = intent.getStringExtra(Constants.KEY_OTP)
        var email = intent.getStringExtra(Constants.KEY_EMAIL)

        binding.continueBtn.setOnClickListener {
            if (binding.firstPinView.text.toString().isNotEmpty() || email != null || otp != null || !(binding.firstPinView.text.toString() == otp)) {
                var intent = Intent(this, ResetPasswordActivity::class.java).apply {
                    putExtra(Constants.KEY_EMAIL, email)
                }
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
            }
        }

    }
}