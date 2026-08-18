package com.shubhamthorat.androidtechnicaldeepdive

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.InputStream
import java.io.OutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

/**
 * ANDROID KEYSTORE MASTERY (0 to 100) - INTERVIEW EDITION
 *
 * The Android Keystore system lets you store cryptographic keys in a container to make it
 * more difficult to extract from the device.
 *
 * KEY CONCEPTS:
 * 1. Hardware Security Module (HSM): Keys are often stored in TEE (Trusted Execution Environment) 
 *    or SE (Secure Element), making them non-exportable.
 * 2. Key Material: The actual bytes of the key never enter the application's process.
 */

// =========================================================================================
// PART 1: CRYPTO MANAGER (AES SYMMETRIC ENCRYPTION)
// =========================================================================================

/**
 * A helper class to manage AES encryption/decryption using the Android Keystore.
 */
class CryptoManager {

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    private val encryptCipher get() = Cipher.getInstance(TRANSFORMATION)

    private fun getDecryptCipherForIv(iv: ByteArray): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(iv))
        }
    }

    private fun getKey(): SecretKey {
        val existingKey = keyStore.getEntry(ALIAS, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey()
    }

    /**
     * Creating a key in the Keystore.
     * Note: We specify that the key can only be used for ENCRYPT and DECRYPT.
     */
    private fun createKey(): SecretKey {
        return KeyGenerator.getInstance(ALGORITHM).apply {
            init(
                KeyGenParameterSpec.Builder(
                    ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(PADDING)
                    .setUserAuthenticationRequired(false) // Can be set to true for biometrics
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
        }.generateKey()
    }

    /**
     * Encrypting a byte array and writing to an output stream.
     * We prepend the IV (Initialization Vector) so we can use it for decryption.
     */
    fun encrypt(bytes: ByteArray, outputStream: OutputStream): ByteArray {
        val cipher = encryptCipher
        cipher.init(Cipher.ENCRYPT_MODE, getKey())
        val iv = cipher.iv
        outputStream.use {
            it.write(iv.size)
            it.write(iv)
            it.write(cipher.doFinal(bytes))
        }
        return iv
    }

    /**
     * Decrypting from an input stream.
     */
    fun decrypt(inputStream: InputStream): ByteArray {
        return inputStream.use {
            val ivSize = it.read()
            val iv = ByteArray(ivSize)
            it.read(iv)
            
            val encryptedBytes = it.readBytes()
            getDecryptCipherForIv(iv).doFinal(encryptedBytes)
        }
    }

    companion object {
        private const val ALIAS = "my_secret_key"
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
    }
}

// =========================================================================================
// PART 2: ASYMMETRIC ENCRYPTION (RSA)
// =========================================================================================

/**
 * RSA is used for Digital Signatures or small data encryption (like wrapping AES keys).
 * 
 * INTERVIEW TIP: 
 * Symmetric (AES) = Same key for Encrypt/Decrypt. Faster. Better for large data.
 * Asymmetric (RSA) = Public Key (Encrypt/Verify) & Private Key (Decrypt/Sign).
 */
object RsaMastery {
    // Similar to AES but uses KeyPairGenerator
    // KeyProperties.KEY_ALGORITHM_RSA
}

// =========================================================================================
// INTERVIEW DEEP DIVE & SUMMARY
// =========================================================================================

/**
 * 1. TEE vs SE:
 *    - TEE (Trusted Execution Environment): A secure area of the main processor.
 *    - SE (Secure Element): A separate hardware chip (like in Pixel/iPhone). More secure.
 * 
 * 2. Key Attestation:
 *    Allows you to verify that the key is actually stored in hardware and not in software.
 * 
 * 3. Biometric Binding:
 *    By calling .setUserAuthenticationRequired(true), the key can only be used if 
 *    the user authenticates via fingerprint/face within a certain time frame.
 * 
 * 4. Is the Keystore encrypted?
 *    The Keystore itself is a system service. The key material is stored by the system 
 *    and bound to the user's lock screen credentials.
 * 
 * 5. What happens if the user changes their lock screen?
 *    If the key is "Invalidated on new biometric enrollment", it becomes unusable 
 *    for security reasons.
 */
