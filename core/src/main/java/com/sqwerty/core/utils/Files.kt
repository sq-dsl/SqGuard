package com.sqwerty.core.utils

import java.io.File

fun getAllProjectFilesViaRecursion(file: File): List<File> {
    return if (file.listFiles() == null) listOf(file)
    else file.listFiles()!!.map {
        getAllProjectFilesViaRecursion(it)
    }.flatten()
}

fun File.isImage(): Boolean {
    return listOf(
        "jpeg", "jpg", "png", "gif", "bmp", "tiff", "tif", "webp", "heif", "heic",
    ).contains(extension)
}

inline fun File.ifExist(action: File.() -> Unit): File {
    if (exists()) action(this)
    return this
}

inline fun File.ifNotExist(action: File.() -> Unit): File {
    if (exists().not()) action(this)
    return this
}