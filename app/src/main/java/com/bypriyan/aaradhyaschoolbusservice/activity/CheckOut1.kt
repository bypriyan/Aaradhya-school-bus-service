package com.bypriyan.aaradhyaschoolbusservice.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.unit.Constraints
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

        Log.d("dashss", "onCreate: $userId, $token, $token_type")
        userViewModel.fetchUser(userId)

        binding.profileImage.setOnClickListener(){
            startActivity(Intent(this, ProfileActivity::class.java))
        }

         // data getting
        userViewModel.user.observe(this) { userDetails ->
            userDetails?.data?.let { data ->
                Log.d("TAGss", "onCreate: $data")
                loadImageWithGlide(Constants.KEY_IMAGE_PATH+data.image_url)
                preferenceManager.apply {
                    putString(Constants.KEY_FULL_NAME, data.full_name ?: "")
                    putString(Constants.KEY_EMAIL, data.email ?: "")
                    putString(Constants.KEY_USER_CLASS, data.`class`?: "")
                    putString(Constants.KEY_IMAGE, data.image_url ?: "")
                    putString(Constants.KEY_YEAR, data.year ?: "")
                    putString(Constants.KEY_STANDARD, data.standard ?: "")
                    putString(Constants.KEY_AGE, data.age.toString() ?: "")
                }
            } ?: run {
                Log.e("UserDetails", "userDetails or data is null")
            }
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


