package com.zhuorui.securties.debug.fps

import base2app.ex.gson
import base2app.util.JsonUtil
import com.google.gson.reflect.TypeToken
import io.objectbox.converter.PropertyConverter

/**
 * BigDecimal没有默认支持,因此您必须创建Converter
 */
class JsonConverter : PropertyConverter<List<Pair<String, Int>>, String> {

    override fun convertToEntityProperty(databaseValue: String?): List<Pair<String, Int>>? {
        val type = object : TypeToken<List<Pair<String, Int>>>() {}.type
        return databaseValue?.let { JsonUtil.fromJson(it,type) }
    }


    override fun convertToDatabaseValue(entityProperty: List<Pair<String, Int>>?): String? {
       return entityProperty?.gson()
    }
}