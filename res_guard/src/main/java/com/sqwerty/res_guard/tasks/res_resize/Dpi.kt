package com.sqwerty.res_guard.tasks.res_resize

enum class Dpi(val dpi: String, val resolution: Double) {
        LDPI("ldpi", 25.0),
        MDPI("mdpi", 33.33),
        HDPI("hdpi", 50.0),
        XHDPI("xhdpi", 66.67),
        XXHDPI("xxhdpi", 100.0),
        XXXHDPI("xxxhdpi", 150.0)
    }