package com.bypriyan.aaradhyaschoolbusservice.utility

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bypriyan.aaradhyaschoolbusservice.R
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityDetailsLiveBinding
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityHomeBinding

class DetailsLiveActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetailsLiveBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsLiveBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}