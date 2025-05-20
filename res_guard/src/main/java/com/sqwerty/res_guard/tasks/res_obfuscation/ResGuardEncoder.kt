package com.sqwerty.res_guard.tasks.res_obfuscation

import com.sqwerty.core.utils.SqTask
import com.sqwerty.core.utils.getAllProjectFilesViaRecursion
import com.sqwerty.res_guard.ResGuardPluginExtensions
import com.sqwerty.res_guard.tasks.res_obfuscation.ResGuardEncryptor.encryptValue
import com.sqwerty.res_guard.utils.Helper
import com.sqwerty.res_guard.utils.Helper.getResGuardMap
import com.sqwerty.res_guard.utils.Helper.getResources
import com.sqwerty.res_guard.utils.Helper.isInBlacklist
import com.sqwerty.res_guard.utils.Helper.replaceRes
import com.sqwerty.res_guard.utils.Helper.updateFileName
import com.sqwerty.res_guard.utils.ResType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.gradle.api.Project
import org.gradle.api.Task
import java.io.File
import java.io.FileOutputStream

open class ResGuardEncoder : SqTask() {
    private val mappingFile = Helper.getResGuardMappingFile(project)

    override fun Task.doLast() {
        val mappingFOS = FileOutputStream(mappingFile, false)
        val resources = getResources(project)
        val scope = CoroutineScope(Dispatchers.IO)

        val asyncResUpdating = resources.groupBy { res -> res.nameWithoutExtension }.values
            .map { grouped ->
                scope.launch {
                    val res = grouped.first()
                    val encryptedValue = res.nameWithoutExtension.encryptValue(project)
                    mappingFOS.writeValue(res.nameWithoutExtension, encryptedValue)
                    grouped.forEach {
                        it.updateFileName(encryptedValue, project)
                    }
                }
            }
        scope.launch {
            asyncResUpdating.joinAll()
            mappingFOS.close()
            val s = File.separator
            val latestMapping = getResGuardMap(project)
            getAllProjectFilesViaRecursion(project.layout.projectDirectory.dir("src${s}main").asFile)
                .forEach { file ->
                    if (file.isInBlacklist(project)) return@forEach
                    file.readText().apply {
                        var updatedText = this
                        latestMapping.forEach { (key, value) ->
                            updatedText = ResType.values().fold(updatedText) { prev, resType ->
                                prev.replaceRes(resType, value, key)
                            }
                        }
                        file.writeText(updatedText)
                    }
                }
        }
    }

    private fun FileOutputStream.writeValue(value: String, encryptedValue: String) {
        val pair = "${value}->${encryptedValue}\n"
        write(pair.encodeToByteArray())
    }

    override fun Task.onlyIf(): Boolean {
        return project.extensions.getByType(ResGuardPluginExtensions::class.java).enabled
    }

    companion object : SqTaskCompanion() {
        override fun Project.addToTaskSequence() {
            //tasks.named("preReleaseBuild") { dependsOn(taskKClass.jvmName) }
        }
    }
}