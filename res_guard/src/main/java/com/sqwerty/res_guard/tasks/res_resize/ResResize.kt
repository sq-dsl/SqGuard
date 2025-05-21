package com.sqwerty.res_guard.tasks.res_resize

import com.sqwerty.core.utils.SqTask
import com.sqwerty.core.utils.ifNotExist
import com.sqwerty.res_guard.extensions.ResResizeExtensions
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.Directory
import java.io.File
import kotlin.reflect.jvm.jvmName

open class ResResize : SqTask() {
    private val isWindows = System.getProperty("os.name").lowercase().contains("win")
    private lateinit var magickDir: Directory
    private val s = File.separator
    private val drawableDir = project.layout.projectDirectory.dir("src${s}main${s}res${s}drawable")
        .asFile

    override fun Task.doLast() {
        prepareDirs()
        resizeImages()
    }

    override fun Task.onlyIf(): Boolean {
        val extensions = project.extensions.getByType(ResResizeExtensions::class.java)
        if (isWindows) {
            val pathToMagick = extensions.pathToMagick ?: return false
            magickDir = File(pathToMagick).ifNotExist { return false }
                .run { project.objects.directoryProperty().apply { set(this@run) }.get() }
        }
        return project.extensions.getByType(ResResizeExtensions::class.java).enabled
                && drawableDir.listFiles().count { it.extension == "png" } > 0
                && (isDesignStandardized() || extensions.resizeHard)
    }

    companion object : SqTaskCompanion() {
        override fun Project.addToTaskSequence() {
            tasks.named("preBuild").configure { dependsOn(taskKClass.jvmName) }
        }
    }

    private fun isDesignStandardized(): Boolean {
        return drawableDir.listFiles().filter { it.extension == "png" }
            .any {
                Runtime.getRuntime().exec(createCheckPrompt(it)).inputStream.use {
                    it.readBytes().decodeToString().split(" ").let {
                        ImageSize(x = it[0].toInt(), y = it[1].toInt())
                    }
                }.run {
                    this standardCheck ImageSize.portraitBackground || this standardCheck ImageSize.landscapeBackground
                }
            }
    }

    private fun resizeImages() {
        val images = drawableDir.listFiles().filter { it.extension == "png" }
        images.forEach { image ->
            Dpi.values().forEach { dpi ->
                val convertPrompt = createConvertPrompt(image, dpi)
                Runtime.getRuntime().exec(convertPrompt).inputStream.use {
                    it.readBytes()
                }
            }
            image.delete()
        }
    }

    private fun prepareDirs() {
        Dpi.values().forEach {
            val resDir = project.objects.directoryProperty().apply { set(drawableDir.parentFile) }
                .get()
            resDir.dir("drawable-${it.dpi}").asFile.ifNotExist { mkdir() }
        }
    }

    private fun createConvertPrompt(image: File, dpi: Dpi): Array<String> {
        val resDir = project.objects.directoryProperty().apply { set(drawableDir.parentFile) }.get()
        val resultPath = resDir.dir("drawable-${dpi.dpi}")
            .file(image.nameWithoutExtension + ".webp").asFile.absolutePath
        var convertCommand = arrayOf(
            image.absolutePath,
            "-strip",
            "-resize",
            "${dpi.resolution}%",
            "-quality",
            "80", resultPath
        )
        if (isWindows) {
            val magickExe = magickDir.file("magick.exe").asFile.absolutePath
            convertCommand = arrayOf(magickExe) + convertCommand
        } else {
            convertCommand = arrayOf("convert") + convertCommand
        }
        return convertCommand
    }

    private fun createCheckPrompt(image: File): Array<String> {
        val isWindows = System.getProperty("os.name").lowercase().contains("win")
        var checkSizeDefaultCommand = arrayOf("identify", "-format", "%w %h", image.absolutePath)
        if (isWindows) {
            val magickExe = magickDir.file("magick.exe").asFile.absolutePath
            checkSizeDefaultCommand = arrayOf(magickExe) + checkSizeDefaultCommand
        }
        return checkSizeDefaultCommand
    }

}