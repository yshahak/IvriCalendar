package il.co.yshahak.ivricalendar.calendar;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Instances;

import java.util.Date;
import java.util.TimeZone;

import il.co.yshahak.ivricalendar.calendar.google.CalendarAccount;
import il.co.yshahak.ivricalendar.calendar.google.EventInstance;
import il.co.yshahak.ivricalendar.calendar.google.GoogleManager;
import il.co.yshahak.ivricalendar.calendar.jewish.JewCalendar;

/**
 * Created by Yaakov Shahak
 * on 03/07/17.
 */

public class EventsProviderImpl implements EventsProvider {

    public static final String[] INSTANCE_PROJECTION = new String[] {
            Instances.EVENT_ID,       // 0
            Instances.BEGIN,          // 1
            Instances.END,            // 2
            Instances.TITLE ,         // 3
            Instances.DISPLAY_COLOR,  // 4
            Calendars.CALENDAR_DISPLAY_NAME,     //5
            Calendars.CALENDAR_COLOR,   //6
            Calendars.OWNER_ACCOUNT };   //7


    @SuppressLint("MissingPermission")
    @Override
    public Uri addSingleEvent(ContentResolver contentResolver, String title, long calID, long start, long duration) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, start);
        values.put(CalendarContract.Events.DTEND, start + duration);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        return contentResolver.insert(CalendarContract.Events.CONTENT_URI, values);
    }

    @SuppressLint("MissingPermission")
    @Override
    public Uri addDailyEvent(ContentResolver contentResolver, String title, long calID, long start, long duration, int repeatCount) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, start);
        String durationRule = EventsHelper.covertDurationToRule(duration);
        values.put(CalendarContract.Events.DURATION, durationRule);
        values.put(CalendarContract.Events.RRULE, "FREQ=DAILY;COUNT=" + repeatCount);//;BYDAY=TU   "FREQ=WEEKLY;BYDAY=TU;UNTIL=20150428"
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        return contentResolver.insert(CalendarContract.Events.CONTENT_URI, values);
    }

    @SuppressLint("MissingPermission")
    @Override
    public Uri addWeeklyEvent(ContentResolver contentResolver, String title, long calID, long start, long duration, int repeatCount) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, start);
        String durationRule = EventsHelper.covertDurationToRule(duration);
        values.put(CalendarContract.Events.DURATION, durationRule);
        values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;COUNT=" + repeatCount);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        return contentResolver.insert(CalendarContract.Events.CONTENT_URI, values);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void addMonthlyEvent(ContentResolver contentResolver, String title, long calID, long start, long duration, int repeatCount) {
        JewCalendar calStart = new JewCalendar(new Date(start));
        for (int i = 0 ; i < repeatCount ; i++) {
            Uri uri = addSingleEvent(contentResolver, title, calID, calStart.getTime().getTime(), duration);
            calStart.shiftMonthForward();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void addYearlyEvent(ContentResolver contentResolver, String title, long calID, long start, long duration, int repeatCount) {
        JewCalendar calStart = new JewCalendar(new Date(start));
        for (int i = 0 ; i < repeatCount ; i++) {
            Uri uri = addSingleEvent(contentResolver, title, calID, calStart.getTime().getTime(), duration);
            calStart.setJewishYear(calStart.getJewishYear() + 1);
        }
    }

    @Override
    public void addEvent(ContentResolver contentResolver, String title, long calID, EventInstance.Repeat repeat, long start, long duration, int repeatCount) {
        switch (repeat){
            case SINGLE:
                addSingleEvent(contentResolver, title, calID, start, duration);
                break;
            case DAY:
                addDailyEvent(contentResolver, title, calID, start, duration, repeatCount);
                break;
            case WEEK:
                addWeeklyEvent(contentResolver, title, calID, start, duration, repeatCount);
                break;
            case MONTH:
                addMonthlyEvent(contentResolver, title, calID, start, duration, repeatCount);
                break;
            case YEAR:
                addYearlyEvent(contentResolver, title, calID, start, duration, repeatCount);
                break;
        }
    }

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
    public Cursor getEvents(ContentResolver cr, long begin, long end) {
        Uri uri = GoogleManager.asSyncAdapter(begin, end);
        String WHERE_CALENDARS_SELECTED = CalendarContract.Calendars.VISIBLE + " = ? "; //AND " +
        String[] WHERE_CALENDARS_ARGS = {"1"};//
//        Cursor cur;
        return cr.query(uri,
                INSTANCE_PROJECTION,
                WHERE_CALENDARS_SELECTED,
                WHERE_CALENDARS_ARGS,
                null);
//        if (cur == null) {
//            return null;
//        }
//        return EventsHelper.getEvents(cur);
    }

    @Override
    public Uri getInstanceUriForJewishMonth(JewCalendar jewCalendar) {
        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, "com.google");
        ContentUris.appendId(builder, jewCalendar.getBeginOfMonth());
        ContentUris.appendId(builder, jewCalendar.getEndOfMonth());
        return builder.build();
    }

    @Override
    public void updateCalendarVisibility(ContentResolver contentResolver, CalendarAccount calendarAccount, boolean visibility) {
        Uri.Builder builder = CalendarContract.Calendars.CONTENT_URI.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER,"true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, calendarAccount.getCalendarName())
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, "com.google");
        ContentUris.appendId(builder, calendarAccount.getAccountId());
        ContentValues contentValues = new ContentValues();
        contentValues.put(CalendarContract.Calendars.VISIBLE, visibility);
        contentResolver.update(builder.build(), contentValues, null, null);
    }
}
