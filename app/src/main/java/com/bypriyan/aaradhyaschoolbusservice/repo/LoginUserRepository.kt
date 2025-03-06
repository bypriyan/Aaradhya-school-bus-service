package com.bypriyan.aaradhyaschoolbusservice.repo

import com.bypriyan.aaradhyaschoolbusservice.api.ApiServiceLogin
import com.bypriyan.aaradhyaschoolbusservice.apiResponce.ApiLoginResponse
import com.bypriyan.aaradhyaschoolbusservice.model.LoginUser
import javax.inject.Inject

class LoginUserRepository @Inject constructor(private val apiService: ApiServiceLogin) {


    suspend fun loginUser(email:String,password:String): ApiLoginResponse {


        return apiService.loginUser(email,password)
    }
}