package com.zhuorui.commonwidget.enums

import base2app.ex.text
import com.zhuorui.commonwidget.R

/**
 *
 * @Description:    性别枚举
 * @Author:         luosi
 */
enum class SexTypeEnum(val sexStr: String, val sexCode: Int) {
    MALE(text(R.string.male), 1),//男性
    FEMALE(text(R.string.female), 2),//女性
    DEFAULT(text(R.string.secrecy), 3);//保密（默认）

    companion object {

        fun getSex(sexStr: String?): SexTypeEnum {
            sexStr?.let {
                values().forEach {
                    if (it.sexStr == sexStr) {
                        return it
                    }
                }
            }
            return DEFAULT
        }

        fun getSex(sexCode: Int?): SexTypeEnum {
            sexCode?.let {
                values().forEach {
                    if (it.sexCode == sexCode) {
                        return it
                    }
                }
            }
            return DEFAULT
        }

    }
}