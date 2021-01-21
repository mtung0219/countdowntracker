package com.qi.helloworld;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.Calendar;
import java.util.Date;


@Database(entities = {Event.class}, version = 3, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class EventRoomDatabase extends RoomDatabase {

    public abstract EventDao eventDao();
    private static EventRoomDatabase INSTANCE;

    public static EventRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (EventRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            EventRoomDatabase.class, "event_database")
                            // Wipes and rebuilds instead of migrating
                            // if no Migration object.
                            // Migration is not part of this practical.
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback(){

                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase db){
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    /**
     * Populate the database in the background.
     */
    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final EventDao mDao;
        String[] eventNames = {"event five", "event three", "event one"};
        Date d1 = getDate(2021,10,5);
        Date d2 = getDate(2021,5,5);
        Date d3 = getDate(2021,3,5);

        Date[] eventDates = {d1,d2,d3};
        PopulateDbAsync(EventRoomDatabase db) {
            mDao = db.eventDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            // If we have no words, then create the initial list of words
            // ***** don't enable this
            /*if (mDao.getAnyEvent().length < 1) {
                for (int i = 0; i <= eventNames.length - 1; i++) {
                    Event event = new Event(eventNames[i], eventDates[i], 0);
                    mDao.insert(event);
                }
            }*/
            return null;
        }

        public Date getDate(int year, int month, int day) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, day);
            return cal.getTime();
        }
    }
}
