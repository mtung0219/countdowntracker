package com.qi.helloworld;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class AddEventActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY =
            "com.example.android.twoactivities.extra.REPLY";
    public static final String EVENT_NAME_KEY = "event name";
    public static final String YEAR_KEY = "year";
    public static final String MONTH_KEY = "month";
    public static final String DAY_KEY = "day";

    private int year;
    private int month;
    private int day;
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

        Toast.makeText(this, dateMessage,
                Toast.LENGTH_SHORT).show();
    }

    public void AddToMasterList(View view) {
        // Get the reply message from the edit text.
        EditText eT = (EditText) findViewById(R.id.name_text);
        eventName = eT.getText().toString();
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
}