package com.sqwerty.res_guard.tasks.res_deobfuscation

import com.sqwerty.core.utils.SqTask
import org.gradle.api.Project
import kotlin.reflect.KClass

open class ResGuardDecoder : SqTask() {

    companion object : SqTaskCompanion() {
        override fun Project.addToTaskSequence() {
            TODO("Not yet implemented")
        }
    }
}