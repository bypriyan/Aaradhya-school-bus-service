package com.bypriyan.aaradhyaschoolbusservice.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.unit.Constraints
import com.bumptech.glide.Glide
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityDasboardBinding
import com.bypriyan.aaradhyaschoolbusservice.viewModel.TokenViewModel
import com.bypriyan.aaradhyaschoolbusservice.viewModel.UserViewModel
import com.bypriyan.bustrackingsystem.utility.Constants
import com.bypriyan.bustrackingsystem.utility.PreferenceManager
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CheckOut1 : AppCompatActivity() {
    private var backPressedTime: Long = 0
    private val backPressThreshold: Long = 2000 // 2 seconds
    lateinit var  binding: ActivityDasboardBinding
    lateinit var userId: String
    private val userViewModel: UserViewModel by viewModels()
    @Inject
    lateinit var preferenceManager: PreferenceManager
    private val tokenViewModel: TokenViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDasboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = preferenceManager.getString(Constants.KEY_USER_ID)!!
        Log.d("TAGss", "onCreate: userId $userId")

        binding.profileImage.setOnClickListener(){
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        userViewModel.fetchUser(userId)
         // data getting
        userViewModel.user.observe(this) { userDetails ->
            userDetails?.data?.let { data ->
                Log.d("TAGss", "onCreate: $data")
                loadImageWithGlide(Constants.KEY_IMAGE_PATH+data.image_url)
                binding.name.text = data.full_name
                uploadToken(data.id.toString())
                preferenceManager.apply {
                    putString(Constants.KEY_FULL_NAME, data.full_name ?: "")
                    putString(Constants.KEY_EMAIL, data.email ?: "")
                    putString(Constants.KEY_USER_CLASS, data.`class`?: "")
                    putString(Constants.KEY_IMAGE, data.image_url ?: "")
                    putString(Constants.KEY_YEAR, data.year ?: "")
                    putString(Constants.KEY_STANDARD, data.standard ?: "")
                    putString(Constants.KEY_AGE, data.age.toString() ?: "")

                    Log.d("lull", "onCreate: ${data.guardians[0]}")
                    Log.d("lull", "onCreate: ${data.guardians[1]}")
                    Log.d("lull", "onCreate: ${data.guardians[2]}")

                    putString(Constants.KEY_FATHER_NAME, data.guardians[0].name)
                    putString(Constants.KEY_FATHER_NUMBER, data.guardians[0].phone_number)

                    putString(Constants.KEY_MOTHER_NAME, data.guardians[1].name)
                    putString(Constants.KEY_MOTHER_NUMBER, data.guardians[1].phone_number)

                    putString(Constants.KEY_GUARDIAN_NAME, data.guardians[2].name)
                    putString(Constants.KEY_GUARDIAN_PHONE, data.guardians[2].phone_number)
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

        tokenViewModel.tokenResponse.observe(this) { response ->
            Log.d("token", "onCreate: $response")
        }

        binding.signOut.setOnClickListener {
            showLogoutDialog()
        }

    }
    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Logout Confirmation")
        builder.setMessage("Are you sure you want to log out?")

        builder.setPositiveButton("Yes") { dialog, _ ->
            dialog.dismiss()
            signOutUser()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun signOutUser() {
        preferenceManager.clear()
        preferenceManager.putBoolean(Constants.KEY_IS_ONBORDING_SCREEN_SEEN, true)
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun loadImageWithGlide(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl) // Load the image URL
            .into(binding.profileImage) // Set the image to the ImageView
    }

    private fun uploadToken(userId: String){
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM_TOKEN", "FCM Token: $token")
                tokenViewModel.insertOrUpdateToken(userId, token)
            } else {
                Log.e("FCM_TOKEN", "Failed to get FCM token", task.exception)
            }
        }
    }


    override fun onBackPressed() {
        if (backPressedTime + backPressThreshold > System.currentTimeMillis()) {
            super.onBackPressed()
            finish()
        } else {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()
    }
}


