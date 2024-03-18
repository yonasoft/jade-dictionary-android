package com.yonasoft.jadedictionary.util

import androidx.room.TypeConverter
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromWordIdList(value: List<Int>): String {
        val serializer = ListSerializer(Int.serializer())
        return Json.encodeToString(serializer, value)
    }

    @TypeConverter
    fun toWordIdList(value: String): List<Int> {
        val serializer = ListSerializer(Int.serializer())
        return Json.decodeFromString(serializer, value)
    }
}
