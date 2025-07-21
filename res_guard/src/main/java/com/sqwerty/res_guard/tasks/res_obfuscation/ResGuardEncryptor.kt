package com.sqwerty.res_guard.tasks.res_obfuscation

import com.sqwerty.res_guard.extensions.ResGuardExtensions
import org.gradle.api.Project
import java.security.MessageDigest
import java.util.Base64
import java.util.UUID

object ResGuardEncryptor {
    fun String.encryptValue(project: Project): String {
        val input = this.lowercase()
        val salt = UUID.randomUUID().toString().replace("-", "").take(8)
        val timeComponent = System.currentTimeMillis().toString().takeLast(4)
        val secretKey = project.projectDir.parentFile.name + project.name

        val messageDigest = MessageDigest.getInstance("SHA-256")
        messageDigest.update(secretKey.toByteArray())
        messageDigest.update((input + salt + timeComponent).toByteArray())

        val hash = messageDigest.digest()
        val safeBase32 = Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(hash)
            .lowercase()

        val extensions = project.extensions.getByType(ResGuardExtensions::class.java)
        val minNameLength = extensions.minNameLength.coerceAtLeast(16)
        val maxNameLength = extensions.maxNameLength.coerceAtMost(246)

        val base = if (safeBase32[0].isDigit()) "${('a'..'Z').random()}$safeBase32" else safeBase32
        val padding = (minNameLength..maxNameLength).random() / 4
        val result = buildString {
            append(base)
            while (length < padding) append(('a'..'Z').random())
        }

        return result.take(maxNameLength)
    }

}