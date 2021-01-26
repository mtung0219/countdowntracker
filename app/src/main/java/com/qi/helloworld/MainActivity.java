package com.qi.helloworld;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.Calendar;
import java.util.Date;

import java.util.List;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.qi.helloworld.extra.MESSAGE";
    // Unique tag for the intent reply

    public static final int ADD_EVENT_REQUEST = 1;
    public static final int SETTINGS_REQUEST = 2;
    private int mCount = 0;
    private RecyclerView mRecyclerView;
    private WordListAdapter mAdapter;
    private EventViewModel mEventViewModel;
    private List<Event> finalEvents;
    private Context ctx;

    private NotificationManager mNotificationManager;
    private AlarmManager alarmManager;
    private static final int NOTIFICATION_ID = 0;
    private static final String PRIMARY_CHANNEL_ID =
            "primary_notification_channel";

    private static final String PREFERENCE_LAST_NOTIF_ID = "PREFERENCE_LAST_NOTIF_ID";
    private static final String SHARED_PREFS_FILE = "SHARED_PREFS_FILE";

    private SharedPreferences sp;
    private long mLastClickTime = 0;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        int currentDisplay  = sp.getInt(SettingsActivity.PREFERENCE_DISPLAY_CODE,0);
        int currentColor  = sp.getInt(SettingsActivity.PREFERENCE_COLOR_CODE,0);
        super.onCreate(savedInstanceState);
        if (currentColor == 0) {
            Log.d("ASDF","NIGHT MODE NO");
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            Log.d("ASDF","NIGHT MODE YES");
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        ctx = this;
        mEventViewModel = ViewModelProviders.of(this).get(EventViewModel.class);

        mRecyclerView = findViewById(R.id.recyclerview);
        mAdapter = new WordListAdapter(this, "current");
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        mEventViewModel.getCurrentEvents().observe(this, new Observer<List<Event>>() {
            @Override
            public void onChanged(@Nullable final List<Event> events) {
                // Update the cached copy of the words in the adapter.
                finalEvents = events;
                mAdapter.setEvents(events);

                // save the task list to preference
                SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                Gson gson = new Gson();
                String jsonText = gson.toJson(finalEvents);
                editor.putString("eventList", jsonText);
                editor.apply();
                Log.d("Loading", "event list set via gson");

                /*try {
                    FileOutputStream fileOutputStream = new FileOutputStream("Events.ser");
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                    objectOutputStream.writeObject(finalEvents);
                    objectOutputStream.close();
                    fileOutputStream.close();
                    Log.d("Loading", "event list serialized");
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

                Context ctx = getApplicationContext();
                sendRefreshBroadcast(ctx);
            }
        });

        // swipe recyclerview items to delete them
        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder,
                                         int direction) {

                        new AlertDialog.Builder(MainActivity.this)
                                .setMessage("Are you sure you want to delete this event?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        int position = viewHolder.getAdapterPosition();
                                        Event myEvent = mAdapter.getEventAtPosition(position);
                                        Toast.makeText(MainActivity.this, "Deleting " +
                                                myEvent.getName(), Toast.LENGTH_SHORT).show();

                                        // Delete the event and notification
                                        int deleteID = myEvent.getNotifID();
                                        String deleteName = myEvent.getName();
                                        mEventViewModel.deleteEvent(myEvent);
                                        Intent notifyIntent = new Intent(MainActivity.this, AlarmReceiver.class);
                                        notifyIntent.putExtra("eventName", deleteName);
                                        notifyIntent.putExtra("type","oneDay");
                                        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        notifyIntent.setAction(deleteID+"");
                                        final PendingIntent alarmIntentCancel = PendingIntent.getBroadcast (
                                                MainActivity.this, deleteID, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                        alarmManager.cancel(alarmIntentCancel); //cancels 1-day alarm

                                        Intent notifyIntent2 = new Intent(MainActivity.this, AlarmReceiver.class);
                                        notifyIntent2.putExtra("eventName", deleteName);
                                        notifyIntent.putExtra("type","dayOf");
                                        notifyIntent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        notifyIntent2.setAction((deleteID+1)+"");
                                        final PendingIntent alarmIntentCancel2 = PendingIntent.getBroadcast (
                                                MainActivity.this, deleteID+1, notifyIntent2, PendingIntent.FLAG_UPDATE_CURRENT);
                                        alarmManager.cancel(alarmIntentCancel2); //cancels day-of alarm

                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        mAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                                    }
                                }) .show();
                    }
                });

        findViewById(R.id.button_count).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // prevent double clicks, using threshold time of 1 second
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                countUp(v);
            }
        });

        helper.attachToRecyclerView(mRecyclerView);
        createNotificationChannel();
    }

    /**
     * Schedule a notification when an event is created.
     */
    private void sendNotification(String eventName, Date d, int xId, String notificationType) {
        Intent notifyIntent = new Intent(this, AlarmReceiver.class);
        notifyIntent.putExtra("eventName", eventName);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notifyIntent.setAction(xId+"");
        notifyIntent.putExtra("type",notificationType);
        Log.d("asdf","adding this extra name : " + eventName + " with notif ID " + xId);
        final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
                (this, xId, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, d.getTime(), notifyPendingIntent);
        }
    }

    /**
     * Generates unique notification IDs to make sure intents are not reused.
     * Increments by 2 to accommodate the two notifications created.
     */
    private static int getNextNotifId(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int id = sharedPreferences.getInt(PREFERENCE_LAST_NOTIF_ID, 0) + 2;
        if (id == Integer.MAX_VALUE) { id = 0; }
        sharedPreferences.edit().putInt(PREFERENCE_LAST_NOTIF_ID, id).apply();
        return id;
    }

    /**
     * Sends a broadcast to refresh the widget. Data sent via intent extras.
     */
    public void sendRefreshBroadcast(Context context) {
        Log.d("Loading","sending an updated broadcast...............");
        String[] testStringArray = new String[finalEvents.size()];
        long[] testLongArray = new long[finalEvents.size()];
        for (int i = 0; i < finalEvents.size(); i++) {
            testStringArray[i]=(finalEvents.get(i).getName());
            testLongArray[i] = (finalEvents.get(i).getDateLong());
        }
        String teststring = finalEvents.get(0).getName();

        int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(context, CountdownTrackerWidget.class));

        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.setComponent(new ComponentName(context, CountdownTrackerWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        intent.putExtra("doesthiswork",teststring);
        intent.putExtra("doesthisworkarray",testStringArray);
        intent.putExtra("dateLongArray",testLongArray);
        context.sendBroadcast(intent);
    }

    /**
     * Sets a specific date for testing purposes.
     */
    private long getTestLocalDateLong() {
        Calendar c = Calendar.getInstance();
        Date prelimDate = c.getTime();

        c.setTime(prelimDate);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY,2);
        c.set(Calendar.MINUTE, 7);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime().getTime();
    }

    /**
     * When new event is logged by user, sends notifications and insert event into ViewModel.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_EVENT_REQUEST && resultCode == RESULT_OK) {
            String reply_event_name = data.getStringExtra(AddEventActivity.EVENT_NAME_KEY);
            int reply_year= data.getIntExtra(AddEventActivity.YEAR_KEY,0);
            int reply_month= data.getIntExtra(AddEventActivity.MONTH_KEY,0);
            int reply_day= data.getIntExtra(AddEventActivity.DAY_KEY,0);
            Log.d("ASDF","original reply ints are " + reply_year + " " + reply_month + " " + reply_day);
            Date date = getDate(reply_year, reply_month, reply_day);
            int xId = getNextNotifId(this);
            int xId2;
            if (xId == Integer.MAX_VALUE) { xId2 = 0; } else {xId2 = xId + 1; }

            Event event = new Event(reply_event_name, date,xId);
            Log.d("ASDF","original saved date is " + date.getTime());
            mEventViewModel.insert(event);

            int daysLeft = event.getDaysLeft();

            // only send one-day notif if event date is not today
            if (daysLeft > 0) {
                Date notifDate_oneBefore = getNotifDateOneDayLeft(reply_year, reply_month, reply_day);
                sendNotification(reply_event_name, notifDate_oneBefore,xId, "oneDay");
            }

            Date notifDate_dayOf = getNotifDateDayOf(reply_year, reply_month, reply_day);
            sendNotification(reply_event_name, notifDate_dayOf,xId2, "dayOf");

        } else if (requestCode == SETTINGS_REQUEST && resultCode == RESULT_OK){
            mAdapter.refreshEvents();
            /*int currentDisplay  = sp.getInt(SettingsActivity.PREFERENCE_DISPLAY_CODE,0);
            int currentColor  = sp.getInt(SettingsActivity.PREFERENCE_COLOR_CODE,0);
            if (currentColor == 0) {
                getTheme().applyStyle(R.style.ThemeDay_HelloWorld, true);
                //setTheme(R.style.ThemeDay_HelloWorld);
            } else {
                getTheme().applyStyle(R.style.Theme_HelloWorld, true);
                //setTheme(R.style.Theme_HelloWorld);
            }*/
            reload();
            //Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Reload this activity upon theme change.
     */
    public void reload() {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }


    /**
     * Creates notification channel for SDKs OREO and up.
     */
    public void createNotificationChannel() {

        // Create a notification manager object.
        mNotificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {

            // Create the NotificationChannel with all the parameters.
            NotificationChannel notificationChannel = new NotificationChannel
                    (PRIMARY_CHANNEL_ID,
                            "Stand up notification",
                            NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription
                    ("Notifies every 15 minutes to stand up and walk");
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
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
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, SETTINGS_REQUEST);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method used to set Date object for events. Time of day is set to 11:59pm system time
     * so that "Days Left" will not incremented until the very end of the day.
     */
    public Date getDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY,23);
        cal.set(Calendar.MINUTE,59);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);

        Log.d("ASDF","time set is " + cal.getTime().getTime());
        return cal.getTime();
    }

    /**
     * Gets date one day before event due at 7:30am, for notification purposes.
     */
    public Date getNotifDateOneDayLeft(int year, int month, int day) {
        // notifies at 7:30am the day before
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY,7);
        cal.set(Calendar.MINUTE,30);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);

        cal.add(Calendar.DAY_OF_YEAR, -1);

        return cal.getTime();
    }

    /**
     * Gets date day of event at 7:00am, for notification purposes.
     */
    public Date getNotifDateDayOf(int year, int month, int day) {
        // notifies at 7am day of
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY,7);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);

        return cal.getTime();
    }

    /**
     * Go to Add Event page.
     */
    public void countUp(View view) {
        Intent intent = new Intent(this, AddEventActivity.class);
        //String message = mMessageEditText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivityForResult(intent, ADD_EVENT_REQUEST);

        //Intent intent2 = new Intent(this, RoomWordsSample.class);
        //startActivity(intent2);
    }
}