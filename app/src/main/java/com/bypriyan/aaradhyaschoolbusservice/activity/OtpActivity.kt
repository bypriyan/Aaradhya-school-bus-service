package com.bypriyan.aaradhyaschoolbusservice.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import coil3.toUri
import com.bypriyan.aaradhyaschoolbusservice.R
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityLoginBinding
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityOtpBinding
import com.bypriyan.aaradhyaschoolbusservice.model.RegisterRequest
import com.bypriyan.aaradhyaschoolbusservice.repo.RegisterUserRepository
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
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject
import kotlin.text.category

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
                isLoading(true)
                lifecycleScope.launch {
                    // Check if imageUriString is not null before parsing
                    val fileUri = Uri.parse(imageUriString)
                    val compressedFile = compressImage(this@OtpActivity, fileUri)

                    val filePart = compressedFile?.let { file ->
                        MultipartBody.Part.createFormData(
                            "fileToUpload",
                            file.name,
                            file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        )
                    }
                    Log.d("fetch", "onCreate: $filePart")

                    val params = mapOf(
                        "fullName" to fullName,
                        "email" to email,
                        "className" to className,
                        "password" to password,
                        "age" to age,
                        "standard" to standard,
                        "year" to year,
                        "fatherName" to fatherName,
                        "fatherNumber" to fatherPhone,
                        "motherName" to motherName,
                        "motherNumber" to motherPhone
                    ).mapValues { it.value?.toRequestBody("text/plain".toMediaTypeOrNull()) }

                    // Ensure that all RequestBody values are not null
                    val fullNameBody = params["fullName"]!!
                    val emailBody = params["email"]!!
                    val classNameBody = params["className"]!!
                    val passwordBody = params["password"]!!
                    val ageBody = params["age"]!!
                    val standardBody = params["standard"]!!
                    val yearBody = params["year"]!!
                    val fatherNameBody = params["fatherName"]!!
                    val fatherNumberBody = params["fatherNumber"]!!
                    val motherNameBody = params["motherName"]!!
                    val motherNumberBody = params["motherNumber"]!!

                    registerUserViewModel.registerUser(
                        fullNameBody, emailBody, classNameBody, passwordBody,
                        ageBody, standardBody, yearBody,
                        fatherNameBody, fatherNumberBody, motherNameBody,
                        motherNumberBody, filePart
                    )
                }
                // Observe the response

            } else {
                Toast.makeText(this, "OTP does not match", Toast.LENGTH_SHORT).show()
            }
        }

        registerUserViewModel.registerResponse.observe(this, Observer { response ->
            Log.d("fetch", "onCreate: $response")
        })
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

                val compressedFile = File(context.cacheDir, "compressed_${System.currentTimeMillis()}.jpg")
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

}