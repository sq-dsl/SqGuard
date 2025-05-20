package com.sqwerty.res_guard

import com.sqwerty.res_guard.tasks.res_obfuscation.ResGuardEncoder
import org.gradle.api.Plugin
import org.gradle.api.Project

class ResGuardPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.createExtensions()
        ResGuardEncoder.Companion(project)
    }

    private fun Project.createExtensions() {
        extensions.create("resGuard", ResGuardPluginExtensions::class.java)
    }
}