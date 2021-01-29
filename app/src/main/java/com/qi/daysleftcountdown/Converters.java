package com.qi.daysleftcountdown;

import androidx.room.TypeConverter;
import java.util.Date;

/**
 * Converters used for Dao.
 */
public class Converters {

    //static SimpleDateFormat df = new SimpleDateFormat();

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : (date.getTime());
    }

}
