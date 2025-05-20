package com.sqwerty.res_guard

import com.sqwerty.res_guard.utils.ResType

open class ResGuardPluginExtensions() {
    var enabled: Boolean = false
    var resTypes: List<ResType> = ResType.values().toList()

    /**
     * @author should include full path. Example: C:\Users\You
     */
    var outputMappingPath: String? = null
}