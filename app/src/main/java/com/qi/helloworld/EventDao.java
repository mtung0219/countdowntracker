package com.qi.helloworld;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverters;

import java.util.List;

@Dao
@TypeConverters(Converters.class)
public interface EventDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Event event);

    @Query("DELETE FROM event_table")
    void deleteAll();

    @Query("SELECT * from event_table ORDER BY date ASC")
    LiveData<List<Event>> getAllEvents();

    @Query("SELECT * from event_table WHERE date < (strftime('%s','now','start of day')*1000)  ORDER BY date ASC")
    LiveData<List<Event>> getPastEvents();
//    @Query("SELECT * from event_table WHERE date < :cmp ORDER BY date ASC")

    @Query("SELECT * from event_table  ORDER BY date ASC")
    LiveData<List<Event>> getCurrentEvents();

    @Query("SELECT * from event_table LIMIT 1")
    Event[] getAnyEvent();

    @Delete
    void deleteEvent(Event event);
}