package com.bypriyan.aaradhyaschoolbusservice.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.bypriyan.aaradhyaschoolbusservice.R
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityDasboardBinding
import com.bypriyan.aaradhyaschoolbusservice.viewModel.UserViewModel
import com.bypriyan.bustrackingsystem.utility.Constants
import com.bypriyan.bustrackingsystem.utility.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

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

        Log.d("dash", "onCreate: $userId, $token, $token_type")

        userViewModel.getUserDetails(userId, token)

        userViewModel.userDetails.observe(this) { userDetails ->
            // Update UI with user details
            binding.name.text = "Hi, ${userDetails.fullName}"
            loadImageWithGlide(Constants.KEY_IMAGE_PATH+userDetails.image)
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


