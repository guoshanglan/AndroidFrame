package com.zhuorui.securties.debug.info

/**
 * Created by wanglikun on 2018/9/14.
 */
class SysInfoItem {
    var isPermission = false
    val name: String
    val value: String

    constructor(name: String, value: String) {
        this.name = name
        this.value = value
    }

    constructor(name: String, value: String, isPermission: Boolean) {
        this.name = name
        this.value = value
        this.isPermission = isPermission
    }
}