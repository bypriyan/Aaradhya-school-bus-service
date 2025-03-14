package com.bypriyan.aaradhyaschoolbusservice.activity.forgot

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.bypriyan.aaradhyaschoolbusservice.R
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityForgotEmailBinding
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityLoginBinding
import com.bypriyan.aaradhyaschoolbusservice.viewModel.OTPViewModel
import com.bypriyan.bustrackingsystem.utility.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class ForgotEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotEmailBinding
    //viewModel
    private val otpViewModel: OTPViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sendOTPBtn.setOnClickListener {
            if(binding.usernameET.text.toString().isNotEmpty()){
                isLoading(true)
                otpViewModel.sendOtp(binding.usernameET.text.toString())
            }
        }

        otpViewModel.otpResponse.observe(this, Observer { result ->
            result?.let {
                isLoading(false)
                it.onSuccess { response ->
                    var intent = Intent(this, ForgotOTPActivity::class.java).apply {
                        putExtra(Constants.KEY_OTP, response.otp.toString())
                        putExtra(Constants.KEY_EMAIL, binding.usernameET.text.toString())
                    }
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                    }
                }.onFailure { error ->
                    Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                }
            }
        })

    }

    private fun isLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressbar.visibility = View.VISIBLE
            binding.sendOTPBtn.visibility = View.GONE
        } else {
            binding.progressbar.visibility = View.GONE
            binding.sendOTPBtn.visibility = View.VISIBLE
        }
    }

}