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
import com.bypriyan.aaradhyaschoolbusservice.viewModel.LoginViewModel
import com.bypriyan.bustrackingsystem.utility.Constants
import com.bypriyan.bustrackingsystem.utility.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.bypriyan.aaradhyaschoolbusservice.activity.forgot.ForgotEmailActivity
import android.text.Editable
import android.text.TextWatcher

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private var backPressedTime: Long = 0
    private val backPressThreshold: Long = 2000 // 2 seconds

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
                loginViewModel.loginUser(email, password)
            }
        }

        // Add TextWatchers for email and password fields
        binding.usernameET.addTextChangedListener(createTextWatcher(binding.username))
        binding.passwordEt.addTextChangedListener(createTextWatcher(binding.password))

        loginViewModel.loginResponse.observe(this) { response ->
            isLoading(false)
            Log.d("logrepo", "onCreate: $response")
            response?.let {
                if (it.status == "success") {
                    preferenceManager.putString(Constants.KEY_USER_ID, it.id.toString())
                    preferenceManager.putBoolean(Constants.KEY_IS_LOGGED_IN, true)
                    it.reservations?.let { reservations ->
                        if (reservations.isNotEmpty()) {
                            preferenceManager.putBoolean(Constants.PAYMENT_STATUS, false)
                            preferenceManager.apply {
                                putString(Constants.KEY_RESERVATION_ID, reservations[0].reservationId.toString())
                                putString(Constants.KEY_CREATED_AT, reservations[0].createdAt)
                                putString(Constants.KEY_AMOUNT_PAID, reservations[0].amountPaid)
                                putString(Constants.KEY_PLAN, reservations[0].plan)
                                putString(Constants.KEY_INSTALLMENT_PAID, reservations[0].installmentPaid)
                                putString(Constants.KEY_TOTAL_AMOUNT, reservations[0].totalAmount)
                                putString(Constants.KEY_PICKUP_LOCATION, reservations[0].pickupLocation)
                                putString(Constants.KEY_PICKUP_LATITUDE, reservations[0].pickupLatitude)
                                putString(Constants.KEY_PICKUP_LONGITUDE, reservations[0].pickupLongitude)
                                putString(Constants.KEY_DROP_LOCATION, reservations[0].dropLocation)
                                putString(Constants.KEY_DROP_LATITUDE, reservations[0].dropLatitude)
                                putString(Constants.KEY_DROP_LONGITUDE, reservations[0].dropLongitude)
                            }
                        }
                    }
                    if (it.reservations.isNullOrEmpty()) {
                        val intent = Intent(this, CheckOut1::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        preferenceManager.putBoolean(Constants.PAYMENT_STATUS, true)
                        val intent = Intent(this, DashBoard1Activity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
        }

        binding.forgotPass.setOnClickListener {
            startActivity(Intent(this, ForgotEmailActivity::class.java))
        }
    }

    override fun onBackPressed() {
        if (backPressedTime + backPressThreshold > System.currentTimeMillis()) {
            super.onBackPressed()
            finish()
        } else {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()
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
        binding.username.error = null
        binding.password.error = null

        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        return when {
            trimmedEmail.isEmpty() -> {
                binding.username.error = "Email is required"
                binding.username.requestFocus()
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches() -> {
                binding.username.error = "Enter a valid email"
                binding.username.requestFocus()
                false
            }
            trimmedPassword.isEmpty() -> {
                binding.password.error = "Password is required"
                binding.password.requestFocus()
                false
            }
            trimmedPassword.length < 6 -> {
                binding.password.error = "Password must be at least 6 characters"
                binding.password.requestFocus()
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

    private fun createTextWatcher(textInputLayout: com.google.android.material.textfield.TextInputLayout): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                textInputLayout.error = null // Clear error when user starts typing
            }
        }
    }
}