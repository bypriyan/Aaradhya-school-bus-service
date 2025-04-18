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
import com.bypriyan.aaradhyaschoolbusservice.R
import com.bypriyan.aaradhyaschoolbusservice.activity.LoginActivity
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityForgotOtpactivityBinding
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityResetPasswordBinding
import com.bypriyan.aaradhyaschoolbusservice.viewModel.LoginViewModel
import com.bypriyan.bustrackingsystem.utility.Constants
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var email = intent.getStringExtra(Constants.KEY_EMAIL)

        binding.saveBtn.setOnClickListener {
            if (validatePasswords(binding.passwordEt, binding.confirmpasswordEt)) {
                isLoading(true)
                loginViewModel.resetPassword(email.toString(), binding.passwordEt.text.toString())
            }
        }

        loginViewModel.resetPasswordResponse.observe(this) { response ->
            isLoading(false) // Ensures loading state is stopped regardless of response

            response?.let {
                if (it.status == "success") {
                    showToast("Password reset successful")
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }else{
                    showToast(response.message)
                }
            }
        }
    }

    fun validatePasswords(passwordEt: TextInputEditText, confirmPasswordEt: TextInputEditText): Boolean {
        val password = passwordEt.text.toString().trim()
        val confirmPassword = confirmPasswordEt.text.toString().trim()

        return when {
            password.isEmpty() -> {
                passwordEt.error = "Password cannot be empty"
                false
            }
            confirmPassword.isEmpty() -> {
                confirmPasswordEt.error = "Confirm Password cannot be empty"
                false
            }
            password.length < 6 -> {
                passwordEt.error = "Password must be at least 6 characters"
                false
            }
            password != confirmPassword -> {
                confirmPasswordEt.error = "Passwords do not match"
                false
            }
            else -> true
        }
    }

    private fun isLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressbar.visibility = View.VISIBLE
            binding.saveBtn.visibility = View.GONE
        } else {
            binding.progressbar.visibility = View.GONE
            binding.saveBtn.visibility = View.VISIBLE
        }
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }

}