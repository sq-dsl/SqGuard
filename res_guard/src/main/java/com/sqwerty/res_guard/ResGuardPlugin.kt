package com.sqwerty.res_guard

import com.sqwerty.res_guard.extensions.ResGuardExtensions
import com.sqwerty.res_guard.extensions.ResResizeExtensions
import com.sqwerty.res_guard.tasks.res_deobfuscation.ResGuardDecoder
import com.sqwerty.res_guard.tasks.res_obfuscation.ResGuardEncoder
import com.sqwerty.res_guard.tasks.res_resize.ResResize
import org.gradle.api.Plugin
import org.gradle.api.Project

class ResGuardPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.createExtensions()
        ResGuardEncoder.Companion(project)
        ResGuardDecoder.Companion(project)
        ResResize.Companion(project)
    }

    private fun Project.createExtensions() {
        extensions.create("resGuard-mapping", ResGuardExtensions::class.java)
        extensions.create("resGuard-resize", ResResizeExtensions::class.java)
    }
}