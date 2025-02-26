package com.bypriyan.aaradhyaschoolbusservice.apiResponce

data class ApiResponceSendOTP(
    val message: String,
    val otp: String,
    val status: String
)