package com.qi.helloworld;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;


public class AddEventActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY =
            "com.example.android.twoactivities.extra.REPLY";
    public static final String EVENT_NAME_KEY = "event name";
    public static final String YEAR_KEY = "year";
    public static final String MONTH_KEY = "month";
    public static final String DAY_KEY = "day";

    private int year = -1;
    private int month = -1;
    private int day = -1;
    private String eventName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
    }


    public void showDatePicker(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(),"datePicker");
    }

    public void processDatePickerResult(int year, int month, int day) {
        String month_string = Integer.toString(month + 1);
        String day_string = Integer.toString(day);
        String year_string = Integer.toString(year);
        this.year = year;
        this.month = month + 1;
        this.day = day;
        String dateMessage = (month_string +
                "/" + day_string +
                "/" + year_string);

        Date pickedDate = getDate(this.year, this.month, this.day);

        if (getDaysLeft(pickedDate) <= 0) {
            Toast.makeText(this, "Must pick a date in the future!",
                    Toast.LENGTH_LONG).show();
            this.year = -1;
            this.month = -1;
            this.day = -1;
        } else {
            Toast.makeText(this, dateMessage,
                    Toast.LENGTH_SHORT).show();
        }
    }

    public Date getDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, day);
        return cal.getTime();
    }
    public int getDaysLeft(Date d) {
        Calendar now = Calendar.getInstance();
        Calendar later = Calendar.getInstance();
        //now.setTimeInMillis(getNowInMillis());
        now.setTime(getNow());
        later.setTime(d);
        return daysBetween(now, later);
    }

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

    public void AddToMasterList(View view) {
        // Get the reply message from the edit text.
        EditText eT = (EditText) findViewById(R.id.name_text);
        eventName = eT.getText().toString();

        if (!everythingValid()) {
            Toast.makeText(this, "Fill in missing info first!",
                    Toast.LENGTH_LONG).show();
            return;
        }
        // Create a new intent for the reply, add the reply message to it
        // as an extra, set the intent result, and close the activity.
        Intent replyIntent = new Intent();
        replyIntent.putExtra(EVENT_NAME_KEY, eventName);
        replyIntent.putExtra(YEAR_KEY, year);
        replyIntent.putExtra(MONTH_KEY, month);
        replyIntent.putExtra(DAY_KEY,day);
        setResult(RESULT_OK, replyIntent);
        finish();
    }

    private boolean everythingValid() {
        if (this.year < 0 || this.month < 0 || this.day < 0 || eventName.equals("")) return false;
        return true;
    }
}