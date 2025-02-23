package com.bypriyan.aaradhyaschoolbusservice.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.bypriyan.aaradhyaschoolbusservice.R
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivitySignUpBinding
import com.bypriyan.aaradhyaschoolbusservice.viewModel.OTPViewModel
import com.bypriyan.bustrackingsystem.utility.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySignUpBinding
    //viewModel
    private val otpViewModel: OTPViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sendOTPBtn.setOnClickListener {
            otpViewModel.sendOtp(binding.emailEt.text.toString())
            if(validateInputFields()){
                isLoading(true)
                otpViewModel.sendOtp(binding.emailEt.text.toString())
            }
        }

        otpViewModel.otpResponse.observe(this, Observer { result ->
            result?.let {
                it.onSuccess { response ->
                    startOtpActivity()
                    isLoading(false)
                }.onFailure { error ->
                    isLoading(false)
                    Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                }
            }
        })

    }

    private fun validateInputFields(): Boolean {
        return when {
            binding.fullNameEt.text.isNullOrBlank() -> {
                binding.fullName.error = "Full Name is required"
                false
            }
            binding.standerdEt.text.isNullOrBlank() -> {
                binding.standerdTi.error = "Standard is required"
                false
            }
            binding.classEt.text.isNullOrBlank() -> {
                binding.classs.error = "Class is required"
                false
            }
            binding.ageEt.text.isNullOrBlank() || binding.ageEt.text.toString().toInt() <= 0 -> {
                binding.age.error = "Invalid age"
                false
            }
            binding.yearEt.text.isNullOrBlank() -> {
                binding.year.error = "Year is required"
                false
            }
            binding.fatherNameEt.text.isNullOrBlank() -> {
                binding.fatherName.error = "Father's Name is required"
                false
            }
            binding.fPhoneNumEt.text.isNullOrBlank() || binding.fPhoneNumEt.text.toString().length != 10 || !binding.fPhoneNumEt.text.toString().all { it.isDigit() } -> {
                binding.fPhoneNum.error = "Invalid Father's Phone Number"
                false
            }
            binding.mothersName.text.isNullOrBlank() -> {
                binding.motherNAME.error = "Mother's Name is required"
                false
            }
            binding.mPhoneEt.text.isNullOrBlank() || binding.mPhoneEt.text.toString().length != 10 || !binding.mPhoneEt.text.toString().all { it.isDigit() } -> {
                binding.mPhoneNumber.error = "Invalid Mother's Phone Number"
                false
            }
            binding.emailEt.text.isNullOrBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(binding.emailEt.text.toString()).matches() -> {
                binding.email.error = "Invalid email address"
                false
            }
            binding.passwordEt.text.isNullOrBlank() || binding.passwordEt.text.toString().length < 6 -> {
                binding.password.error = "Password must be at least 6 characters"
                false
            }
            else -> true
        }

    }

    private fun startOtpActivity(){

        val fullName = binding.fullNameEt.text.toString()
        val standard = binding.standerdEt.text.toString()
        val className = binding.classEt.text.toString()
        val age = binding.ageEt.text.toString()
        val year = binding.yearEt.text.toString()
        val fatherName = binding.fatherNameEt.text.toString()
        val fatherPhone = binding.fPhoneNumEt.text.toString()
        val motherName = binding.mothersName.text.toString()
        val motherPhone = binding.mPhoneEt.text.toString()
        val email = binding.emailEt.text.toString()
        val password = binding.passwordEt.text.toString()

        val intent = Intent(this, OtpActivity::class.java)
        intent.putExtra(Constants.KEY_FULL_NAME, fullName)
        intent.putExtra(Constants.KEY_STANDARD, standard)
        intent.putExtra(Constants.KEY_CLASS, className)
        intent.putExtra(Constants.KEY_AGE, age)
        intent.putExtra(Constants.KEY_YEAR, year)
        intent.putExtra(Constants.KEY_FATHER_NAME, fatherName)
        intent.putExtra(Constants.KEY_FATHER_PHONE, fatherPhone)
        intent.putExtra(Constants.KEY_MOTHER_NAME, motherName)
        intent.putExtra(Constants.KEY_MOTHER_PHONE, motherPhone)
        intent.putExtra(Constants.KEY_EMAIL, email)
        intent.putExtra(Constants.KEY_PASSWORD, password)
        // Start the next activity
        startActivity(intent)
    }

    fun isLoading(isLoading: Boolean){
        if (isLoading){
            binding.progressbar.visibility = View.VISIBLE
            binding.sendOTPBtn.visibility = View.GONE
        }else{
            binding.progressbar.visibility = View.GONE
            binding.sendOTPBtn.visibility = View.VISIBLE
        }
    }


}