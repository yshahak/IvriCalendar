package il.co.yshahak.ivricalendar.calendar.jewish;

import java.util.ArrayList;

import il.co.yshahak.ivricalendar.calendar.google.Event;

/**
 * Created by yshahak on 09/10/2016.
 */

public class Day {

    private ArrayList<Event> googleEvents = new ArrayList<>();
    private String month;
    private final String label;
    private long startDayInMillis, endDayInMillis;
    private int dayInMonth;


    public Day(String month, String label, long[] beginAndEnd, int dayInMonth) {
        this.month = month;
        this.label = label;
        if (beginAndEnd != null) {
            this.startDayInMillis = beginAndEnd[0];
            this.endDayInMillis = beginAndEnd[1];
        }
        this.dayInMonth = dayInMonth;
    }

    public ArrayList<Event> getGoogleEvents() {
        return googleEvents;
    }

    public String getMonth() {
        return month;
    }

    public String getLabel() {
        return label;
    }

    public long getStartDayInMillis() {
        return startDayInMillis;
    }

    public int getDayInMonth() {
        return dayInMonth;
    }

    public long getEndDayInMillis() {
        return endDayInMillis;
    }
}
