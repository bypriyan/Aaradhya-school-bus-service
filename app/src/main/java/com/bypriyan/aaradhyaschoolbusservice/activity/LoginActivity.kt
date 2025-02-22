package com.bypriyan.aaradhyaschoolbusservice.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

binding.tvRegister.setOnClickListener(){
    startActivity(Intent(this, SignUpActivity::class.java))
}


    }
}