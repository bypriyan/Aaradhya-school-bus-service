package com.bypriyan.aaradhyaschoolbusservice.activity

import AlphabeticInputFilter
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivitySignUpBinding
import com.bypriyan.aaradhyaschoolbusservice.viewModel.EmailViewModel
import com.bypriyan.bustrackingsystem.utility.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    private var backPressedTime: Long = 0
    private val backPressThreshold: Long = 2000 // 2 seconds

    private lateinit var binding: ActivitySignUpBinding
    private val emailViewModel: EmailViewModel by viewModels()
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private var selectedImageUri: Uri? = null // Store the selected image URI

    private val classes = arrayOf("Jasmin", "Lilly", "Orchid", "Rose", "IriS", "Tulip", "Lotus")
    private val standards = arrayOf("Nursery", "LKG", "UKG", "1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th", "9th", "10th")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.privacyPolicy.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.termsfeed.com/live/00a3440d-2f50-4d1c-967a-b05ecc56552a"))
            startActivity(intent)
        }


        binding.terms.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.freeprivacypolicy.com/live/344e4044-4efc-4e52-b6d8-beaba19d509b"))
            startActivity(intent)
        }


        binding.back.setOnClickListener { finish() }


        isLoading(false)
        setupUI()
        setupObservers()
        setupImagePicker()
        setupBackPressHandler()
    }

    override fun onResume() {
        super.onResume()
        // Reset isLoading state when the activity is resumed
        isLoading(false)
        binding.sendOTPBtn.visibility=View.VISIBLE
    }

    private fun setupUI() {
        // Set up AutoCompleteTextView for classes and standards
        val classAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, classes)
        val standardAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, standards)

        binding.autoCompleteTextViewClass.setAdapter(classAdapter)
        binding.autoCompleteTextViewstanderd.setAdapter(standardAdapter)

        binding.autoCompleteTextViewClass.setOnClickListener {
            if (binding.autoCompleteTextViewClass.isPopupShowing) {
                binding.autoCompleteTextViewClass.dismissDropDown()
            } else {
                binding.autoCompleteTextViewClass.showDropDown()
            }
        }
        binding.autoCompleteTextViewstanderd.setOnClickListener {
            if (binding.autoCompleteTextViewstanderd.isPopupShowing) {
                binding.autoCompleteTextViewstanderd.dismissDropDown()
            } else {
                binding.autoCompleteTextViewstanderd.showDropDown()
            }
        }

        // Handle image selection
        binding.selectImageCard.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Handle Send OTP button click
        binding.sendOTPBtn.setOnClickListener {
            if (validateInputFields()) {
                binding.sendOTPBtn.visibility = View.GONE
                isLoading(true)
                emailViewModel.sendOtp(binding.emailEt.text.toString())
            }else{Toast.makeText(this,"Something went wrong please check your details", Toast.LENGTH_SHORT).show()}
        }

        // Add TextWatchers for real-time validation
        binding.fullNameEt.addTextChangedListener(createTextWatcher(binding.fullName))
        binding.emailEt.addTextChangedListener(createEmailTextWatcher())
        binding.passwordEt.addTextChangedListener(createTextWatcher(binding.password))
        binding.ageEt.addTextChangedListener(createTextWatcher(binding.age))
        binding.yearEt.addTextChangedListener(createTextWatcher(binding.year))

        // Add TextWatchers for phone number fields
        binding.fPhoneNumEt.addTextChangedListener(createPhoneNumberTextWatcher(binding.fPhoneNum))
        binding.mPhoneEt.addTextChangedListener(createPhoneNumberTextWatcher(binding.mPhoneNumber))
        binding.guardianPhoneEt.addTextChangedListener(createPhoneNumberTextWatcher(binding.guardianNumber))

        // Apply InputFilter to name fields
        binding.fullNameEt.filters = arrayOf(AlphabeticInputFilter())
        binding.fatherNameEt.filters = arrayOf(AlphabeticInputFilter())
        binding.mothersName.filters = arrayOf(AlphabeticInputFilter())
        binding.guardianName.filters = arrayOf(AlphabeticInputFilter())

        // Add TextWatchers for name fields
        binding.fullNameEt.addTextChangedListener(createNameTextWatcher(binding.fullName))
        binding.fatherNameEt.addTextChangedListener(createNameTextWatcher(binding.fatherName))
        binding.mothersName.addTextChangedListener(createNameTextWatcher(binding.motherNAME))
        binding.guardianName.addTextChangedListener(createNameTextWatcher(binding.guardianNAME))
    }


    private fun createNameTextWatcher(textInputLayout: com.google.android.material.textfield.TextInputLayout): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val name = s.toString()
                val regex = Regex("[^A-Za-z ]") // Regex to check for invalid characters
                if (regex.containsMatchIn(name)) {
                    textInputLayout.error = "Numbers and special characters are not allowed"
                } else {
                    textInputLayout.error = null // Clear the error
                }
            }
        }
    }

    private fun createPhoneNumberTextWatcher(textInputLayout: com.google.android.material.textfield.TextInputLayout): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val phoneNumber = s.toString()
                if (phoneNumber.length > 10) {
                    // Truncate the input to 10 digits
                    val truncatedNumber = phoneNumber.substring(0, 10)
                    textInputLayout.editText?.setText(truncatedNumber)
                    textInputLayout.editText?.setSelection(truncatedNumber.length) // Move cursor to the end
                    Toast.makeText(this@SignUpActivity, "Phone number cannot exceed 10 digits", Toast.LENGTH_SHORT).show()
                }
                textInputLayout.error = null // Clear any previous error
            }
        }
    }
    private fun setupObservers() {
        emailViewModel.otpLiveData.observe(this) { result ->
            result.onSuccess { otp ->
                startOtpActivity(otp)
            }.onFailure {
                isLoading(false)
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupImagePicker() {
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                selectedImageUri = it
                displayImage(it)
            }
        }
    }

    private fun setupBackPressHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedTime + backPressThreshold > System.currentTimeMillis()) {
                    finish()
                } else {
                    Toast.makeText(this@SignUpActivity, "Press back again to go Login Screen", Toast.LENGTH_SHORT).show()
                }
                backPressedTime = System.currentTimeMillis()
            }
        })
    }

    private fun validateInputFields(): Boolean {
        return when {
            // Check if an image is selected
            selectedImageUri == null -> {
                Toast.makeText(this, "Please select a profile image", Toast.LENGTH_SHORT).show()
                false
            }
            binding.fullNameEt.text.isNullOrBlank() -> {
                binding.fullName.error = "Full Name is required"
                false
            }
            binding.autoCompleteTextViewstanderd.text.isNullOrBlank() -> {
                binding.autoCompleteTextViewstanderd.error = "Standard is required"
                false
            }
            binding.autoCompleteTextViewClass.text.isNullOrBlank() -> {
                binding.autoCompleteTextViewClass.error = "Class is required"
                false
            }
            binding.ageEt.text.isNullOrBlank() || binding.ageEt.text.toString().toInt() <= 0 ||binding.ageEt.text.toString().toInt()>=100-> {
                binding.age.error = "Invalid age"
                false
            }
            binding.yearEt.text.isNullOrBlank() || !isValidYear(binding.yearEt.text.toString()) -> {
                binding.year.error = "Enter a valid year"
                false
            }
            binding.fPhoneNumEt.text.isNullOrBlank() || binding.fPhoneNumEt.text.toString().length != 10 || !binding.fPhoneNumEt.text.toString().all { it.isDigit() } -> {
                binding.fPhoneNum.error = "Invalid Father's Phone Number"
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
        val intent = Intent(this, OtpActivity::class.java).apply {
            putExtra(Constants.KEY_FULL_NAME, binding.fullNameEt.text.toString())
            putExtra(Constants.KEY_STANDARD, binding.autoCompleteTextViewstanderd.text.toString())
            putExtra(Constants.KEY_CLASS, binding.autoCompleteTextViewClass.text.toString())
            putExtra(Constants.KEY_AGE, binding.ageEt.text.toString())
            putExtra(Constants.KEY_YEAR, binding.yearEt.text.toString())
            putExtra(Constants.KEY_FATHER_NAME, binding.fatherNameEt.text.toString())
            putExtra(Constants.KEY_FATHER_PHONE, binding.fPhoneNumEt.text.toString())
            putExtra(Constants.KEY_MOTHER_NAME, binding.mothersName.text.toString())
            putExtra(Constants.KEY_MOTHER_PHONE, binding.mPhoneEt.text.toString())
            putExtra(Constants.KEY_EMAIL, binding.emailEt.text.toString())
            putExtra(Constants.KEY_PASSWORD, binding.passwordEt.text.toString())
            putExtra(Constants.KEY_OTP, otp)
            selectedImageUri?.let { putExtra(Constants.KEY_PROFILE_IMAGE_URI, it.toString()) }
        }
        startActivity(intent)
    }

    private fun isLoading(isLoading: Boolean) {
        binding.progressbar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.sendOTPBtn.isEnabled = !isLoading
    }

    private fun displayImage(imageUri: Uri) {
        try {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, imageUri))
            } else {
                BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
            }
            binding.profileImage.visibility = View.VISIBLE
            binding.galleryIcon.visibility = View.GONE
            binding.profileImage.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isValidYear(year: String): Boolean {
        return try {
            val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
            val enteredYear = year.toInt()
            enteredYear in 1900..currentYear // Ensures year is within a valid range
        } catch (e: NumberFormatException) {
            false
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

    private fun createEmailTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val email = s.toString()
                if (email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    binding.email.error = "Invalid email address"
                } else {
                    binding.email.error = null
                }
            }
        }
    }
}