package com.bypriyan.aaradhyaschoolbusservice

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityLogin2Binding
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityMainBinding

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLogin2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogin2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginBtn.setOnClickListener{
            startActivity(Intent(this, HomeActivity::class.java))
        }
    }
}