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
import com.bypriyan.aaradhyaschoolbusservice.R
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityLoginBinding
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityOtpBinding
import com.bypriyan.aaradhyaschoolbusservice.model.LoginRequest
import com.bypriyan.aaradhyaschoolbusservice.viewModel.RegisterUserViewModel
import com.bypriyan.bustrackingsystem.utility.Constants
import com.bypriyan.bustrackingsystem.utility.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.inject.Inject

@AndroidEntryPoint
class OtpActivity : AppCompatActivity() {

    lateinit var binding: ActivityOtpBinding
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

            binding.continueBtn.setOnClickListener {
                if (binding.firstPinView.text.toString().isNotEmpty() &&
                    binding.firstPinView.text.toString() == otp
                ) {
                    // Compress and encode the image
                    val imageUri = Uri.parse(imageUriString)
                    val compressedImage = compressImage(imageUri, this)

                    // Create LoginRequest
                    val loginRequest = LoginRequest(
                        full_name = fullName!!,
                        email = email!!,
                        class_ = className!!,
                        image = compressedImage,
                        password = password!!,
                        age = age!!,
                        standard = standard!!,
                        year = year!!,
                        father_name = fatherName!!,
                        father_number = fatherPhone!!,
                        mother_name = motherName!!,
                        mother_number = motherPhone!!
                    )

                    // Call ViewModel to register user
                    registerUserViewModel.registerUser(loginRequest)

                    // Observe the response
                    registerUserViewModel.registerResponse.observe(this, Observer { result ->
                        result?.let {
                            it.onSuccess { response ->
                                isLoading(false)
                                Log.d("otp", "onCreate: $response")

                            }.onFailure { error ->
                                isLoading(false)
                                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                }else{
                    Log.d("otp", "onCreate: not match")
                }
            }


        }// end on create

    fun isLoading(isLoading: Boolean){
        if (isLoading){
            binding.progressbar.visibility = View.VISIBLE
            binding.continueBtn.visibility = View.GONE
        }else{
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

    fun compressImage(uri: Uri, context: Context): String {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val compressedBitmap = compressBitmap(bitmap, 50) // Compress to 50%
        return encodeImage(compressedBitmap)
    }

    private fun compressBitmap(bitmap: Bitmap, quality: Int): Bitmap {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        val byteArray = outputStream.toByteArray()
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    private fun encodeImage(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }


}