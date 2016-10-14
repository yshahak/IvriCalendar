package il.co.yshahak.ivricalendar.calendar.google;

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

    public Event(int eventId, String eventTitle, boolean allDayEvent, long begin, long end, int displayColor, String calendarDisplayName) {
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.allDayEvent = allDayEvent;
        this.begin = begin;
        this.end = end;
        this.displayColor = displayColor;
        this.calendarDisplayName = calendarDisplayName;
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
}
