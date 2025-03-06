package com.bypriyan.aaradhyaschoolbusservice.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityProfileBinding
import com.bypriyan.bustrackingsystem.utility.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve data from Intent safely
        val fullName = intent.getStringExtra(Constants.KEY_FULL_NAME) ?: "N/A"
        val email = intent.getStringExtra(Constants.KEY_EMAIL) ?: "N/A"
        val userClass = intent.getStringExtra(Constants.KEY_USER_CLASS) ?: "N/A"
        val age = intent.getStringExtra(Constants.KEY_AGE) ?: "N/A"
        val standard = intent.getStringExtra(Constants.KEY_STANDARD) ?: "N/A"
        val year = intent.getStringExtra(Constants.KEY_YEAR) ?: "N/A"
        val image = intent.getStringExtra(Constants.KEY_IMAGE) ?: ""
        val createdAt = intent.getStringExtra(Constants.KEY_CREATED_AT) ?: "N/A"

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
        loadImageWithGlide(Constants.KEY_IMAGE_PATH + image)
    }

    private fun loadImageWithGlide(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl) // Load the image URL
            .into(binding.profileImage) // Set the image to the ImageView
    }
}
