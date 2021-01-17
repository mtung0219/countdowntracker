package com.qi.helloworld;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EventRepository {private EventDao mEventDao;
    private LiveData<List<Event>> mAllEvents;
    private LiveData<List<Event>> mPastEvents;

    EventRepository(Application application) {
        EventRoomDatabase db = EventRoomDatabase.getDatabase(application);
        mEventDao = db.eventDao();
        //mAllEvents = mEventDao.getAllEvents();**********************************************
        Calendar c = Calendar.getInstance();
        long prelimDate = c.getTime().getTime();
        Log.d("ASDF", " " + prelimDate);
        mAllEvents = mEventDao.getAllEvents();
        //mPastEvents = mEventDao.getPastEvents(prelimDate);
        mPastEvents = mEventDao.getPastEvents();
    }

    LiveData<List<Event>> getAllEvents() {
        return mAllEvents;
    }
    LiveData<List<Event>> getPastEvents() {
        return mPastEvents;
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