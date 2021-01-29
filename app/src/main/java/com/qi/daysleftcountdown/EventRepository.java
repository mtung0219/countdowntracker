package com.qi.daysleftcountdown;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Offers convenient methods to access and modify eventDao data.
 * Shields ViewModel from database changes.
 */
public class EventRepository {private EventDao mEventDao;
    private LiveData<List<Event>> mAllEvents;
    private LiveData<List<Event>> mPastEvents;
    private LiveData<List<Event>> mCurrentEvents;

    EventRepository(Application application) {
        EventRoomDatabase db = EventRoomDatabase.getDatabase(application);
        mEventDao = db.eventDao();
        Calendar c = Calendar.getInstance();
        long prelimDate = c.getTime().getTime();
        mAllEvents = mEventDao.getAllEvents();
        mPastEvents = mEventDao.getPastEvents(getStartLocalDateLong());
        mCurrentEvents = mEventDao.getCurrentEvents(getStartLocalDateLong());
    }

    LiveData<List<Event>> getAllEvents() {
        return mAllEvents;
    }
    LiveData<List<Event>> getPastEvents() {
        return mPastEvents;
    }
    LiveData<List<Event>> getCurrentEvents() {
        return mCurrentEvents;
    }

    public void insert(Event event) {
        new insertAsyncTask(mEventDao).execute(event);
    }
    public void deleteAll()  {
        new deleteAllWordsAsyncTask(mEventDao).execute();
    }

    public void deleteEvent(Event event)  {
        new deleteWordAsyncTask(mEventDao).execute(event);
    }

    /**
     * Returns start of current day to use as comparison cutoff for events' Days Left.
     */
    public long getStartLocalDateLong() {
        Calendar c = Calendar.getInstance();
        Date prelimDate = c.getTime();

        c.setTime(prelimDate);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        Log.d("asdf",c.getTime().getTime()+"");

        return c.getTime().getTime();
    }


    private static class insertAsyncTask extends AsyncTask<Event, Void, Void> {

        private EventDao mAsyncTaskDao;

        insertAsyncTask(EventDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Event... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class deleteAllWordsAsyncTask extends AsyncTask<Void, Void, Void> {
        private EventDao mAsyncTaskDao;

        deleteAllWordsAsyncTask(EventDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.deleteAll();
            return null;
        }
    }

    private static class deleteWordAsyncTask extends AsyncTask<Event, Void, Void> {
        private EventDao mAsyncTaskDao;

        deleteWordAsyncTask(EventDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Event... params) {
            mAsyncTaskDao.deleteEvent(params[0]);
            return null;
        }
    }
}