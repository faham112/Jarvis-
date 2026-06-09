package com.example.util

import android.util.Base64
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Robust symmetric encryption helper for Jarvis personal data, active logs, and triggers.
 * Demonstrates local, offline cryptographic operations for end-to-end data security.
 */
object JarvisCryptography {
    private const val ALGORITHM = "AES/CBC/PKCS5Padding"
    private val DEFAULT_IV = byteArrayOf(
        0x51, 0x41, 0x52, 0x56, 0x49, 0x53, 0x5f, 0x53, // "JARVIS_S"
        0x45, 0x43, 0x55, 0x52, 0x45, 0x5f, 0x49, 0x56  // "ECURE_IV"
    )

    /**
     * Derives a 256-bit AES Key from any string secret phrase.
     */
    fun deriveKey(passphrase: String): SecretKeySpec {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = passphrase.toByteArray(Charsets.UTF_8)
        val keyBytes = digest.digest(bytes)
        return SecretKeySpec(keyBytes, "AES")
    }

    /**
     * Encrypts plain text using AES/CBC/PKCS5Padding.
     */
    fun encrypt(plainText: String, secretKey: SecretKeySpec): String {
        return try {
            val cipher = Cipher.getInstance(ALGORITHM)
            val ivSpec = IvParameterSpec(DEFAULT_IV)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
            val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
            Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
        } catch (e: Exception) {
            "ERR_ENCRYPT_FAIL: ${e.localizedMessage}"
        }
    }

    /**
     * Decrypts AES/CBC/PKCS5Padding encrypted base64 string.
     */
    fun decrypt(cipherText: String, secretKey: SecretKeySpec): String {
        return try {
            val cipher = Cipher.getInstance(ALGORITHM)
            val ivSpec = IvParameterSpec(DEFAULT_IV)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
            val decodedBytes = Base64.decode(cipherText, Base64.DEFAULT)
            val decryptedBytes = cipher.doFinal(decodedBytes)
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            "ERR_DECRYPT_FAIL (Invalid Key or Corrupt Cipher)"
        }
    }
}
