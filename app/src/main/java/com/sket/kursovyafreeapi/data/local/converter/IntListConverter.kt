package com.sket.kursovyafreeapi.data.local.converter

import androidx.room.TypeConverter

class IntListConverter {
    @TypeConverter
    fun fromIntList(value: List<Int>): String = value.joinToString(",")

    @TypeConverter
    fun toIntList(value: String): List<Int> =
        value.takeIf(String::isNotBlank)
            ?.split(',')
            ?.mapNotNull(String::toIntOrNull)
            .orEmpty()
}
