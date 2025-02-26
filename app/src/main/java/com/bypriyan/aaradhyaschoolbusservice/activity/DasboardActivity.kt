package com.bypriyan.aaradhyaschoolbusservice.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.bypriyan.aaradhyaschoolbusservice.R
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityDasboardBinding
import com.bypriyan.aaradhyaschoolbusservice.viewModel.UserViewModel
import com.bypriyan.bustrackingsystem.utility.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DasboardActivity : AppCompatActivity() {

    lateinit var  binding: ActivityDasboardBinding
    lateinit var userId: String
    lateinit var token: String
    lateinit var token_type: String

    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDasboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = getIntent().getStringExtra(Constants.KEY_USER_ID).toString()
        token = getIntent().getStringExtra(Constants.KEY_TOKEN).toString()
        token_type = getIntent().getStringExtra(Constants.KEY_TOKEN_TYPE).toString()

        userViewModel.getUserDetails(userId, token)

        userViewModel.userDetails.observe(this) { response ->
            binding.name.text = "Hi, ${response.fullName}"
            loadImageWithGlide(imageUrl = response.image)
            Log.d("UserDetails", "User: ${response.fullName}, Email: ${response.email}")

        }

        Log.d("dash", "onCreate: $userId, $token, $token_type")

    }

    private fun loadImageWithGlide(imageUrl: String) {
        Glide.with(this)
            .load(Constants.KEY_IMAGE_PATH+imageUrl) // Load the image URL
            .into(binding.profileImage) // Set the image to the ImageView
    }
}