# Countdown Tracker App

This is the complete code for the Android app, "Days Left Countdown - App and Widget" (currently in internal testing, awaiting Google approval). This app allows users to keep track of their upcoming events in a simple, minimalist format.
Users provide an event name and date, and that event is added onto a running list. This data is stored locally via Android's Room SQLite database service.

Event history is stored in a separate view, and events can be deleted at any time.

A widget is available to place on the home screen, and is automatically updated any time a change is made (an event is added or removed), and at midnight system-time each day.

Time can be viewed as pure days (for example "1353 days left"), or in year/month/day format ("3y 5mo 12d"). Day/night modes are available within the app.
