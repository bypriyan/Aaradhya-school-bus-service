package com.bypriyan.aaradhyaschoolbusservice.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bypriyan.aaradhyaschoolbusservice.R
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityDasboardBinding
import com.bypriyan.bustrackingsystem.utility.Constants

class DasboardActivity : AppCompatActivity() {

    lateinit var  binding: ActivityDasboardBinding
    lateinit var userId: String
    lateinit var token: String
    lateinit var token_type: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDasboardBinding.inflate(layoutInflater)
        setContentView(binding.root)


        userId = getIntent().getStringExtra(Constants.KEY_USER_ID).toString()
        token = getIntent().getStringExtra(Constants.KEY_TOKEN).toString()
        token_type = getIntent().getStringExtra(Constants.KEY_TOKEN_TYPE).toString()

        Log.d("dash", "onCreate: $userId, $token, $token_type")

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
}


