package com.sqwerty.res_guard.tasks.res_obfuscation

import com.sqwerty.res_guard.utils.Helper
import org.gradle.api.Project
import java.security.MessageDigest
import java.util.Base64
import java.util.UUID

object ResGuardEncryptor {
    fun String.encryptValue(project: Project): String {
        fun getRandomLowercaseChar(): Char {
            return ('a'..'z').random()
        }

        val saltedInput = StringBuilder(this.lowercase())
        val salt = UUID.randomUUID().toString().replace("-", "").substring(0, 16)
        saltedInput.append(salt)

        val secretKey = project.projectDir.parentFile.parentFile.name + project.name
        val messageDigest = MessageDigest.getInstance("SHA-256")
        messageDigest.update(secretKey.toByteArray())
        messageDigest.update(saltedInput.toString().toByteArray())

        val hash = messageDigest.digest()
        val base64 = Base64.getEncoder().encodeToString(hash)
            .replace("+", getRandomLowercaseChar().toChar().toString())
            .replace("/", getRandomLowercaseChar().toString())
            .replace("=", getRandomLowercaseChar().toString())
            .lowercase()
        val finalOutput = StringBuilder()
        for (i in base64.indices) {
            finalOutput.append(base64[i])
            if (i % 5 == 0) {
                finalOutput.append(getRandomLowercaseChar())
            }
        }

        var result = if (finalOutput[0].isDigit()) {
            getRandomLowercaseChar() + finalOutput.substring(1)
        } else {
            finalOutput.toString()
        }
        if (result.length < 25) {
            result = result + result.toCharArray().apply { shuffle() }.concatToString()
        }
        return result.substring(0, result.length.coerceAtMost(127))
    }

}