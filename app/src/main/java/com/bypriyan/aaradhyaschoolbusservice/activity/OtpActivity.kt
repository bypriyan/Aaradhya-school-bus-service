package com.bypriyan.aaradhyaschoolbusservice.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityOtpBinding
import com.bypriyan.aaradhyaschoolbusservice.viewModel.RegisterUserViewModel
import com.bypriyan.bustrackingsystem.utility.Constants
import com.bypriyan.bustrackingsystem.utility.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject
@AndroidEntryPoint
class OtpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpBinding

    @Inject
    lateinit var preferenceManager: PreferenceManager
    private val registerUserViewModel: RegisterUserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve data from Intent
        val fullName = intent.getStringExtra(Constants.KEY_FULL_NAME)
        val standard = intent.getStringExtra(Constants.KEY_STANDARD)
        val className = intent.getStringExtra(Constants.KEY_CLASS)
        val age = intent.getStringExtra(Constants.KEY_AGE)
        val year = intent.getStringExtra(Constants.KEY_YEAR)
        val fatherName = intent.getStringExtra(Constants.KEY_FATHER_NAME)
        val fatherPhone = intent.getStringExtra(Constants.KEY_FATHER_PHONE)
        val motherName = intent.getStringExtra(Constants.KEY_MOTHER_NAME)
        val motherPhone = intent.getStringExtra(Constants.KEY_MOTHER_PHONE)
        val email = intent.getStringExtra(Constants.KEY_EMAIL)
        val password = intent.getStringExtra(Constants.KEY_PASSWORD)
        val otp = intent.getStringExtra(Constants.KEY_OTP)
        val imageUriString = intent.getStringExtra(Constants.KEY_PROFILE_IMAGE_URI)
        val guardianName = intent.getStringExtra(Constants.KEY_GUARDIAN_NAME)
        val guardianPhone = intent.getStringExtra(Constants.KEY_GUARDIAN_PHONE)

        binding.continueBtn.setOnClickListener {
            val enteredOtp = binding.firstPinView.text.toString()

            // Validate OTP first
            when {
                enteredOtp.isEmpty() -> {
                    showToast("Please enter the OTP")
                    return@setOnClickListener
                }
                enteredOtp != otp -> {
                    showToast("OTP does not match")
                    return@setOnClickListener
                }
                else -> {
                    // OTP is valid, proceed with registration
                    isLoading(true)
                    lifecycleScope.launch {
                        try {
                            if (imageUriString.isNullOrEmpty()) {
                                showToast("Error: No image selected")
                                isLoading(false)
                                return@launch
                            }

                            val fileUri = Uri.parse(imageUriString)
                            val compressedFile = compressImage(this@OtpActivity, fileUri)
                                ?: run {
                                    showToast("Error: Image compression failed")
                                    isLoading(false)
                                    return@launch
                                }

                            // Create multipart file
                            val filePart = MultipartBody.Part.createFormData(
                                "image",
                                compressedFile.name,
                                compressedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                            )

                            // Prepare all required fields
                            val requiredFields = listOf(
                                fullName, standard, className, age, year,
                                fatherName, fatherPhone, motherName, motherPhone,
                                email, password, guardianName, guardianPhone
                            )

                            if (requiredFields.any { it.isNullOrEmpty() }) {
                                showToast("Error: Please fill all required fields")
                                isLoading(false)
                                return@launch
                            }

                            // Create request params
                            val params = mapOf(
                                "full_name" to fullName!!,
                                "email" to email!!,
                                "class" to className!!,
                                "password" to password!!,
                                "age" to age!!,
                                "standard" to standard!!,
                                "year" to year!!,
                                "father_name" to fatherName!!,
                                "father_number" to fatherPhone!!,
                                "mother_name" to motherName!!,
                                "mother_number" to motherPhone!!,
                                "guardian_name" to guardianName!!,
                                "guardian_number" to guardianPhone!!
                            ).mapValues { it.value.toRequestBody("text/plain".toMediaTypeOrNull()) }

                            // Make registration request
                            registerUserViewModel.registerUser(
                                params["full_name"]!!,
                                params["email"]!!,
                                params["class"]!!,
                                params["age"]!!,
                                params["standard"]!!,
                                params["year"]!!,
                                params["father_name"]!!,
                                params["father_number"]!!,
                                params["mother_name"]!!,
                                params["mother_number"]!!,
                                params["guardian_name"]!!,
                                params["guardian_number"]!!,
                                params["password"]!!,
                                filePart
                            )
                        } catch (e: Exception) {
                            Log.e("OtpActivity", "Registration failed", e)
                            showToast("Error: Registration failed. Try again.")
                            isLoading(false)
                        }
                    }
                }
            }
        }

        registerUserViewModel.responseMessage.observe(this) { response ->
            isLoading(false)
            response?.let {
                if (it.status == "success") {
                    showToast("Registration Successful")
                    startActivity(Intent(this, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                    finish()
                } else {
                    showToast(it.message ?: "Registration failed")
                }
            }
        }
    }

    private fun isLoading(isLoading: Boolean) {
        binding.progressbar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.continueBtn.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private suspend fun compressImage(context: Context, uri: Uri): File? = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)?.let { bitmap ->
                    File(context.cacheDir, "compressed_${System.currentTimeMillis()}.jpg").apply {
                        FileOutputStream(this).use { outputStream ->
                            if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)) {
                                return@withContext null
                            }
                        }
                        return@withContext this
                    }
                }
            }
            null
        } catch (e: Exception) {
            Log.e("OtpActivity", "Image compression failed", e)
            null
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}