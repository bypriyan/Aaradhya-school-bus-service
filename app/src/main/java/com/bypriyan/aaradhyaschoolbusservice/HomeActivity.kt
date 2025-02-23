package com.bypriyan.aaradhyaschoolbusservice

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.liveBtn.setOnClickListener{
            startActivity(Intent(this, LiveActivity::class.java))
        }

        binding.cardOne.setOnClickListener{
            startActivity(Intent(this, DetailActivity::class.java))
        }

    }
}