package com.qi.helloworld;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;

public class PastEventsActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE
            = "com.qi.helloworld.extra.MESSAGE";
    // Unique tag for the intent reply
    public static final int TEXT_REQUEST = 1;
    private int mCount = 0;
    private RecyclerView mRecyclerView;
    private WordListAdapter mAdapter;
    private EventViewModel mEventViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_events);

        mRecyclerView = findViewById(R.id.recyclerview_past);
        mAdapter = new WordListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mEventViewModel = ViewModelProviders.of(this).get(EventViewModel.class);

        mEventViewModel.getPastEvents().observe(this, new Observer<List<Event>>() {
            @Override
            public void onChanged(@Nullable final List<Event> events) {
                Log.d("asdf","past events has " + events.size() + " items");
                mAdapter.setEvents(events);
            }
        });

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
                        int position = viewHolder.getAdapterPosition();
                        Event myEvent = mAdapter.getEventAtPosition(position);
                        Toast.makeText(PastEventsActivity.this, "Deleting " +
                                myEvent.getName(), Toast.LENGTH_LONG).show();

                        // Delete the event
                        mEventViewModel.deleteEvent(myEvent);
                    }
                });

        helper.attachToRecyclerView(mRecyclerView);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public Date getDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, day);
        return cal.getTime();
    }

}