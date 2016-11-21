package il.co.yshahak.ivricalendar.calendar.google;

/**
 * Created by yshah on 11/21/2016.
 */

public class CalendarAccount {

    private int accountId;
    private int calendarColor;
    private String calendarDisplayName;
    private String calendarName;
    private String calendarOwnerName;
    private boolean calendarIsVisible;

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public void setCalendarDisplayName(String calendarDisplayName) {
        this.calendarDisplayName = calendarDisplayName;
    }

    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }

    public void setCalendarOwnerName(String calendarOwnerName) {
        this.calendarOwnerName = calendarOwnerName;
    }

    public void setCalendarColor(int calendarColor) {
        this.calendarColor = calendarColor;
    }

    public void setCalendarIsVisible(boolean calendarIsVisible) {
        this.calendarIsVisible = calendarIsVisible;
    }

    public int getAccountId() {
        return accountId;
    }

    public String getCalendarDisplayName() {
        return calendarDisplayName;
    }

    public String getCalendarName() {
        return calendarName;
    }

    public String getCalendarOwnerName() {
        return calendarOwnerName;
    }

    public int getCalendarColor() {
        return calendarColor;
    }

    public boolean isCalendarIsVisible() {
        return calendarIsVisible;
    }
}
