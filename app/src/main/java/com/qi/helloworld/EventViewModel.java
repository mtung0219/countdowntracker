package com.qi.helloworld;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * ViewModel for Event Repository.
 */
public class EventViewModel extends AndroidViewModel {
    private EventRepository mRepository;

    private LiveData<List<Event>> mAllEvents;
    private LiveData<List<Event>> mPastEvents;
    private LiveData<List<Event>> mCurrentEvents;

    public EventViewModel (Application application) {
        super(application);
        mRepository = new EventRepository(application);
        mAllEvents = mRepository.getAllEvents();
        mPastEvents = mRepository.getPastEvents();
        mCurrentEvents = mRepository.getCurrentEvents();
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
        mRepository.insert(event);
    }

    public void deleteAll() {
        mRepository.deleteAll();
    }

    public void deleteEvent(Event event) {
        mRepository.deleteEvent(event);
    }
}
