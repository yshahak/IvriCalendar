package il.co.yshahak.ivricalendar.calendar.google;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.text.format.Time;
import android.util.Log;

import net.sourceforge.zmanim.hebrewcalendar.JewishCalendar;

import java.util.Calendar;
import java.util.TimeZone;

import static android.provider.CalendarContract.Instances.CONTENT_BY_DAY_URI;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.Calendar_PROJECTION;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.HEBREW_CALENDAR_SUMMERY_TITLE;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.KEY_HEBREW_ID;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_ACCOUNTNAME_INDEX;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_DISPLAY_NAME_INDEX;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_Events_ID_INDEX;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_ID_INDEX;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_OWNER_ACCOUNT_INDEX;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_TITLE;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.REQUEST_READ_CALENDAR;

/**
 * Created by B.E.L on 01/11/2016.
 */

public class GoogleManager {

    public static void getCalendars(Activity activity) {

        // Submit the query and get a Cursor object back.
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            syncCalendars(activity);
            // Run query
            Cursor cur;
            ContentResolver cr = activity.getContentResolver();
            Uri uri = CalendarContract.Calendars.CONTENT_URI;
            String selection = "((" + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?))";
            //        String[] selectionArgs = new String[]{"yshahak@gmail.com", "com.google", "yshahak@gmail.com"};
            String[] selectionArgs = new String[]{"com.google"};
            cur = cr.query(uri, Calendar_PROJECTION, selection, selectionArgs, null);
            if (cur != null) {
                while (cur.moveToNext()) {
                    long calID = 0;
                    String displayName = null;
                    String accountName = null;
                    String ownerName = null;

                    // Get the field values
                    calID = cur.getLong(PROJECTION_ID_INDEX);
                    displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
                    accountName = cur.getString(PROJECTION_ACCOUNTNAME_INDEX);
                    ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);
                    if (displayName.equals(HEBREW_CALENDAR_SUMMERY_TITLE)){
                        Log.d("TAG", "calID: " + calID + " , displayName: " + displayName + ", accountName: " + accountName + " , ownerName: " + ownerName);
                        PreferenceManager.getDefaultSharedPreferences(activity).edit()
                                .putLong(KEY_HEBREW_ID, calID).apply();
                    }
//                    getEvent(activity, calID);
                };
                cur.close();
            }

        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CALENDAR}, REQUEST_READ_CALENDAR);
        }
    }

    @SuppressWarnings("MissingPermission")
    public static void syncCalendars(Context context){
        Account[] accounts = AccountManager.get(context).getAccounts();
        String authority = CalendarContract.Calendars.CONTENT_URI.getAuthority();
        for (Account account : accounts) {
            Bundle extras = new Bundle();
            extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
            if (account.type.equals("com.google")) {
                ContentResolver.requestSync(account, authority, extras);
            }
        }
    }

    public static void getEvent(Activity activity, Long calId){
        // Specify the date range you want to search for recurring
// event instances
        // Construct the query with the desired date range.
        Uri.Builder builder = CONTENT_BY_DAY_URI.buildUpon();

        Cursor cur ;
        ContentResolver cr = activity.getContentResolver();
        Calendar beginTime = Calendar.getInstance();
        long endMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.add(Calendar.MONTH, -7);
        long startMillis = endTime.getTimeInMillis();

        String selection = CalendarContract.Events.CALENDAR_ID + " = ?";
        String[] selectionArgs = new String[] {calId.toString()};
        ContentUris.appendId(builder, startMillis);
        ContentUris.appendId(builder, endMillis);
// Submit the query
//        cur = instancesQuery(cr, null, (int)startMillis, (int)endMillis, null, null, null);
        cur =  cr.query(builder.build(),
                null,
                null,
                null,
                null);
        if (cur == null) {
            return;
        }
        while (cur.moveToNext()) {
            long eventID;
            eventID = cur.getLong(PROJECTION_Events_ID_INDEX);
            String title = cur.getString(PROJECTION_TITLE);
            Log.i("TAG", "title:  " + title);
//            getInstances(activity, eventID);
        }
        cur.close();
    }

    public static Uri asSyncAdapter(JewishCalendar jewishCalendar) {
        jewishCalendar.setJewishDayOfMonth(1);
        Time time = new Time();
        time.set(jewishCalendar.getTime().getTime());
        time.allDay = true;
        time.hour = 0;
        time.minute = 0;
        time.second = 0;
        long begin = Time.getJulianDay(time.toMillis(true), 0);
        if (jewishCalendar.getDaysInJewishMonth() == 29){
            time.monthDay += 29;
        } else {
            time.monthDay += 30;
        }
        long end = Time.getJulianDay(time.toMillis(true), 0);

        Uri.Builder builder = CalendarContract.Instances.CONTENT_BY_DAY_URI.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, "com.google");
        ContentUris.appendId(builder, begin);
        ContentUris.appendId(builder, end);
        return builder.build();
    }

    @SuppressWarnings("MissingPermission")
    public static void addHebrewEventToGoogleServer(Context context, String title, int... times){
        long calID = PreferenceManager.getDefaultSharedPreferences(context).getLong(KEY_HEBREW_ID, -1L);
        if (calID == -1L){
            return;
        }
        long startMillis;
        long endMillis;
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(Calendar.HOUR_OF_DAY, times[0]);
        beginTime.set(Calendar.MINUTE, times[1]);
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(Calendar.HOUR_OF_DAY, times[2]);
        endTime.set(Calendar.MINUTE, times[3]);
        endMillis = endTime.getTimeInMillis();

        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        if (uri != null) {
            Log.d("TAG", "uri inserted: " + uri.toString());
            // get the event ID that is the last element in the Uri
            long eventID = Long.parseLong(uri.getLastPathSegment());
            Log.d("TAG", "eventID: " + eventID);
            syncCalendars(context);
        }
    }

}
