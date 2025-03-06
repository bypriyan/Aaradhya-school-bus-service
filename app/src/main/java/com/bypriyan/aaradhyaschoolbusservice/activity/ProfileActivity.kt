package com.bypriyan.aaradhyaschoolbusservice.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityProfileBinding
import com.bypriyan.bustrackingsystem.utility.Constants
import com.bypriyan.bustrackingsystem.utility.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    @Inject
    lateinit var preferenceManager: PreferenceManager
    private lateinit var fullName: String
    private lateinit var email: String
    private lateinit var userClass: String
    private lateinit var age: String
    private lateinit var standard: String
    private lateinit var year: String
    private lateinit var image: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve data from Intent safely
        preferenceManager.apply {
             fullName = getString(Constants.KEY_FULL_NAME).toString()
             email = getString(Constants.KEY_EMAIL).toString()
             userClass = getString(Constants.KEY_USER_CLASS).toString()
             age = getString(Constants.KEY_AGE).toString()
             standard = getString(Constants.KEY_STANDARD).toString()
             year = getString(Constants.KEY_YEAR).toString()
             image = Constants.KEY_IMAGE_PATH+getString(Constants.KEY_IMAGE)

        }

        val fatherName = intent.getStringExtra(Constants.KEY_FATHER_NAME) ?: "N/A"
        val fatherNumber = intent.getStringExtra(Constants.KEY_FATHER_NUMBER) ?: "N/A"
        val motherName = intent.getStringExtra(Constants.KEY_MOTHER_NAME) ?: "N/A"
        val motherNumber = intent.getStringExtra(Constants.KEY_MOTHER_NUMBER) ?: "N/A"

        // Bind data to views
        binding.fullNameEt.setText(fullName)
        binding.standerdEt.setText(standard)
        binding.classEt.setText(userClass)
        binding.ageEt.setText(age)
        binding.yearEt.setText(year)
        binding.fatherNameEt.setText(fatherName)
        binding.mothersName.setText(motherName)
        binding.mPhoneEt.setText(motherNumber)
        binding.emailEt.setText(email)

        // Load image using Glide
        loadImageWithGlide(image)
    }

    private fun loadImageWithGlide(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl) // Load the image URL
            .into(binding.profileImage) // Set the image to the ImageView
    }
}
