package com.app.sololevelinghabittracker.data.local

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {
    // Convert LocalDate to String for Room storage
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }

    // Convert String back to LocalDate from Room storage
    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

}