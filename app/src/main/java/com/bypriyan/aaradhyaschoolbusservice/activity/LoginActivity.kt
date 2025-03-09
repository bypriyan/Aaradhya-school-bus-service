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

        loginViewModel.loginResponse.observe(this) { response ->
            isLoading(false)
            response?.let {
                if (it.status == "success") {
                    preferenceManager.putString(Constants.KEY_USER_ID,it.id.toString())
                    var intent = Intent(this, CheckOut1::class.java)
                } else {
                    Log.d("login", "onCreate: ${it.message}")
                }
            }
        }


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
}


