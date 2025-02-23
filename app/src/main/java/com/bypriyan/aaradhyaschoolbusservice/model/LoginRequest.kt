package com.bypriyan.aaradhyaschoolbusservice.model

data class LoginRequest(
    val full_name: String,
    val email: String,
    val class_: String,
    val image: String, // Base64 encoded image or file path
    val password: String,
    val age: String,
    val standard: String,
    val year: String,
    val father_name: String,
    val father_number: String,
    val mother_name: String,
    val mother_number: String
)