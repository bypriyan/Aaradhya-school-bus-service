package com.bypriyan.gradify.utility

import android.util.Base64
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

object AESHelper {
    private const val AES = "AES"

    // Generate a 128-bit AES Key
    fun generateKey(): SecretKey {
        val keyGen = KeyGenerator.getInstance(AES)
        keyGen.init(128) // Specify key size
        return keyGen.generateKey()
    }

    // Encrypt the text
    fun encrypt(input: String, secretKey: SecretKey): String {
        val cipher = Cipher.getInstance(AES)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(input.toByteArray(StandardCharsets.UTF_8))
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    // Decrypt the text
    fun decrypt(encryptedInput: String, secretKey: SecretKey): String {
        val cipher = Cipher.getInstance(AES)
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decodedBytes = Base64.decode(encryptedInput, Base64.DEFAULT)
        val originalBytes = cipher.doFinal(decodedBytes)
        return String(originalBytes, StandardCharsets.UTF_8)
    }
}
