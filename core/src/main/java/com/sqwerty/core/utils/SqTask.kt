package com.sqwerty.core.utils

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

abstract class SqTask : DefaultTask() {
    init {
        addActions()
    }

    private fun Task.addActions() {
        onlyIf { onlyIf() }
        doFirst { doFirst() }
        doLast { doLast() }
    }

    open fun Task.doLast(): Unit = Unit
    open fun Task.doFirst(): Unit = Unit
    open fun Task.onlyIf(): Boolean = true

    abstract class SqTaskCompanion {
        val taskKClass: KClass<out SqTask> = Class.forName(
            this@SqTaskCompanion::class.java.name.substringBefore("$")
        ).kotlin as KClass<out SqTask>

        abstract fun Project.addToTaskSequence()

        operator fun invoke(project: Project) {
            project.registerTask()
            project.afterEvaluate { addToTaskSequence() }
        }

        private fun Project.registerTask() {
            tasks.register(taskKClass.jvmName, taskKClass.java) {
                group = "splinter-sdk"
            }
        }
    }
}