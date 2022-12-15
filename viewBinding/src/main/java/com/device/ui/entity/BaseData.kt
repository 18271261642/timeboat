package com.device.ui.entity

import androidx.annotation.Keep

@Keep
open class BaseData<T>() {
      var code: Int = 0
    var result : Int = 0
    lateinit var message: String
    var data: T? =  null

    constructor(
        code: Int = 0,
        result :Int = 0,
        message: String = "",
        data: T) : this() {
        this.code =code
        this.result =code
        this.message =message
        this.data = data
    }

    /**
     * 数据是否正确，默认实现
     */
    open fun codeSuccess(): Boolean {
        return code == 1
    }

    /**
     * 数据是否正确，默认实现
     */
    open fun success(): Boolean {
        return result == 1
    }

    /**
     * 获取错误信息，默认实现
     */
    open fun getMsg(): String {
        return message
    }
}