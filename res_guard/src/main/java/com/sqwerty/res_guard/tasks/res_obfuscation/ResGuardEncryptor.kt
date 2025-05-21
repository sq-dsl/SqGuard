package com.sqwerty.res_guard.tasks.res_obfuscation

import com.sqwerty.res_guard.extensions.ResGuardExtensions
import org.gradle.api.GradleException
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

        val timeComponent = System.currentTimeMillis().toString().takeLast((3..7).random())
        saltedInput.append(timeComponent)

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
        val extensions = project.extensions.getByType(ResGuardExtensions::class.java)
        val minNameLength = extensions.minNameLength
        val maxNameLength = extensions.maxNameLength

        if (maxNameLength > 255 || minNameLength < 16) throw GradleException()

        repeat((6..36).random()) {
            result += getRandomLowercaseChar()
        }
        if (result.length < minNameLength) {
            repeat(minNameLength - result.length + (0..96).random()) { result += getRandomLowercaseChar() }
        }

        return result.substring(0, result.length.coerceAtMost(maxNameLength))
    }

}