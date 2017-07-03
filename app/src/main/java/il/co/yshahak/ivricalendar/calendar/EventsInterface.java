package il.co.yshahak.ivricalendar.calendar;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.List;

import il.co.yshahak.ivricalendar.calendar.google.EventInstance;
import il.co.yshahak.ivricalendar.calendar.jewish.JewCalendar;

/**
 * Created by Yaakov Shahak
 * on 18/06/17.
 */

public interface EventsInterface {

    int addEvent();

    void updateEvent();

    boolean deleteEvent(int id);

    EventInstance getEvent(int id);

    List<EventInstance> getEvents(Context context, long begin, long end);

    EventInstance convertCursorToEvent(Cursor cursor);

    Uri getInstanceUriForJewishMonth(JewCalendar jewCalendar);

}
