package com.bypriyan.aaradhyaschoolbusservice.apiResponce

import com.google.gson.annotations.SerializedName

data class UserDetails(
    val id: Int,
    @SerializedName("full_name") val fullName: String,
    val email: String,
    @SerializedName("class") val userClass: String,
    val image: String,
    val year: String,
    @SerializedName("father_name") val fatherName: String,
    @SerializedName("father_number") val fatherNumber: String,
    @SerializedName("mother_name") val motherName: String,
    @SerializedName("mother_number") val motherNumber: String,
    @SerializedName("email_verified_at") val emailVerifiedAt: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    val age: Int,
    val standard: String,
    @SerializedName("isApproved") val isApproved: Int,
    @SerializedName("userType") val userType: String,
    val otp: String?,
    @SerializedName("otp_verified") val otpVerified: Int
)