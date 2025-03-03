package com.bypriyan.aaradhyaschoolbusservice.model

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val className: String,
    val password: String,
    val age: String,
    val standard: String,
    val year: String,
    val fatherName: String,
    val fatherNumber: String,
    val motherName: String,
    val motherNumber: String,
    val imageUri: String? // Use MultipartBody.Part? instead of String
)