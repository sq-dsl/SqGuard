package com.sqwerty.res_guard.tasks.res_resize

data class ImageSize(val x: Int, val y: Int) {
        infix fun standardCheck(size: ImageSize): Boolean {
            return x == size.x && y == size.y
        }

        companion object {
            val portraitBackground = ImageSize(x = 1080, y = 1920)
            val landscapeBackground = ImageSize(x = 1920, y = 1080)
        }
    }