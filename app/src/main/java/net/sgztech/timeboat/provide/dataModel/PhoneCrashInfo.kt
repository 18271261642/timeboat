package net.sgztech.timeboat.provide.dataModel

import androidx.annotation.Keep

@Keep
class PhoneCrashInfo {
    var versionName: String? = null
    var versionCode: String? = null
    var osVersion: String? = null
    var sdkVersion: String? = null
    var manufacture: String? = null
    var model: String? = null

    // cpu架构
    var cpuAbi: String? = null
    var exception: String? = null
}