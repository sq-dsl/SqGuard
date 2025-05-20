package com.sqwerty.res_guard.utils

import com.sqwerty.core.utils.isImage
import com.sqwerty.res_guard.ResGuardPluginExtensions
import org.gradle.api.Project
import java.io.File

object Helper {
    fun getResGuardMappingFile(project: Project): File {
        val filePath = project.extensions.getByType(ResGuardPluginExtensions::class.java)
            .outputMappingPath ?: project.file("release").apply { mkdir() }.absolutePath
        return File(filePath, "resGuard.map")
    }

    fun getResGuardMap(project: Project): Map<String, String> {
        return getResGuardMappingFile(project).readText().lines().filterNot { it.isBlank() }
            .associate { it.split("->").run { get(0) to get(1) } }
    }

    fun File.updateFileName(encryptedValue: String, project: Project) {
        val newName = "${encryptedValue}.${extension}"
        val newPath = parent + File.separator + newName
        renameTo(project.file(newPath))
    }

    fun String.replaceRes(type: ResType, value: String, key: String): String {
        return replace(
            Regex("@${type.name.lowercase()}/${value}\\b"),
            "@${type.name.lowercase()}/${key}"
        ).replace(
            Regex("R\\.${type.name.lowercase()}\\.${value}\\b"),
            "R.${type.name.lowercase()}.${key}"
        )
    }

    fun File.isInBlacklist(project: Project): Boolean {
        return isImage() || absolutePath.contains("build") || extension.run {
            contains("mp") || contains("tf")
        }
    }
}