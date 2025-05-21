package com.sqwerty.res_guard.extensions

open class ResResizeExtensions {
    /**
     * Example: C:\Users\You\magick
     */
    var pathToMagick: String? = null

    /**
     * Resizes all png resources, even if they don't fit 1080x1980 or 1980x1080.
     * Also useful when adding a new image to resized resources
     */
    var resizeHard: Boolean = false

    var enabled: Boolean = false
}