package com.projectx.fitfloaw.localdatabase;

import androidx.room.TypeConverter;

import java.time.LocalDate;

public class Converters {
    @TypeConverter
    public static Boolean fromInteger(Integer value) {
        return value == null ? null : value != 0;
    }

    @TypeConverter
    public static Integer booleanToInteger(Boolean value) {
        return value == null ? null : (value ? 1 : 0);
    }

    @TypeConverter
    public static LocalDate fromTimestamp(Long value) {
        return value == null ? null : LocalDate.ofEpochDay(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(LocalDate date) {
        return date == null ? null : date.toEpochDay();
    }
}
