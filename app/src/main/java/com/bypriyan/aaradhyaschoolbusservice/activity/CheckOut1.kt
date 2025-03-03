package com.bypriyan.aaradhyaschoolbusservice.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityDasboardBinding
import com.bypriyan.aaradhyaschoolbusservice.viewModel.UserViewModel
import com.bypriyan.bustrackingsystem.utility.Constants
import com.bypriyan.bustrackingsystem.utility.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CheckOut1 : AppCompatActivity() {

    lateinit var  binding: ActivityDasboardBinding
    lateinit var userId: String
    lateinit var token: String
    lateinit var token_type: String
    private val userViewModel: UserViewModel by viewModels()
    @Inject
    lateinit var preferenceManager: PreferenceManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDasboardBinding.inflate(layoutInflater)
        setContentView(binding.root)


        userId = getIntent().getStringExtra(Constants.KEY_USER_ID).toString()
        token = getIntent().getStringExtra(Constants.KEY_TOKEN).toString()
        token_type = getIntent().getStringExtra(Constants.KEY_TOKEN_TYPE).toString()

        preferenceManager.putString(Constants.KEY_USER_ID, userId)
        preferenceManager.putString(Constants.KEY_TOKEN, token)
        preferenceManager.putString(Constants.KEY_TOKEN_TYPE, token_type)

        Log.d("dash", "onCreate: $userId, $token, $token_type")
        userViewModel.getUserDetails(userId, token)

        binding.profileImage.setOnClickListener(){
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        userViewModel.userDetails.observe(this) { userDetails ->
            // Update UI with user details
            Log.d("checks", "onCreate: $userDetails")
            binding.name.text = "Hi, ${userDetails.fullName}"
            loadImageWithGlide(Constants.KEY_IMAGE_PATH+userDetails.image)
            preferenceManager.putString(Constants.KEY_STANDARD, userDetails.standard)
            preferenceManager.putString(Constants.KEY_FULL_NAME, userDetails.fullName)


            // Save user details in SharedPreferences
            preferenceManager.putString(Constants.KEY_USER_ID, userDetails.id.toString())
            preferenceManager.putString(Constants.KEY_EMAIL, userDetails.email)
            preferenceManager.putString(Constants.KEY_USER_CLASS, userDetails.userClass)
            preferenceManager.putString(Constants.KEY_IMAGE, userDetails.image)
            preferenceManager.putString(Constants.KEY_YEAR, userDetails.year)
            preferenceManager.putString(Constants.KEY_FATHER_NAME, userDetails.fatherName)
            preferenceManager.putString(Constants.KEY_FATHER_NUMBER, userDetails.fatherNumber)
            preferenceManager.putString(Constants.KEY_MOTHER_NAME, userDetails.motherName)
            preferenceManager.putString(Constants.KEY_MOTHER_NUMBER, userDetails.motherNumber)
            preferenceManager.putString(Constants.KEY_EMAIL_VERIFIED_AT, userDetails.emailVerifiedAt)
            preferenceManager.putString(Constants.KEY_CREATED_AT, userDetails.createdAt)
            preferenceManager.putString(Constants.KEY_UPDATED_AT, userDetails.updatedAt)
            preferenceManager.putString(Constants.KEY_AGE, userDetails.age.toString())
            preferenceManager.putString(Constants.KEY_IS_APPROVED, userDetails.isApproved.toString())
            preferenceManager.putString(Constants.KEY_USER_TYPE, userDetails.userType)
            preferenceManager.putString(Constants.KEY_OTP, userDetails.otp)
            preferenceManager.putString(Constants.KEY_OTP_VERIFIED, userDetails.otpVerified.toString())
        }

        binding.txtPickupDropSameLocation.setOnClickListener {
            // Start PickupDropActivity for same pickup and drop location
            val intent = Intent(this, PickupDropActivity::class.java)
            intent.putExtra("MODE", "SAME_LOCATION") // Flag for same location
            startActivity(intent)
        }

        binding.txtOnlyDropLocation.setOnClickListener {
            // Start PickupDropActivity for only drop location
            val intent = Intent(this, PickupDropActivity::class.java)
            intent.putExtra("MODE", "ONLY_DROP") // Flag for only drop
            startActivity(intent)
        }

        binding.txtPickupDropDifferentLocation.setOnClickListener {
            // Start PickupDropActivity for different pickup and drop locations
            val intent = Intent(this, PickupDropActivity::class.java)
            intent.putExtra("MODE", "DIFFERENT_LOCATION") // Flag for different locations
            startActivity(intent)
        }
    }

    private fun loadImageWithGlide(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl) // Load the image URL
            .into(binding.profileImage) // Set the image to the ImageView
    }
}


