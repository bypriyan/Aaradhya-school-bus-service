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
            if (binding.firstPinView.text.toString().isNotEmpty()) {
                isLoading(true)
                lifecycleScope.launch {
                    try {
                        val fileUri = Uri.parse(imageUriString)
                        val compressedFile = compressImage(this@OtpActivity, fileUri)

                        val filePart =
                            compressedFile?.asRequestBody("image/jpeg".toMediaTypeOrNull())?.let { body ->
                                MultipartBody.Part.createFormData(
                                    "image", // Ensure this matches the server's expected field name
                                    compressedFile.name,
                                    body
                                )
                            }

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
                        ).mapValues { it.value?.toRequestBody("text/plain".toMediaTypeOrNull()) }

                        if (filePart != null) {
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
                        }
                    } catch (e: Exception) {
                        Log.e("OtpActivity", "Registration failed: ${e.message}")
                        isLoading(false)
                    }
                }
            } else {
                Toast.makeText(this, "OTP does not match", Toast.LENGTH_SHORT).show()
            }
        }

        registerUserViewModel.registerResponse.observe(this, Observer { response ->
            isLoading(false)
            response?.let {
                Toast.makeText(this, "Registration Successful", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun isLoading(isLoading: Boolean) {
        binding.progressbar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.continueBtn.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

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
}
