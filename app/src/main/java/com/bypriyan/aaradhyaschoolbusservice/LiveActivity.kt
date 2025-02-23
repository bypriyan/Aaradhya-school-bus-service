package com.bypriyan.aaradhyaschoolbusservice

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityHomeBinding
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityLiveBinding
import com.bypriyan.aaradhyaschoolbusservice.utility.DetailsLiveActivity

class LiveActivity : AppCompatActivity() {

    lateinit var binding: ActivityLiveBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityLiveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cardOne.setOnClickListener{
            startActivity(Intent(this, DetailsLiveActivity::class.java))
        }
    }
}