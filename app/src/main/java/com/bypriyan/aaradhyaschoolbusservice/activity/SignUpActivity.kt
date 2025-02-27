package com.bypriyan.aaradhyaschoolbusservice.activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivitySignUpBinding
import com.bypriyan.aaradhyaschoolbusservice.viewModel.OTPViewModel
import com.bypriyan.bustrackingsystem.utility.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySignUpBinding
    //viewModel
    private val otpViewModel: OTPViewModel by viewModels()
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private var selectedImageUri: Uri? = null // Store the selected image URI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)



        //selected image
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                selectedImageUri = it // Store the URI for later use
                displayImage(it)
            }
        }


        binding.sendOTPBtn.setOnClickListener {
            if(validateInputFields()){
                isLoading(true)
                otpViewModel.sendOtp(binding.emailEt.text.toString())
            }
        }

        binding.selectImageCard.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }


        otpViewModel.otpResponse.observe(this, Observer { result ->
            result?.let {
                it.onSuccess { response ->
                    startOtpActivity(response.otp.toString())
                    isLoading(false)
                }.onFailure { error ->
                    isLoading(false)
                    Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                }
            }
        })

        setInputFieldData()

    }

    private fun setInputFieldData() {
        // Set full name
        binding.fullNameEt.setText("John Doe")

        // Set standard
        binding.standerdEt.setText("10th")

        // Set class
        binding.classEt.setText("A")

        // Set age
        binding.ageEt.setText("16")

        // Set year
        binding.yearEt.setText("2023")

        // Set father's name
        binding.fatherNameEt.setText("John Doe Sr.")

        // Set father's phone number
        binding.fPhoneNumEt.setText("1234567890")

        // Set mother's name
        binding.mothersName.setText("Jane Doe")

        // Set mother's phone number
        binding.mPhoneEt.setText("0987654321")

        // Set email
        binding.emailEt.setText("104abcdabcd104@gmail.com")

        // Set password
        binding.passwordEt.setText("123456")
    }

    private fun validateInputFields(): Boolean {
        return when {
            binding.fullNameEt.text.isNullOrBlank() -> {
                binding.fullName.error = "Full Name is required"
                false
            }
            binding.standerdEt.text.isNullOrBlank() -> {
                binding.standerdTi.error = "Standard is required"
                false
            }
            binding.classEt.text.isNullOrBlank() -> {
                binding.classs.error = "Class is required"
                false
            }
            binding.ageEt.text.isNullOrBlank() || binding.ageEt.text.toString().toInt() <= 0 -> {
                binding.age.error = "Invalid age"
                false
            }
            binding.yearEt.text.isNullOrBlank() -> {
                binding.year.error = "Year is required"
                false
            }
            binding.fatherNameEt.text.isNullOrBlank() -> {
                binding.fatherName.error = "Father's Name is required"
                false
            }
            binding.fPhoneNumEt.text.isNullOrBlank() || binding.fPhoneNumEt.text.toString().length != 10 || !binding.fPhoneNumEt.text.toString().all { it.isDigit() } -> {
                binding.fPhoneNum.error = "Invalid Father's Phone Number"
                false
            }
            binding.mothersName.text.isNullOrBlank() -> {
                binding.motherNAME.error = "Mother's Name is required"
                false
            }
            binding.mPhoneEt.text.isNullOrBlank() || binding.mPhoneEt.text.toString().length != 10 || !binding.mPhoneEt.text.toString().all { it.isDigit() } -> {
                binding.mPhoneNumber.error = "Invalid Mother's Phone Number"
                false
            }
            binding.emailEt.text.isNullOrBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(binding.emailEt.text.toString()).matches() -> {
                binding.email.error = "Invalid email address"
                false
            }
            binding.passwordEt.text.isNullOrBlank() || binding.passwordEt.text.toString().length < 6 -> {
                binding.password.error = "Password must be at least 6 characters"
                false
            }
            else -> true
        }

    }

    private fun startOtpActivity(otp: String) {

        val fullName = binding.fullNameEt.text.toString()
        val standard = binding.standerdEt.text.toString()
        val className = binding.classEt.text.toString()
        val age = binding.ageEt.text.toString()
        val year = binding.yearEt.text.toString()
        val fatherName = binding.fatherNameEt.text.toString()
        val fatherPhone = binding.fPhoneNumEt.text.toString()
        val motherName = binding.mothersName.text.toString()
        val motherPhone = binding.mPhoneEt.text.toString()
        val email = binding.emailEt.text.toString()
        val password = binding.passwordEt.text.toString()

        val intent = Intent(this, OtpActivity::class.java)
        intent.putExtra(Constants.KEY_FULL_NAME, fullName)
        intent.putExtra(Constants.KEY_STANDARD, standard)
        intent.putExtra(Constants.KEY_CLASS, className)
        intent.putExtra(Constants.KEY_AGE, age)
        intent.putExtra(Constants.KEY_YEAR, year)
        intent.putExtra(Constants.KEY_FATHER_NAME, fatherName)
        intent.putExtra(Constants.KEY_FATHER_PHONE, fatherPhone)
        intent.putExtra(Constants.KEY_MOTHER_NAME, motherName)
        intent.putExtra(Constants.KEY_MOTHER_PHONE, motherPhone)
        intent.putExtra(Constants.KEY_EMAIL, email)
        intent.putExtra(Constants.KEY_PASSWORD, password)
        intent.putExtra(Constants.KEY_OTP,otp)
        // Pass the selected image URI
        selectedImageUri?.let {
            intent.putExtra(Constants.KEY_PROFILE_IMAGE_URI, it.toString())
        }
        // Start the next activity
        startActivity(intent)
    }


    fun isLoading(isLoading: Boolean){
        if (isLoading){
            binding.progressbar.visibility = View.VISIBLE
            binding.sendOTPBtn.visibility = View.GONE
        }else{
            binding.progressbar.visibility = View.GONE
            binding.sendOTPBtn.visibility = View.VISIBLE
        }
    }

    private fun displayImage(imageUri: Uri) {
        try {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(contentResolver, imageUri)
                ImageDecoder.decodeBitmap(source)
            } else {
                contentResolver.openInputStream(imageUri)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                }
            }
            binding.profileImage.visibility = View.VISIBLE
            binding.galleryIcon.visibility = View.GONE
            binding.profileImage.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun startStudentDetailsActivity() {
        val fullName = binding.fullNameEt.text.toString()
        val standard = binding.standerdEt.text.toString()
        val className = binding.classEt.text.toString()
        val age = binding.ageEt.text.toString()
        val year = binding.yearEt.text.toString()
        val fatherName = binding.fatherNameEt.text.toString()
        val fatherPhone = binding.fPhoneNumEt.text.toString()
        val motherName = binding.mothersName.text.toString()
        val motherPhone = binding.mPhoneEt.text.toString()
        val email = binding.emailEt.text.toString()
        val profileImageUri = selectedImageUri?.toString()

        // Debugging: Print values in Logcat
        android.util.Log.d("SignUpActivity", "Full Name: $fullName")
        android.util.Log.d("SignUpActivity", "Email: $email")
        android.util.Log.d("SignUpActivity", "Profile Image URI: $profileImageUri")

        val intent = Intent(this, StudentDetailActivity::class.java)
        intent.putExtra(Constants.KEY_FULL_NAME, fullName)
        intent.putExtra(Constants.KEY_STANDARD, standard)
        intent.putExtra(Constants.KEY_CLASS, className)
        intent.putExtra(Constants.KEY_AGE, age)
        intent.putExtra(Constants.KEY_YEAR, year)
        intent.putExtra(Constants.KEY_FATHER_NAME, fatherName)
        intent.putExtra(Constants.KEY_FATHER_PHONE, fatherPhone)
        intent.putExtra(Constants.KEY_MOTHER_NAME, motherName)
        intent.putExtra(Constants.KEY_MOTHER_PHONE, motherPhone)
        intent.putExtra(Constants.KEY_EMAIL, email)
        profileImageUri?.let {
            intent.putExtra(Constants.KEY_PROFILE_IMAGE_URI, it)
        }

        startActivity(intent)
    }


}