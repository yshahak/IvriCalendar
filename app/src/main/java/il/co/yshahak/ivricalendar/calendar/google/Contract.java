package il.co.yshahak.ivricalendar.calendar.google;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Instances;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by yshahak on 08/10/2016.
 */

public class Contract {

    public static final int REQUEST_READ_CALENDAR = 100;
    // Projection array. Creating indices for this array instead of doing
// dynamic lookups improves performance.
    private static final String[] Calendar_PROJECTION = new String[]{
            Calendars._ID,                           // 0
            Calendars.ACCOUNT_NAME,                  // 1
            Calendars.CALENDAR_DISPLAY_NAME,         // 2
            Calendars.OWNER_ACCOUNT                  // 3
    };

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

    public static void getCalendars(Activity activity) {
        // Run query
        Cursor cur;
        ContentResolver cr = activity.getContentResolver();
        Uri uri = Calendars.CONTENT_URI;
        String selection = "((" + Calendars.CALENDAR_DISPLAY_NAME + " = ?))";
//        String[] selectionArgs = new String[]{"yshahak@gmail.com", "com.google", "yshahak@gmail.com"};
        String[] selectionArgs = new String[]{"Ester&Yaakov"};
        // Submit the query and get a Cursor object back.
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
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
                    accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
                    ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);
                    Log.d("TAG", "calID: " + calID);
                    getEvent(activity, calID);
                };
                cur.close();
            }

        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CALENDAR}, REQUEST_READ_CALENDAR);
        }
    }

    private static final String[] EVENT_PROJECTION = new String[]{
            Events._ID,                           // 0
            Events.TITLE,
            Events.DTSTART
           };

    // The indices for the projection array above.
    private static final int PROJECTION_Events_ID_INDEX = 0;
    private static final int PROJECTION_TITLE = 1;
    private static final int PROJECTION_DTSTARTE = 2;

    public static void getEvent(Activity activity, Long calId){
        // Specify the date range you want to search for recurring
// event instances
        // Construct the query with the desired date range.
        Uri.Builder builder = Instances.CONTENT_BY_DAY_URI.buildUpon();

        Cursor cur ;
        ContentResolver cr = activity.getContentResolver();
        Calendar beginTime = Calendar.getInstance();
        long endMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.add(Calendar.MONTH, -7);
        long startMillis = endTime.getTimeInMillis();

        String selection = Events.CALENDAR_ID + " = ?";
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


    private static final String[] INSTANCE_PROJECTION = new String[] {
            Instances.EVENT_ID,      // 0
            Instances.BEGIN,         // 1
            Instances.TITLE          // 2
    };


    // The indices for the projection array above.
    private static final int PROJECTION_BEGIN_INDEX = 1;
    private static final int PROJECTION_TITLE_INDEX = 2;

    public static void getInstances(Activity activity, Long eventId){
        // Specify the date range you want to search for recurring
// event instances
        Time time = new Time();
        time.setToNow();
        time.monthDay +=1;
        time.allDay = true;
        time.hour = 0;
        time.minute = 0;
        time.second = 0;
        long begin = Time.getJulianDay(time.toMillis(true), 0);
        time.monthDay +=1;
        long end = Time.getJulianDay(time.toMillis(true), 0);
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(Calendar.DAY_OF_MONTH, 9);
        Cursor cur ;
        ContentResolver cr = activity.getContentResolver();

        String selection = Instances.EVENT_ID + " = ?";
        String[] selectionArgs = new String[] {eventId.toString()};

// Construct the query with the desired date range.
        Uri.Builder builder = Instances.CONTENT_BY_DAY_URI.buildUpon();
        ContentUris.appendId(builder, begin);
        ContentUris.appendId(builder, end);

// Submit the query
        cur =  cr.query(builder.build(),
                INSTANCE_PROJECTION,
                null,
                null,
                null);
        if (cur == null) {
            return;
        }
        while (cur.moveToNext()) {
            String title;
            long eventID = 0;
            long beginVal = 0;

            // Get the field values
            eventID = cur.getLong(PROJECTION_ID_INDEX);
            beginVal = cur.getLong(PROJECTION_BEGIN_INDEX);
            title = cur.getString(PROJECTION_TITLE_INDEX);

            // Do something with the values.
            Log.i("TAG", "Event:  " + title);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(beginVal);
            DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
            Log.i("TAG", "Date: " + formatter.format(calendar.getTime()));
        }
        cur.close();
    }

    /**
     * @param cr The ContentResolver to use for the query
     * @param projection The columns to return
     * @param startDay The start of the time range to query in UTC millis since
     *            epoch
     * @param endDay The end of the time range to query in UTC millis since
     *            epoch
     * @param selection Filter on the query as an SQL WHERE statement
     * @param selectionArgs Args to replace any '?'s in the selection
     * @param orderBy How to order the rows as an SQL ORDER BY statement
     * @return A Cursor of instances matching the selection
     */
    private static final Cursor instancesQuery(ContentResolver cr, String[] projection,
                                               int startDay, int endDay, String selection, String[] selectionArgs, String orderBy) {
        String WHERE_CALENDARS_SELECTED = Calendars.VISIBLE + "=?";
        String[] WHERE_CALENDARS_ARGS = {"1"};
        String DEFAULT_SORT_ORDER = "begin ASC";

        Uri.Builder builder = Instances.CONTENT_BY_DAY_URI.buildUpon();
        ContentUris.appendId(builder, startDay);
        ContentUris.appendId(builder, endDay);
        if (TextUtils.isEmpty(selection)) {
            selection = WHERE_CALENDARS_SELECTED;
            selectionArgs = WHERE_CALENDARS_ARGS;
        } else {
            selection = "(" + selection + ") AND " + WHERE_CALENDARS_SELECTED;
            if (selectionArgs != null && selectionArgs.length > 0) {
                selectionArgs = Arrays.copyOf(selectionArgs, selectionArgs.length + 1);
                selectionArgs[selectionArgs.length - 1] = WHERE_CALENDARS_ARGS[0];
            } else {
                selectionArgs = WHERE_CALENDARS_ARGS;
            }
        }
        return cr.query(builder.build(), projection, selection, selectionArgs,
                orderBy == null ? DEFAULT_SORT_ORDER : orderBy);
    }

}
