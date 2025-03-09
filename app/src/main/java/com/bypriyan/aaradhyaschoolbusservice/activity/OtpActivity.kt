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
import com.bypriyan.aaradhyaschoolbusservice.viewModel.RegisterViewModel
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
            if (binding.firstPinView.text.toString().isNotEmpty()) {
                isLoading(true)
                lifecycleScope.launch {
                    try {
                        if (imageUriString.isNullOrEmpty()) {
                            Log.e("OtpActivity", "Image URI is null or empty")
                            showToast("Error: No image selected.")
                            isLoading(false)
                            return@launch
                        }

                        val fileUri = Uri.parse(imageUriString)
                        val compressedFile = compressImage(this@OtpActivity, fileUri)

                        if (compressedFile == null || !compressedFile.exists()) {
                            Log.e("OtpActivity", "Failed to compress image")
                            showToast("Error: Image compression failed.")
                            isLoading(false)
                            return@launch
                        }

                        val filePart = MultipartBody.Part.createFormData(
                            "image",
                            compressedFile.name,
                            compressedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        )

                        val params = mapOf(
                            "full_name" to fullName,
                            "email" to email,
                            "class" to className,
                            "password" to password,
                            "age" to age,
                            "standard" to standard,
                            "year" to year,
                            "father_name" to fatherName,
                            "father_number" to fatherPhone,
                            "mother_name" to motherName,
                            "mother_number" to motherPhone
                        ).mapValues {
                            it.value?.toRequestBody("text/plain".toMediaTypeOrNull())
                        }

                        if (params.values.any { it == null }) {
                            Log.e("OtpActivity", "Missing required registration fields")
                            showToast("Error: Please fill all required fields.")
                            isLoading(false)
                            return@launch
                        }

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
                            params["password"]!!,
                            filePart
                        )
                    } catch (e: Exception) {
                        Log.e("OtpActivity", "Registration failed: ${e.message}")
                        showToast("Error: Registration failed. Try again.")
                        isLoading(false)
                    }
                }
            } else {
                showToast("OTP does not match")
            }
        }

        registerUserViewModel.registerResponse.observe(this) { response ->
            isLoading(false)
            response?.let {
                showToast("Registration Successful")
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } ?: isLoading(false)
        }
    }

    private fun isLoading(isLoading: Boolean) {
        binding.progressbar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.continueBtn.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private suspend fun compressImage(context: Context, uri: Uri): File? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                if (bitmap == null) {
                    Log.e("Upload", "Bitmap decoding failed")
                    return@withContext null
                }

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

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}
