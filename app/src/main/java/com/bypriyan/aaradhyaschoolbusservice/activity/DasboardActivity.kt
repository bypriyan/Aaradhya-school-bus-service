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

        val draw = findViewById<DrawerLayout>(R.id.drawerLayout)
        userId = getIntent().getStringExtra(Constants.KEY_USER_ID).toString()
        token = getIntent().getStringExtra(Constants.KEY_TOKEN).toString()
        token_type = getIntent().getStringExtra(Constants.KEY_TOKEN_TYPE).toString()

        Log.d("dash", "onCreate: $userId, $token, $token_type")


        binding.btnMYPayments.setOnClickListener(){
            startActivity(Intent(this, PaymentHistory::class.java))
        }

        binding.btnPDLocation.setOnClickListener(){
        startActivity(Intent(this, PickupDropActivity::class.java))

        }

        var count = 0
        binding.menuBar.setOnClickListener(){
            if(count!=0){
            Toast.makeText(this,"clicked ", Toast.LENGTH_SHORT)
            draw.closeDrawer(GravityCompat.END)
            count--
            }else {draw.openDrawer(GravityCompat.END)
            count++}
        }
    }
}