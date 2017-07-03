package il.co.yshahak.ivricalendar.calendar;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Instances;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import il.co.yshahak.ivricalendar.calendar.google.EventInstance;
import il.co.yshahak.ivricalendar.calendar.google.GoogleManager;
import il.co.yshahak.ivricalendar.calendar.jewish.JewCalendar;

/**
 * Created by Yaakov Shahak
 * on 03/07/17.
 */

public class EventsInterfaceImpl implements EventsInterface {

    public static final String[] INSTANCE_PROJECTION = new String[] {
            Instances.EVENT_ID,       // 0
            Instances.BEGIN,          // 1
            Instances.END,            // 2
            Instances.TITLE ,         // 3
            Instances.DISPLAY_COLOR,  // 4
            Calendars.CALENDAR_DISPLAY_NAME,     //5
            Calendars.CALENDAR_COLOR,   //6
            Calendars.OWNER_ACCOUNT };   //7


    // The indices for the projection array above.
    public static final int PROJECTION_EVENT_ID = 0;
    public static final int PROJECTION_BEGIN_INDEX = 1;
    public static final int PROJECTION_END_INDEX = 2;
    public static final int PROJECTION_TITLE_INDEX = 3;
    public static final int PROJECTION_DISPLAY_COLOR_INDEX = 4;
    public static final int PROJECTION_CALENDAR_DISPLAY_NAME_INDEX = 5;
    public static final int PROJECTION_CALENDAR_COLOR_INDEX = 6;

    @Override
    public int addEvent() {
        return 0;
    }

    @Override
    public void updateEvent() {

    }

    @Override
    public boolean deleteEvent(int id) {
        return false;
    }

    @Override
    public EventInstance getEvent(int id) {
        return null;
    }

    @Override
    public List<EventInstance> getEvents(Context context, long begin, long end) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = GoogleManager.asSyncAdapter(begin, end);
        String WHERE_CALENDARS_SELECTED = CalendarContract.Calendars.VISIBLE + " = ? "; //AND " +
        String[] WHERE_CALENDARS_ARGS = {"1"};//
        Cursor cur;
        cur = cr.query(uri,
                INSTANCE_PROJECTION,
                WHERE_CALENDARS_SELECTED,
                WHERE_CALENDARS_ARGS,
                null);
        if (cur == null) {
            return null;
        }
        List<EventInstance> list = new ArrayList<>();
        while (cur.moveToNext()) {
            EventInstance eventInstance = convertCursorToEvent(cur);
            list.add(eventInstance);
        }
        cur.close();
        return list;
    }

    @Override
    public EventInstance convertCursorToEvent(Cursor cursor) {
        long eventId = cursor.getLong(PROJECTION_EVENT_ID);
        String title = cursor.getString(PROJECTION_TITLE_INDEX);
        long start = cursor.getLong((PROJECTION_BEGIN_INDEX));
        long end = cursor.getLong((PROJECTION_END_INDEX));
        String calendarName = cursor.getString(PROJECTION_CALENDAR_DISPLAY_NAME_INDEX);
        int displayColor = cursor.getInt(PROJECTION_DISPLAY_COLOR_INDEX);
//        int calendarColor = cursor.getInt(PROJECTION_CALENDAR_COLOR_INDEX);
        boolean allDayEvent = (end - start) == TimeUnit.DAYS.toMillis(1);
        if (allDayEvent){
            end = start;
        }
        int dayOfMonth = JewCalendar.getDayOfMonth(start);
        return new EventInstance(eventId, title, allDayEvent, start, end, displayColor, calendarName, dayOfMonth);
    }

    @Override
    public Uri getInstanceUriForJewishMonth(JewCalendar jewCalendar) {
        Uri.Builder builder = CalendarContract.Instances.CONTENT_BY_DAY_URI.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, "com.google");
        ContentUris.appendId(builder, jewCalendar.getBeginOfMonth());
        ContentUris.appendId(builder, jewCalendar.getEndOfMonth());
        return builder.build();
    }
}
