package com.qi.helloworld;

import android.icu.text.DateFormat;

import androidx.room.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

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

    /*@TypeConverter
    public static Date timestampToDate(String value) {
        //if (value != null) {
        //    try {
        //        return df.parse(value);
        //    } catch (ParseException e) {
        //        e.printStackTrace();
        //    }
        //}
        //return null;
    }
    @TypeConverter
    public static String dateToTimestamp(Date date) {
        //if (date != null) {
        //    return df.format(date);
        //}
        //return null;
    }*/

}
