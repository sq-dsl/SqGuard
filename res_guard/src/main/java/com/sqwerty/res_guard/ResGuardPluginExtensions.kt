package com.sqwerty.res_guard

import com.sqwerty.res_guard.utils.ResType

open class ResGuardPluginExtensions() {
    var enabled: Boolean = false
    var resTypes: List<ResType> = ResType.values().toList()

    /**
     * @author should include full path. Example: C:\Users\You
     */
    var outputMappingPath: String? = null

    /**
     * @throws Exception value is less than 16
     */
    var minNameLength: Int = 25

    /**
     * @throws Exception value is greater than 255
     */
    var maxNameLength: Int = 255
}