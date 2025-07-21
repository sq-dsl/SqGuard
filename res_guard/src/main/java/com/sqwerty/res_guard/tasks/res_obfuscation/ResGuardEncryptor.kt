package com.sqwerty.res_guard.tasks.res_obfuscation

import com.sqwerty.res_guard.extensions.ResGuardExtensions
import org.gradle.api.Project
import java.security.MessageDigest
import java.util.Base64
import java.util.UUID
import java.util.regex.Pattern

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

        // Encode and sanitize to valid Android resource name (only a-z, 0-9)
        val rawEncoded = Base64.getUrlEncoder().withoutPadding().encodeToString(hash).lowercase()
        val safeBase = rawEncoded.replace(Regex("[^a-z0-9]"), "")

        // Ensure first character is a letter
        val randomLetter = ('a'..'z').random()
        val base = if (safeBase.firstOrNull()?.isLetter() != true) "$randomLetter$safeBase" else safeBase

        // Get min/max name lengths from extension config
        val extensions = project.extensions.getByType(ResGuardExtensions::class.java)
        val minNameLength = extensions.minNameLength.coerceAtLeast(16)
        val maxNameLength = extensions.maxNameLength.coerceAtMost(246)
        val targetLength = (minNameLength..maxNameLength).random()

        // Build result by appending random letters until desired length
        val result = buildString {
            append(base)
            while (length < targetLength) append(('a'..'z').random())
        }

        return result.take(maxNameLength)
    }
}