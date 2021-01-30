package com.qi.daysleftcountdown;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SettingsActivity extends AppCompatActivity {

    public static final String PREFERENCE_DISPLAY_CODE = "PREFERENCE_DISPLAY_CODE";
    public static final String PREFERENCE_COLOR_CODE = "PREFERENCE_COLOR_CODE";
    private SharedPreferences sp;
    private Spinner dateSpinner;
    private Spinner colorSpinner;
    public static final String[] displayModes = new String[]{"Year/Month/Day", "Day Only"};
    public static final String[] colorSchemes= new String[]{"Light","Night"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        int currentDisplay  = sp.getInt(PREFERENCE_DISPLAY_CODE,0);
        int currentColor  = sp.getInt(PREFERENCE_COLOR_CODE,0);

        dateSpinner = findViewById(R.id.spinner_settings);
        colorSpinner = findViewById(R.id.colorscheme_spinner_settings);

        ArrayAdapter<String> dateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, displayModes);
        ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, colorSchemes);
        dateSpinner.setAdapter(dateAdapter);
        colorSpinner.setAdapter(colorAdapter);

        dateSpinner.setSelection(currentDisplay);
        colorSpinner.setSelection(currentColor);
    }

    /**
     * Updates the date display and color settings and saves it to Shared Preferences.
     */
    public void applySettings(View view) {
        Intent replyIntent = new Intent();
        sp.edit().putInt(PREFERENCE_DISPLAY_CODE, dateSpinner.getSelectedItemPosition()).apply();
        sp.edit().putInt(PREFERENCE_COLOR_CODE, colorSpinner.getSelectedItemPosition()).apply();
        setResult(RESULT_OK, replyIntent);
        finish();
    }


    /**
     * Inflates menu that goes to Past Events.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * On Click listener for items in menu.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_past_events) {
            Intent intent = new Intent(this, PastEventsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}