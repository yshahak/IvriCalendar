package il.co.yshahak.ivricalendar.calendar.google;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yshahak on 14/10/2016.
 */

public class EventInstance implements Comparable<EventInstance> {

    private long eventId;
    private String eventTitle;
    private boolean allDayEvent;
    private long begin, end;
    private int displayColor;
    private String calendarDisplayName;
    private int dayOfMonth;
    private Date beginDate, endDate;

    public EventInstance(long eventId, String eventTitle, boolean allDayEvent, long begin, long end, int displayColor, String calendarDisplayName) {
        this.eventId = eventId;
        this.eventTitle = (eventTitle != null && eventTitle.length() > 0) ? eventTitle : "(ללא כותרת)";
        this.allDayEvent = allDayEvent;
        this.begin = begin;
        this.end = end;
        this.displayColor = displayColor;
        this.calendarDisplayName = calendarDisplayName;
    }

    public EventInstance(long eventId, String eventTitle, boolean allDayEvent, long begin, long end, int displayColor, String calendarDisplayName, int dayOfMonth) {
        this.eventId = eventId;
        this.eventTitle = (eventTitle != null && eventTitle.length() > 0) ? eventTitle : "(ללא כותרת)";
        this.allDayEvent = allDayEvent;
        this.begin = begin;
        this.end = end;
        this.displayColor = displayColor;
        this.calendarDisplayName = calendarDisplayName;
        this.dayOfMonth = dayOfMonth;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public long getEventId() {
        return eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public boolean isAllDayEvent() {
        return allDayEvent;
    }

    public long getBegin() {
        return begin;
    }

    public long getEnd() {
        return end;
    }

    public int getDisplayColor() {
        return displayColor;
    }

    public String getCalendarDisplayName() {
        return calendarDisplayName;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.US);

    @Override
    public String toString() {
        String result = String.format(Locale.US, "\neventId=%d" +
                "\neventTitle=%s" +
                "\nbegin=%s" +
                "\nend=%s",
                eventId,eventTitle, simpleDateFormat.format(begin), simpleDateFormat.format(end));
        return result;
    }

    @Override
    public int compareTo(@NonNull EventInstance instance) {
        return (int) (this.begin - instance.begin);
    }

    public enum Repeat{
        SINGLE,
        DAY,
        WEEK,
        MONTH,
        YEAR
    }
}
