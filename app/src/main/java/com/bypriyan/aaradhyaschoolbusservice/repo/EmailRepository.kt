package com.bypriyan.aaradhyaschoolbusservice.repo

import com.bypriyan.aaradhyaschoolbusservice.utility.SmtpSender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EmailRepository @Inject constructor(private val smtpSender: SmtpSender) {

    suspend fun sendOtp(email: String): String {
        return withContext(Dispatchers.IO) { // Run in Background Thread
            smtpSender.sendOtp(email)
        }
    }
}