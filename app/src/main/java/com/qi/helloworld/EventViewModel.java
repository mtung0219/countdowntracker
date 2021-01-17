package com.qi.helloworld;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class EventViewModel extends AndroidViewModel {
    private EventRepository mRepository;

    private LiveData<List<Event>> mAllEvents;
    private LiveData<List<Event>> mPastEvents;

    public EventViewModel (Application application) {
        super(application);
        mRepository = new EventRepository(application);
        mAllEvents = mRepository.getAllEvents();
        mPastEvents = mRepository.getPastEvents();
    }


    LiveData<List<Event>> getAllEvents() {
        return mAllEvents;
    }

    LiveData<List<Event>> getPastEvents() {
        return mPastEvents;
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
