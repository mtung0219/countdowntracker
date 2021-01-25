package com.qi.helloworld;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@Entity(tableName = "event_table")
public class Event implements Serializable {

    @PrimaryKey(autoGenerate = true)
    //@ColumnInfo(name = "id")
    private int id = 0;

    @NonNull
    //@PrimaryKey
    @ColumnInfo(name = "event")
    private String name;

    @ColumnInfo(name = "date")
    private Date date;

    @ColumnInfo(name = "notifID")
    private int notifID;

    public Event(@NonNull String name, Date date, int notifID) {
        this.name = name;
        this.date = date;
        this.notifID = notifID;
    }

    @NonNull
    public String getName() {
        return this.name;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return this.date;
    }

    public long getDateLong() {
        return this.date.getTime();
    }

    public int getNotifID() { return this.notifID; }

    public void setNotifId(int notifID) { this.notifID = notifID; }

    @Ignore
    public int getDaysLeft() {
        Calendar now = Calendar.getInstance();
        Calendar later = Calendar.getInstance();
        //now.setTimeInMillis(getNowInMillis());
        now.setTime(getNow());
        later.setTime(this.date);
        return daysBetween(now, later);
    }

    @Ignore
    public int[] getYearsMonthsDaysLeft() {
        Calendar now = Calendar.getInstance();
        Calendar later = Calendar.getInstance();
        //now.setTimeInMillis(getNowInMillis());
        now.setTime(getNow());
        later.setTime(this.date);
        return timeBetween(now, later);
    }


    @Ignore
    private Calendar calFix(Calendar c, Date d) {
        c.setTime(d);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        return c;
    }

    @Ignore
    public long getNowInMillis() {
        Calendar c = Calendar.getInstance();
        Date prelimDate = c.getTime();

        c.setTime(prelimDate);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);

        return c.getTime().getTime();
    }

    @Ignore
    public Date getNow() {
        Calendar c = Calendar.getInstance();
        Date prelimDate = c.getTime();

        c.setTime(prelimDate);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);

        return c.getTime();
    }


    private static int[] timeBetween(Calendar now, Calendar later) {
        Calendar dayOne = (Calendar) now.clone(),
                dayTwo = (Calendar) later.clone();

        if (dayTwo.getTime().getTime() > dayOne.getTime().getTime()) {
            //swap them, dayOne is always the later one
            Calendar temp = dayOne;
            dayOne = dayTwo;
            dayTwo = temp;
        }
        int yearsBetween = 0;
        int monthsBetween = 0;
        int daysBetween = 0;
        int hoursBetween = 0;
        int minutesBetween = 0;

        while (dayOne.getTime().getTime() > dayTwo.getTime().getTime()) {
            yearsBetween += 1;
            dayOne.add(Calendar.YEAR, -1);
        }

        if (dayOne.getTime().getTime() < dayTwo.getTime().getTime()) {
            yearsBetween -= 1;
            dayOne.add(Calendar.YEAR, 1);
        }

        while (dayOne.getTime().getTime() > dayTwo.getTime().getTime()) {
            monthsBetween += 1;
            dayOne.add(Calendar.MONTH, -1);
        }

        if (dayOne.getTime().getTime() < dayTwo.getTime().getTime()) {
            monthsBetween -= 1;
            dayOne.add(Calendar.MONTH, 1);
        }

        while (dayOne.getTime().getTime() > dayTwo.getTime().getTime()) {
            daysBetween += 1;
            dayOne.add(Calendar.MILLISECOND, -1 * 1000 * 60 * 60 * 24);
        }

        if (dayOne.getTime().getTime() < dayTwo.getTime().getTime()) {
            daysBetween -= 1;
            dayOne.add(Calendar.MILLISECOND, 1000 * 60 * 60 * 24);
        }

        while (dayOne.getTime().getTime() > dayTwo.getTime().getTime()) {
            hoursBetween += 1;
            dayOne.add(Calendar.MILLISECOND, -1 * 1000 * 60 * 60);
        }

        if (dayOne.getTime().getTime() < dayTwo.getTime().getTime()) {
            hoursBetween -= 1;
            dayOne.add(Calendar.MILLISECOND, 1000 * 60 * 60);
        }

        while (dayOne.getTime().getTime() > dayTwo.getTime().getTime()) {
            minutesBetween += 1;
            dayOne.add(Calendar.MILLISECOND, -1 * 1000 * 60);
        }

        if (dayOne.getTime().getTime() < dayTwo.getTime().getTime()) {
            minutesBetween -= 1;
            dayOne.add(Calendar.MILLISECOND, 1000 * 60);
        }

        return new int[]{yearsBetween, monthsBetween, daysBetween, hoursBetween, minutesBetween};

    }
    @Ignore
    private static int daysBetween(Calendar now, Calendar later){
        // credit to John Leehey from stackOverflow
        Calendar dayOne = (Calendar) now.clone(),
                dayTwo = (Calendar) later.clone();
        boolean flipResult;
        Log.d("ASDF2","finding days between " + dayOne.getTime().getTime() + " and " +  dayTwo.getTime().getTime());

        if (dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR)) {
            //return Math.abs(dayOne.get(Calendar.DAY_OF_YEAR) - dayTwo.get(Calendar.DAY_OF_YEAR));
            return dayTwo.get(Calendar.DAY_OF_YEAR) - dayOne.get(Calendar.DAY_OF_YEAR);
        } else {
            if (dayTwo.get(Calendar.YEAR) > dayOne.get(Calendar.YEAR)) {
                //swap them
                Calendar temp = dayOne;
                dayOne = dayTwo;
                dayTwo = temp;
                flipResult = false;
            } else {
                flipResult = true;
            }
            int extraDays = 0;

            int dayOneOriginalYearDays = dayOne.get(Calendar.DAY_OF_YEAR);

            while (dayOne.get(Calendar.YEAR) > dayTwo.get(Calendar.YEAR)) {
                dayOne.add(Calendar.YEAR, -1);
                // getActualMaximum() important for leap years
                extraDays += dayOne.getActualMaximum(Calendar.DAY_OF_YEAR);
            }
            if (flipResult) return -(extraDays - dayTwo.get(Calendar.DAY_OF_YEAR) + dayOneOriginalYearDays);
            else return extraDays - dayTwo.get(Calendar.DAY_OF_YEAR) + dayOneOriginalYearDays ;
        }
    }

    @Ignore
    public String get_date_string() {
        Calendar c = Calendar.getInstance();
        c.setTime(this.date);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int year = c.get(Calendar.YEAR);
        String month_string = Integer.toString(month);
        String day_string = Integer.toString(day);
        String year_string = Integer.toString(year);

        return (month_string +
                "/" + day_string +
                "/" + year_string);
    }

    /*@Override
    public int compareTo(Event otherEvent) {
        if (otherEvent.getDate().after(this.getDate())) {
            return -1;
        } else if (otherEvent.getDate().equals(this.getDate())) {
            return otherEvent.getName().compareTo(this.getName());
        } else {
            return 1;
        }
    }*/


}