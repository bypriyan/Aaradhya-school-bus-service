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
import com.bypriyan.aaradhyaschoolbusservice.viewModel.EmailViewModel
import com.bypriyan.aaradhyaschoolbusservice.viewModel.OTPViewModel
import com.bypriyan.bustrackingsystem.utility.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class ForgotEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotEmailBinding
    //viewModel
    private val otpViewModel: OTPViewModel by viewModels()
    private val emailViewModel: EmailViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sendOTPBtn.setOnClickListener {
            if(binding.usernameET.text.toString().isNotEmpty()){
                isLoading(true)
                emailViewModel.sendOtp(binding.usernameET.text.toString())
            }

        }

        emailViewModel.otpLiveData.observe(this) { result ->
            result.onSuccess { otp ->
                var intent = Intent(this, ForgotOTPActivity::class.java).apply {
                    putExtra(Constants.KEY_OTP, otp)
                    putExtra(Constants.KEY_EMAIL, binding.usernameET.text.toString())
                }
                startActivity(intent)
            }.onFailure {
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }


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