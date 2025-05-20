package com.sqwerty.res_guard

import org.gradle.api.Action
import org.gradle.api.Project

fun Project.configureResGuard(configure: Action<ResGuardPluginExtensions>) {
    val extension = extensions.findByType(ResGuardPluginExtensions::class.java)!!
    configure.execute(extension)
}
