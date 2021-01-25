package com.qi.helloworld;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * Adapter used for both MainActivity and Past Events views.
 */
public class WordListAdapter extends
        RecyclerView.Adapter<WordListAdapter.WordViewHolder> {

    private final LayoutInflater mInflater;
    private List<Event> mEvents; // Cached copy of events
    private String isCurrent;
    private SharedPreferences sp;
    private int currentDisplay;
    private int currentColor;

    public WordListAdapter(Context context, String isCurrent) {
        mInflater = LayoutInflater.from(context);
        this.isCurrent = isCurrent;

        sp = PreferenceManager.getDefaultSharedPreferences(context);
        this.currentDisplay = sp.getInt(SettingsActivity.PREFERENCE_DISPLAY_CODE,0);;
        this.currentColor = sp.getInt(SettingsActivity.PREFERENCE_COLOR_CODE,0);;
    }

    @NonNull
    @Override
    public WordListAdapter.WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate an item view.
        View mItemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new WordViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(WordListAdapter.WordViewHolder holder,
                                 int position) {
        // Retrieve the data for that position.
        Event event = mEvents.get(position);
        String daysLeft;
        int daysLeftInt = event.getDaysLeft();
        int[] ymd = event.getYearsMonthsDaysLeft();
        if (isCurrent.equals("past")) daysLeftInt = -daysLeftInt;

        if (SettingsActivity.displayModes[currentDisplay].equals("Detailed")) {
            daysLeft = ymd[0] + "y " + ymd[1] + "mo " + ymd[2] + "d "+ ymd[3] + "h " + ymd[4] + "min";
        } else {
            if (daysLeftInt == 0) { daysLeft = "today!"; }
            else { daysLeft = daysLeftInt + ""; }
        }

        String mCurrent = event.getName();
        holder.wordItemView.setText(mCurrent);
        holder.daysLeftItemView.setText(daysLeft);
        /*int[] colorSchemeUse = holder.colorSchemeMagenta;

        if (daysLeftInt < 1) { holder.cv.setCardBackgroundColor(colorSchemeUse[6]); }
        else if (daysLeftInt < 2) { holder.cv.setCardBackgroundColor(colorSchemeUse[5]); }
        else if (daysLeftInt < 3) { holder.cv.setCardBackgroundColor(colorSchemeUse[4]); }
        else if (daysLeftInt < 4) { holder.cv.setCardBackgroundColor(colorSchemeUse[3]); }
        else if (daysLeftInt < 5) { holder.cv.setCardBackgroundColor(colorSchemeUse[2]); }
        else if (daysLeftInt < 6) { holder.cv.setCardBackgroundColor(colorSchemeUse[1]); }
        else { holder.cv.setCardBackgroundColor(colorSchemeUse[0]); }*/
    }

    @Override
    public int getItemCount() {
        if (mEvents != null) return mEvents.size();
        else return 0;
    }

    public Event getEventAtPosition (int position) {
        return mEvents.get(position);
    }

    void setEvents(List<Event> events){
        mEvents = events;
        notifyDataSetChanged();
    }

    void refreshEvents() {
        currentDisplay = sp.getInt(SettingsActivity.PREFERENCE_DISPLAY_CODE,0);
        currentColor = sp.getInt(SettingsActivity.PREFERENCE_COLOR_CODE,0);
        Log.d("ASDF","NOTIFYING DATA SET CHANGED!");
        notifyDataSetChanged();
    }


    class WordViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        public final TextView wordItemView;
        public final TextView daysLeftItemView;
        public final CardView cv;
        public final int[] colorScheme;
        public final int[] colorSchemeMagenta;
        final WordListAdapter mAdapter;

        public WordViewHolder(View itemView, WordListAdapter adapter) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.word_textview);
            daysLeftItemView = itemView.findViewById(R.id.daysleft_textview);
            cv = itemView.findViewById(R.id.recycler_cardview);
            colorScheme = new int[7];
            //#fcde9c,#faa476,#f0746e,#e34f6f,#dc3977,#b9257a,#7c1d6f
            colorScheme[0] = Color.parseColor("#fcde9c");
            colorScheme[1] = Color.parseColor("#faa476");
            colorScheme[2] = Color.parseColor("#f0746e");
            colorScheme[3] = Color.parseColor("#e34f6f");
            colorScheme[4] = Color.parseColor("#dc3977");
            colorScheme[5] = Color.parseColor("#b9257a");
            colorScheme[6] = Color.parseColor("#7c1d6f");

            colorSchemeMagenta = new int[7];
            //#f3cbd3,#eaa9bd,#dd88ac,#ca699d,#b14d8e,#91357d,#6c2167
            colorSchemeMagenta[0] = Color.parseColor("#f3cbd3");
            colorSchemeMagenta[1] = Color.parseColor("#eaa9bd");
            colorSchemeMagenta[2] = Color.parseColor("#dd88ac");
            colorSchemeMagenta[3] = Color.parseColor("#ca699d");
            colorSchemeMagenta[4] = Color.parseColor("#b14d8e");
            colorSchemeMagenta[5] = Color.parseColor("#91357d");
            colorSchemeMagenta[6] = Color.parseColor("#6c2167");

            this.mAdapter = adapter;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            /*// Get the position of the item that was clicked.
            int mPosition = getLayoutPosition();

            // Use that to access the affected item in mWordList.
            String element = mWordList.get(mPosition);
            // Change the word in the mWordList.

            mWordList.set(mPosition, "Clicked! " + element);
            // Notify the adapter, that the data has changed so it can
            // update the RecyclerView to display the data.
            mAdapter.notifyDataSetChanged();*/
        }
    }

}