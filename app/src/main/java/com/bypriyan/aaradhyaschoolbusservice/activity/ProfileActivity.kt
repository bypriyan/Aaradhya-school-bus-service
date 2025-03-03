package com.bypriyan.aaradhyaschoolbusservice.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bypriyan.aaradhyaschoolbusservice.R
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityCheckOutBinding
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityProfileBinding
import com.bypriyan.bustrackingsystem.utility.Constants
import com.bypriyan.bustrackingsystem.utility.PreferenceManager
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    @Inject
    lateinit var preferenceManager: PreferenceManager
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Retrieve data from SharedPreferences
        val fullName = preferenceManager.getString(Constants.KEY_FULL_NAME)
        val email = preferenceManager.getString(Constants.KEY_EMAIL)
        val userClass = preferenceManager.getString(Constants.KEY_USER_CLASS)
        val image = preferenceManager.getString(Constants.KEY_IMAGE)
        val year = preferenceManager.getString(Constants.KEY_YEAR)
        val fatherName = preferenceManager.getString(Constants.KEY_FATHER_NAME)
        val fatherNumber = preferenceManager.getString(Constants.KEY_FATHER_NUMBER)
        val motherName = preferenceManager.getString(Constants.KEY_MOTHER_NAME)
        val motherNumber = preferenceManager.getString(Constants.KEY_MOTHER_NUMBER)
        val age = preferenceManager.getString(Constants.KEY_AGE)
        val standard = preferenceManager.getString(Constants.KEY_STANDARD)

        loadImageWithGlide(Constants.KEY_IMAGE_PATH+image)

        // Bind data to views
        binding.fullNameEt.setText(fullName)
        binding.standerdEt.setText(standard)
        binding.classEt.setText(userClass)
        binding.ageEt.setText(age)
        binding.yearEt.setText(year)
        binding.fatherNameEt.setText(fatherName)
        binding.fatherNameEt.setText(fatherNumber)
        binding.mothersName.setText(motherName)
        binding.mPhoneEt.setText(motherNumber)
        binding.emailEt.setText(email)


    }

    private fun loadImageWithGlide(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl) // Load the image URL
            .into(binding.profileImage) // Set the image to the ImageView
    }

}