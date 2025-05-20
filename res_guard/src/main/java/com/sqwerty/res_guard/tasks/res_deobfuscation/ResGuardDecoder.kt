package com.sqwerty.res_guard.tasks.res_deobfuscation

import com.sqwerty.core.utils.SqTask
import com.sqwerty.core.utils.getAllProjectFilesViaRecursion
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
import kotlin.reflect.jvm.jvmName

open class ResGuardDecoder : SqTask() {
    override fun Task.doLast() {
        val resources = getResources(project)
        val resGuardMap = getResGuardMap(project).toList()
            .associate { (key, value) -> value to key }

        val scope = CoroutineScope(Dispatchers.IO)
        val asyncResUpdating = resources.groupBy { res -> res.nameWithoutExtension }.values
            .map { grouped ->
                scope.launch {
                    val res = grouped.first()
                    val baseName = resGuardMap.getValue(res.nameWithoutExtension)
                    grouped.forEach {
                        it.updateFileName(baseName, project)
                    }
                }
            }
        scope.launch {
            asyncResUpdating.joinAll()
            val s = File.separator
            getAllProjectFilesViaRecursion(project.layout.projectDirectory.dir("src${s}main").asFile)
                .forEach { file ->
                    if (file.isInBlacklist(project)) return@forEach
                    file.readText().apply {
                        var updatedText = this
                        resGuardMap.forEach { (key, value) ->
                            updatedText = ResType.values().fold(updatedText) { prev, resType ->
                                prev.replaceRes(resType, key, value)
                            }
                        }
                        file.writeText(updatedText)
                    }
                }
        }
    }

    override fun Task.onlyIf(): Boolean {
        return Helper.getResGuardMappingFile(project).exists()
    }

    companion object : SqTaskCompanion() {
        override fun Project.addToTaskSequence() {
            tasks.named("assembleRelease") { finalizedBy(taskKClass.jvmName) }
        }
    }
}