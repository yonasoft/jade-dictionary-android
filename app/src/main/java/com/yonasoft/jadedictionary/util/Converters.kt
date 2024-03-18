package com.yonasoft.jadedictionary.util

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromWordIdList(value: List<Int>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toWordIdList(value: String): List<Int> {
        return Json.decodeFromString(value)
    }
}
