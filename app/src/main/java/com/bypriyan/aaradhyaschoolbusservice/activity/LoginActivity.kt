package com.bypriyan.aaradhyaschoolbusservice.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityLoginBinding
import com.bypriyan.aaradhyaschoolbusservice.model.LoginUser
import com.bypriyan.aaradhyaschoolbusservice.viewModel.LoginViewModel
import com.bypriyan.aaradhyaschoolbusservice.viewModel.PdfViewModel
import com.bypriyan.bustrackingsystem.utility.Constants
import com.bypriyan.bustrackingsystem.utility.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()
    @Inject
    lateinit var preferenceManager: PreferenceManager
    private val pdfViewModel: PdfViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Navigate to SignUpActivity
        binding.signUpBtn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.usernameET.setText("czstar81094@gmail.com")
        binding.passwordEt.setText("123456")

        // Handle login button click
        binding.loginBtn.setOnClickListener {
            val email = binding.usernameET.text.toString()
            val password = binding.passwordEt.text.toString()

            if (validateInputFields(email, password)) {
                isLoading(true)
                loginViewModel.loginUser(email,password)
            }
        }

        // Observe the login response using LiveData
        loginViewModel.loginResponse.observe(this) { response ->
            isLoading(false)
            response?.let {
                if (it.status == "success") {
                    // Save token and navigate to the next screen
                    Log.d("loginss", "onCreate: $it")
                 //   saveToken(it.token, it.token_type, it.id)

                    val paymentStatus = preferenceManager.getString(Constants.PAYMENT_STATUS)?.toBoolean() ?: false
                    if (paymentStatus) {
                        // If the user has made a payment, redirect to DashboardActivity
                        val dashboardIntent = Intent(this@LoginActivity, DashBoard1Activity::class.java)
                        Log.d("lls", "onCreate: $it")
////                        intent.putExtra(Constants.KEY_TOKEN, it.token)
//                        intent.putExtra(Constants.KEY_TOKEN_TYPE, it.token_type)
                        intent.putExtra(Constants.KEY_USER_ID, it.id)
                        preferenceManager.putString(Constants.KEY_USER_ID, it.id)
//                        preferenceManager.putString(Constants.KEY_TOKEN, it.token)
//                        preferenceManager.putString(Constants.KEY_TOKEN_TYPE, it.token_type)
                        startActivity(dashboardIntent)
                        finish()
                    } else {
                        // Otherwise, go to CheckOut1 (payment screen)
                        var intent = Intent(this@LoginActivity, CheckOut1::class.java)
//                        intent.putExtra(Constants.KEY_TOKEN, it.token)
//                        intent.putExtra(Constants.KEY_TOKEN_TYPE, it.token_type)
                        intent.putExtra(Constants.KEY_USER_ID, it.id.toString())
                        preferenceManager.putString(Constants.KEY_USER_ID, it.id.toString())
//                        preferenceManager.putString(Constants.KEY_TOKEN, it.token)
//                        preferenceManager.putString(Constants.KEY_TOKEN_TYPE, it.token_type)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Log.d("login", "onCreate: ${it.message}")
                }
            }
        }

        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
        }

    }

    private fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (!isGranted) {
            // Handle case where user denies permission (show a message if needed)
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

    private fun saveToken(token: String?, tokenType: String?, userId: String?) {
        token?.let {
            preferenceManager.putString(Constants.KEY_TOKEN, token)
            preferenceManager.putString(Constants.KEY_TOKEN_TYPE, tokenType)
            preferenceManager.putString(Constants.KEY_USER_ID, userId)
        }
    }


}


