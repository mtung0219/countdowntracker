package com.qi.helloworld;

import java.util.Calendar;
import java.util.Date;

public class CountdownObject implements Comparable<CountdownObject>{

    private String name;
    private Date eventDate;

    public CountdownObject(String name, Date eventDate) {
        this.name = name;
        this.eventDate = eventDate;
    }

    public String get_name() {
        return this.name;
    }

    public Date get_date() {
        return this.eventDate;
    }

    public int getDaysLeft() {
        Calendar c = Calendar.getInstance();
        Calendar c1 = Calendar.getInstance();
        c.setTime(getNow());
        c1.setTime(eventDate);
        return daysBetween(c, c1);
    }

    private static int daysBetween(Calendar day1, Calendar day2){
        // credit to John Leehey from stackOverflow
        Calendar dayOne = (Calendar) day1.clone(),
                dayTwo = (Calendar) day2.clone();

        if (dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR)) {
            return Math.abs(dayOne.get(Calendar.DAY_OF_YEAR) - dayTwo.get(Calendar.DAY_OF_YEAR));
        } else {
            if (dayTwo.get(Calendar.YEAR) > dayOne.get(Calendar.YEAR)) {
                //swap them
                Calendar temp = dayOne;
                dayOne = dayTwo;
                dayTwo = temp;
            }
            int extraDays = 0;

            int dayOneOriginalYearDays = dayOne.get(Calendar.DAY_OF_YEAR);

            while (dayOne.get(Calendar.YEAR) > dayTwo.get(Calendar.YEAR)) {
                dayOne.add(Calendar.YEAR, -1);
                // getActualMaximum() important for leap years
                extraDays += dayOne.getActualMaximum(Calendar.DAY_OF_YEAR);
            }

            return extraDays - dayTwo.get(Calendar.DAY_OF_YEAR) + dayOneOriginalYearDays ;
        }
    }

    public Date getNow() {
        Calendar c = Calendar.getInstance();
        Date prelimDate = c.getTime();

        c.setTime(prelimDate);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);

        return c.getTime();
    }

    public String get_date_string() {
        Calendar c = Calendar.getInstance();
        c.setTime(eventDate);
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

    @Override
    public int compareTo(CountdownObject o) {
        if (o.get_date().after(this.get_date())) {
            return -1;
        } else if (o.get_date().equals(this.get_date())) {
            return o.get_name().compareTo(this.get_name());
        } else {
            return 1;
        }
    }
}