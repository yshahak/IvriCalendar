package il.co.yshahak.ivricalendar.calendar.jewish;

import net.sourceforge.zmanim.hebrewcalendar.JewishCalendar;

import java.util.ArrayList;
import java.util.Calendar;

import il.co.yshahak.ivricalendar.calendar.google.Event;

import static il.co.yshahak.ivricalendar.calendar.jewish.Month.hebrewDateFormatter;

/**
 * Created by yshahak on 09/10/2016.
 */

public class Day {

    private ArrayList<Event> googleEvents = new ArrayList<>();
    private String month;
    private final String label;
    private long startDayInMillis, endDayInMillis;
    private int dayInMonth;

    public Day(JewishCalendar jewishCalendar) {
        this.month = hebrewDateFormatter.formatMonth(jewishCalendar);
        this.label = hebrewDateFormatter.formatHebrewNumber(jewishCalendar.getJewishDayOfMonth());
        long[] beginAndEnd = getBeginAndEnd(jewishCalendar);
        this.startDayInMillis = beginAndEnd[0];
        this.endDayInMillis = beginAndEnd[1];
        this.dayInMonth = jewishCalendar.getJewishDayOfMonth();
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


    private long[] getBeginAndEnd(JewishCalendar jewishCalendar) {
        long[] longs = new long[2];
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(jewishCalendar.getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        longs[0] = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        longs[1] = calendar.getTimeInMillis();
        return longs;
    }
}
