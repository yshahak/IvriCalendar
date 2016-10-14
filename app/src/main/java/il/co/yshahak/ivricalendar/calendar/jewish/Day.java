package il.co.yshahak.ivricalendar.calendar.jewish;

import java.util.ArrayList;

import il.co.yshahak.ivricalendar.calendar.google.Event;

/**
 * Created by yshahak on 09/10/2016.
 */

public class Day {

    private ArrayList<Event> googleEvents = new ArrayList<>();
    private final String label;
    private long startDayInMillis, endDayInMillis;


    public Day(ArrayList<Event> googleEvents, String label) {
        this.googleEvents = googleEvents;
        this.label = label;
    }

    public Day(String label, long[] beginAndEnd) {
        this.label = label;
        this.startDayInMillis = beginAndEnd[0];
        this.endDayInMillis = beginAndEnd[1];
//        Log.i("Day", "Event:  " + label + " , start: " + startDayInMillis + " ,end " + endDayInMillis );
    }

    public ArrayList<Event> getGoogleEvents() {
        return googleEvents;
    }

    public String getLabel() {
        return label;
    }

    public long getStartDayInMillis() {
        return startDayInMillis;
    }

    public long getEndDayInMillis() {
        return endDayInMillis;
    }
}
