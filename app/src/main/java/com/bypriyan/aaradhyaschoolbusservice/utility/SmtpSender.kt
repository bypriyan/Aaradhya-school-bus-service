package com.bypriyan.aaradhyaschoolbusservice.utility

import java.util.*
import javax.inject.Inject
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import kotlin.random.Random

class SmtpSender @Inject constructor() {

    fun sendOtp(toEmail: String): String {
        val otp = generateOtp()

        val props = Properties().apply {
            put("mail.smtp.host", "smtp.hostinger.com")
            put("mail.smtp.port", "587")
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
        }

        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication("info@aaradhyaschoolbusservice.com", "Umar-&1234")
            }
        })

        return try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress("info@aaradhyaschoolbusservice.com", "Aaradhya School Bus Service"))
                addRecipient(Message.RecipientType.TO, InternetAddress(toEmail))
                subject = "Your OTP Code"
                setText("Your OTP code is: $otp \nThis code is valid for 5 minutes.")
            }

            Transport.send(message)
            otp // Return OTP for verification
        } catch (e: MessagingException) {
            e.printStackTrace()
            ""
        }
    }

    private fun generateOtp(): String {
        return (100000..999999).random().toString()
    }
}
