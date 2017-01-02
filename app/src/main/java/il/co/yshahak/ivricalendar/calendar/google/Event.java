package il.co.yshahak.ivricalendar.calendar.google;

import java.util.Date;

/**
 * Created by yshahak on 14/10/2016.
 */

public class Event {

    private int eventId;
    private String eventTitle;
    private boolean allDayEvent;
    private long begin, end;
    private int displayColor;
    private String calendarDisplayName;
    private int dayOfMonth;
    private Date beginDate, endDate;

    public Event(int eventId, String eventTitle, boolean allDayEvent, long begin, long end, int displayColor, String calendarDisplayName) {
        this.eventId = eventId;
        this.eventTitle = (eventTitle != null && eventTitle.length() > 0) ? eventTitle : "(ללא כותרת)";
        this.allDayEvent = allDayEvent;
        this.begin = begin;
        this.end = end;
        this.displayColor = displayColor;
        this.calendarDisplayName = calendarDisplayName;
    }

    public Event(int eventId, String eventTitle, boolean allDayEvent, long begin, long end, int displayColor, String calendarDisplayName, int dayOfMonth) {
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

    public int getEventId() {
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
}
