package com.bypriyan.aaradhyaschoolbusservice.activity

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityStudentDetailBinding

class StudentDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStudentDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve data from Intent
        val fullName = intent.getStringExtra("full_name") ?: "N/A"
        val standard = intent.getStringExtra("standard") ?: "N/A"
        val className = intent.getStringExtra("class") ?: "N/A"
        val age = intent.getStringExtra("age") ?: "N/A"
        val year = intent.getStringExtra("year") ?: "N/A"
        val fatherName = intent.getStringExtra("father_name") ?: "N/A"
        val fatherPhone = intent.getStringExtra("father_phone") ?: "N/A"
        val motherName = intent.getStringExtra("mother_name") ?: "N/A"
        val motherPhone = intent.getStringExtra("mother_phone") ?: "N/A"
        val email = intent.getStringExtra("email") ?: "N/A"
        val password = intent.getStringExtra("password") ?: "N/A"
        val profileImageUri = intent.getStringExtra("profile_image_uri")

        // Set data to UI
        binding.txtName.text = fullName
        binding.txtStandard.text = "Standard: $standard"
        binding.txtClass.text = "Class: $className"
        binding.txtyear.text = "Year: $year"
        binding.txtMotherName.text = "Mother: $motherName"
        binding.txtFatherName.text = "Father: $fatherName"
        binding.txtId.text = "Age: $age"

        // Display Image if Available
        profileImageUri?.let {
            binding.userImage.setImageURI(Uri.parse(it))
        }
    }
}
