package il.co.yshahak.ivricalendar.calendar.jewish;

import net.sourceforge.zmanim.hebrewcalendar.JewishCalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import il.co.yshahak.ivricalendar.calendar.google.EventInstance;

import static il.co.yshahak.ivricalendar.calendar.jewish.Month.hebrewDateFormatter;

/**
 * Created by yshahak on 09/10/2016.
 */

public class Day {

    private static Calendar calendar = Calendar.getInstance();

    private List<EventInstance> googleEventInstances = new ArrayList<>();
//    private String month;
    private final String label;
    private long startDayInMillis, endDayInMillis;
    private int dayInMonth;
    private boolean isOutOfMonthRange;

    public Day(JewishCalendar jewishCalendar) {
//        this.month = hebrewDateFormatter.formatMonth(jewishCalendar);
        this.dayInMonth = jewishCalendar.getJewishDayOfMonth();
        this.label = hebrewDateFormatter.formatHebrewNumber(dayInMonth);
        setBeginAndEnd(jewishCalendar);
    }


    public List<EventInstance> getGoogleEventInstances() {
        return googleEventInstances;
    }

    public void setGoogleEventInstances(List<EventInstance> googleEventInstances) {
        this.googleEventInstances = googleEventInstances;
    }

//    public String getMonth() {
//        return month;
//    }

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

    public void setOutOfMonthRange(boolean outOfMonthRange) {
        isOutOfMonthRange = outOfMonthRange;
    }

    public boolean isOutOfMonthRange() {
        return isOutOfMonthRange;
    }

    public void setBeginAndEnd(JewishCalendar jewishCalendar) {
        calendar.set(jewishCalendar.getGregorianYear(), jewishCalendar.getGregorianMonth(), jewishCalendar.getGregorianDayOfMonth());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        this.startDayInMillis = calendar.getTimeInMillis();
        this.endDayInMillis = startDayInMillis + TimeUnit.DAYS.toMillis(1);
    }

    public void setBeginAndEnd(Day day) {
        this.startDayInMillis = day.endDayInMillis;
        this.endDayInMillis = startDayInMillis + TimeUnit.DAYS.toMillis(1);
    }
}
