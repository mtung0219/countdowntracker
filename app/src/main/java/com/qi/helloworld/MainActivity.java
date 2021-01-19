package com.qi.helloworld;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Calendar;
import java.util.Date;

import java.util.List;
import java.util.TimeZone;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.qi.helloworld.extra.MESSAGE";
    // Unique tag for the intent reply

    public static final int TEXT_REQUEST = 1;
    private int mCount = 0;
    private RecyclerView mRecyclerView;
    private WordListAdapter mAdapter;
    private EventViewModel mEventViewModel;
    private List<Event> finalEvents;
    private Context ctx;

    private NotificationManager mNotificationManager;
    private static final int NOTIFICATION_ID = 0;
    private static final String PRIMARY_CHANNEL_ID =
            "primary_notification_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ctx = this;
        mRecyclerView = findViewById(R.id.recyclerview);
        mAdapter = new WordListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mEventViewModel = ViewModelProviders.of(this).get(EventViewModel.class);

        mEventViewModel.getCurrentEvents().observe(this, new Observer<List<Event>>() {
            @Override
            public void onChanged(@Nullable final List<Event> events) {
                Log.d("asdf","current events has " + events.size() + " items");
                // Update the cached copy of the words in the adapter.
                finalEvents = events;
                mAdapter.setEvents(events);

                Context ctx = getApplicationContext();
                AppWidgetManager mgr = AppWidgetManager.getInstance(ctx);
                sendRefreshBroadcast(ctx);
            }
        });

        //Context ctx = getApplicationContext();
        //AppWidgetManager mgr = AppWidgetManager.getInstance(ctx);
        //sendRefreshBroadcast(ctx);

        // Add the functionality to swipe items in the
        // recycler view to delete that item
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
                                                myEvent.getName(), Toast.LENGTH_LONG).show();

                                        // Delete the event
                                        mEventViewModel.deleteEvent(myEvent);
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        mAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                                    }
                                }) .show();
                    }
                });

        helper.attachToRecyclerView(mRecyclerView);

        //*********************************notification stuff************************************

        NotificationCompat.Builder asdf1 = new NotificationCompat.Builder(this, "asdf");



        mNotificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        ToggleButton alarmToggle = findViewById(R.id.alarmToggle);

        Intent notifyIntent = new Intent(this, AlarmReceiver.class);

        boolean alarmUp = (PendingIntent.getBroadcast(this, NOTIFICATION_ID,
                notifyIntent, PendingIntent.FLAG_NO_CREATE) != null);
        alarmToggle.setChecked(alarmUp);

        final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
                (this, NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmToggle.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton,
                                                 boolean isChecked) {
                        String toastMessage;
                        if(isChecked){
                            long repeatInterval = 1 * 10 * 1000;
                            //long repeatInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
                            long triggerTime = SystemClock.elapsedRealtime()
                                    + repeatInterval;
                            toastMessage = "Stand Up Alarm On!";
                            if (alarmManager != null) {
                                alarmManager.set
                                        (AlarmManager.RTC_WAKEUP,
                                                getTestLocalDateLong(), notifyPendingIntent);
                            }
                        } else {
                            if (alarmManager != null) {
                                alarmManager.cancel(notifyPendingIntent);
                            }
                            mNotificationManager.cancelAll();
                            toastMessage = "Stand Up Alarm Off!";
                        }
                        Toast.makeText(MainActivity.this, toastMessage,Toast.LENGTH_SHORT)
                                .show();
                    }
                });

        createNotificationChannel();
    }

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
        c.set(Calendar.HOUR_OF_DAY,23);
        c.set(Calendar.MINUTE, 28);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime().getTime();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == TEXT_REQUEST && resultCode == RESULT_OK) {
            String reply_event_name = data.getStringExtra(AddEventActivity.EVENT_NAME_KEY);
            int reply_year= data.getIntExtra(AddEventActivity.YEAR_KEY,0);
            int reply_month= data.getIntExtra(AddEventActivity.MONTH_KEY,0);
            int reply_day= data.getIntExtra(AddEventActivity.DAY_KEY,0);
            Log.d("ASDF","original reply ints are " + reply_year + " " + reply_month + " " + reply_day);
            Date date = getDate(reply_year, reply_month, reply_day);


            Event event = new Event(reply_event_name, date);
            Log.d("ASDF","original saved date is " + date.getTime());
            mEventViewModel.insert(event);
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "Something went wrong.",
                    Toast.LENGTH_LONG).show();
        }
    }

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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_past_events) {
            // Add a toast just for confirmation
            Toast.makeText(this, "Navigating to past events...",
                    Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, PastEventsActivity.class);
            //String message = mMessageEditText.getText().toString();
            //intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

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

    public void countUp(View view) {
        Intent intent = new Intent(this, AddEventActivity.class);
        //String message = mMessageEditText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivityForResult(intent, TEXT_REQUEST);

        //Intent intent2 = new Intent(this, RoomWordsSample.class);
        //startActivity(intent2);
    }
}