package com.bypriyan.aaradhyaschoolbusservice.activity

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityOtpBinding
import com.bypriyan.aaradhyaschoolbusservice.model.RegisterRequest
import com.bypriyan.aaradhyaschoolbusservice.viewModel.RegisterViewModel
import com.bypriyan.bustrackingsystem.utility.Constants
import com.bypriyan.bustrackingsystem.utility.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject

@AndroidEntryPoint
class OtpActivity : AppCompatActivity() {

    lateinit var binding: ActivityOtpBinding
    @Inject
    lateinit var preferenceManager: PreferenceManager
    private val registerUserViewModel: RegisterViewModel by viewModels()

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


        binding.continueBtn.setOnClickListener {
            Log.d("fetch", "onCreate: ${binding.firstPinView.text.toString()} + $otp")
            if (binding.firstPinView.text.toString().isNotEmpty()) {
                // Launch a coroutine to call the suspend function
                lifecycleScope.launch {
                    // Check if imageUriString is not null before parsing
                    val compressedImageFile = if (!imageUriString.isNullOrBlank()) {
                        compressImage(this@OtpActivity, Uri.parse(imageUriString))
                    } else {
                        null // Handle the case where no image is selected
                    }

                    // Pass the file path to RegisterRequest
                    val registerRequest = RegisterRequest(
                        fullName = fullName!!,
                        email = email!!,
                        className = className!!,
                        password = password!!,
                        age = age!!,
                        standard = standard!!,
                        year = year!!,
                        fatherName = fatherName!!,
                        fatherNumber = fatherPhone!!,
                        motherName = motherName!!,
                        motherNumber = motherPhone!!,
                        imageUri = compressedImageFile?.absolutePath // Pass the file path as a String
                    )

                    // Call ViewModel to register user
                    registerUserViewModel.registerUser(registerRequest)

                    // Observe the response
                    registerUserViewModel.registerResponse.observe(
                        this@OtpActivity,
                        Observer { response ->
                            Log.d("fetch", "onCreate: $response")
                        })
                }
            } else {
                Toast.makeText(this, "OTP does not match", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun isLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressbar.visibility = View.VISIBLE
            binding.continueBtn.visibility = View.GONE
        } else {
            binding.progressbar.visibility = View.GONE
            binding.continueBtn.visibility = View.VISIBLE
        }
    }

    private fun saveDataToPreferences(
        fullName: String?,
        standard: String?,
        className: String?,
        age: String?,
        year: String?,
        fatherName: String?,
        fatherPhone: String?,
        motherName: String?,
        motherPhone: String?,
        email: String?,
        password: String?
    ) {
        // Save each value to SharedPreferences
        preferenceManager.putString(Constants.KEY_FULL_NAME, fullName)
        preferenceManager.putString(Constants.KEY_STANDARD, standard)
        preferenceManager.putString(Constants.KEY_CLASS, className)
        preferenceManager.putString(Constants.KEY_AGE, age)
        preferenceManager.putString(Constants.KEY_YEAR, year)
        preferenceManager.putString(Constants.KEY_FATHER_NAME, fatherName)
        preferenceManager.putString(Constants.KEY_FATHER_PHONE, fatherPhone)
        preferenceManager.putString(Constants.KEY_MOTHER_NAME, motherName)
        preferenceManager.putString(Constants.KEY_MOTHER_PHONE, motherPhone)
        preferenceManager.putString(Constants.KEY_EMAIL, email)
        preferenceManager.putString(Constants.KEY_PASSWORD, password)
        // Optionally, set a flag to indicate the user is logged in
        preferenceManager.putBoolean(Constants.KEY_IS_LOGGED_IN, true)
    }

    // In OtpActivity
    private suspend fun compressImage(context: Context, uri: Uri): File? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream) ?: return@withContext null

                val compressedFile =
                    File(context.cacheDir, "compressed_${System.currentTimeMillis()}.jpg")
                val outputStream = FileOutputStream(compressedFile)

                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
                outputStream.flush()
                outputStream.close()

                Log.d("Upload", "Image compressed successfully: ${compressedFile.absolutePath}")
                return@withContext compressedFile
            } catch (e: Exception) {
                Log.e("Upload", "Image compression failed: ${e.message}")
                return@withContext null
            }
        }
    }

    private fun compressBitmap(bitmap: Bitmap, quality: Int): Bitmap {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        val byteArray = outputStream.toByteArray()
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}