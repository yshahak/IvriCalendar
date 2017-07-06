package il.co.yshahak.ivricalendar.calendar;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import il.co.yshahak.ivricalendar.calendar.google.CalendarAccount;
import il.co.yshahak.ivricalendar.calendar.google.EventInstance;
import il.co.yshahak.ivricalendar.calendar.jewish.JewCalendar;

/**
 * Created by Yaakov Shahak
 * on 18/06/17.
 */

public interface EventsProvider {

    Uri addSingleEvent(ContentResolver contentResolver, String title, long calID, long start, long duration);

    Uri addDailyEvent(ContentResolver contentResolver, String title, long calID, long start, long duration, int repeatCount);

    Uri addWeeklyEvent(ContentResolver contentResolver, String title, long calID, long start, long duration, int repeatCount);

    void addMonthlyEvent(ContentResolver contentResolver, String title, long calID, long start, long duration, int repeatCount);

    void addYearlyEvent(ContentResolver contentResolver, String title, long calID, long start, long duration, int repeatCount);

    void addEvent(ContentResolver contentResolver, String title, long calID, EventInstance.Repeat repeat, long start, long end, int count);

    int addEvent();

    void updateEvent();

    boolean deleteEvent(int id);

    EventInstance getEvent(int id);

    Cursor getEvents(ContentResolver cr, long begin, long end);

    //    HashMap<Integer, List<EventInstance>> getEventsMap(Cursor cur);


//    List<EventInstance> getEvents(Cursor cursor);

//    EventInstance convertCursorToEvent(Cursor cursor);

    Uri getInstanceUriForJewishMonth(JewCalendar jewCalendar);

    void updateCalendarVisibility(ContentResolver contentResolver, CalendarAccount calendarAccount, boolean visibility);

}
