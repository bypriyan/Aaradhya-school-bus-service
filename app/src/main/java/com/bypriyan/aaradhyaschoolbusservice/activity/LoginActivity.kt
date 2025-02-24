package com.bypriyan.aaradhyaschoolbusservice.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityLoginBinding
import com.bypriyan.aaradhyaschoolbusservice.model.LoginUser
import com.bypriyan.aaradhyaschoolbusservice.viewModel.LoginViewModel
import com.bypriyan.bustrackingsystem.utility.Constants
import com.bypriyan.bustrackingsystem.utility.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()
    @Inject
    lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Navigate to SignUpActivity
        binding.signUpBtn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // Handle login button click
        binding.loginBtn.setOnClickListener {
            val email = binding.usernameET.text.toString()
            val password = binding.passwordEt.text.toString()

            if (validateInputFields(email, password)) {
                isLoading(true)
                val loginRequest = LoginUser(email, password)
                loginViewModel.loginUser(loginRequest)
            }
        }

        // Observe the login response using LiveData
        loginViewModel.loginResponse.observe(this) { response ->
            isLoading(false)
            response?.let {
                if (it.status == "success") {
                    // Save token and navigate to the next screen
                    Log.d("login", "onCreate: $it")
                    saveToken(it.token, it.token_type)
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    Log.d("login", "onCreate: ${it.message}")
                }
            }
        }
    }

    private fun validateInputFields(email: String, password: String): Boolean {
        return when {
            email.isBlank() -> {
                binding.username.error = "Email is required"
                false
            }
            password.isBlank() -> {
                binding.password.error = "Password is required"
                false
            }
            else -> true
        }
    }

    private fun isLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressbar.visibility = View.VISIBLE
            binding.loginBtn.visibility = View.GONE
        } else {
            binding.progressbar.visibility = View.GONE
            binding.loginBtn.visibility = View.VISIBLE
        }
    }

    private fun saveToken(token: String?, tokenType: String?) {
        // Save the token to SharedPreferences or another storage mechanism
        token?.let {
            preferenceManager.putString(Constants.KEY_TOKEN, token)
            preferenceManager.putString(Constants.KEY_TOKEN_TYPE, tokenType)
        }
    }
}