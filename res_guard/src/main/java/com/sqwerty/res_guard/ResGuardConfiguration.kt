package com.sqwerty.res_guard

import org.gradle.api.Action
import org.gradle.api.Project

inline fun <reified T> Project.configureResGuardPlugin(configure: Action<T>) {
    val extension = extensions.findByType(T::class.java)!!
    configure.execute(extension)
}
