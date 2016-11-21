package il.co.yshahak.ivricalendar.calendar.google;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Instances;
import android.text.format.Time;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static android.provider.CalendarContract.Instances.CONTENT_BY_DAY_URI;

/**
 * Created by yshahak on 08/10/2016.
 */

public class Contract {

    public static String KEY_HEBREW_CALENDAR_CLIENT_API_ID = "keyHebrewIdClientAPI";
    public static String KEY_HEBREW_ID = "keyHebrewId";

    public static String HEBREW_CALENDAR_SUMMERY_TITLE = "לוח עברי על הבר";
    public static final int REQUEST_READ_CALENDAR = 100;
    // Projection array. Creating indices for this array instead of doing
// dynamic lookups improves performance.
    public static final String[] Calendar_PROJECTION = new String[]{
            Calendars._ID,                           // 0
            Calendars.ACCOUNT_NAME,                  // 1
            Calendars.CALENDAR_DISPLAY_NAME,         // 2
            Calendars.OWNER_ACCOUNT,                 // 3
            Calendars.CALENDAR_COLOR,                // 4
            Calendars.VISIBLE                        // 5
    };

    // The indices for the projection array above.
    public static final int PROJECTION_ID_INDEX = 0;
    public static final int PROJECTION_ACCOUNTNAME_INDEX = 1;
    public static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    public static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
    public static final int PROJECTION_COLOR_INDEX = 4;
    public static final int PROJECTION_VISIBLE_INDEX = 5;

    public void addHebrewCalendarToGoogleServer(Context context){
        long calID = 2;
        ContentValues values = new ContentValues();
        Uri.Builder builder = Calendars.CONTENT_URI.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER, "true");
//                .appendQueryParameter(Calendars.ACCOUNT_NAME, "yshahak@gmail.com")
//                .appendQueryParameter(Calendars.ACCOUNT_TYPE, "com.google");
        values.put(Calendars.CALENDAR_DISPLAY_NAME, HEBREW_CALENDAR_SUMMERY_TITLE);
        values.put(Calendars.ACCOUNT_TYPE, "com.google");
        Uri uri = context.getContentResolver().insert(builder.build(), values);
    }



    public static final String[] EVENT_PROJECTION = new String[]{
            Events._ID,                           // 0
            Events.TITLE,
            Events.DTSTART
           };

    // The indices for the projection array above.
    public static final int PROJECTION_Events_ID_INDEX = 0;
    public static final int PROJECTION_TITLE = 1;
    public static final int PROJECTION_DTSTARTE = 2;



    public static final String[] INSTANCE_PROJECTION = new String[] {
            Instances.EVENT_ID,       // 0
            Instances.BEGIN,          // 1
            Instances.END,            // 2
            Instances.TITLE ,         // 3
            Instances.DISPLAY_COLOR,   // 4
            Calendars.CALENDAR_DISPLAY_NAME,     //5
            Calendars.CALENDAR_COLOR,   //6
            Calendars.OWNER_ACCOUNT };   //7


    // The indices for the projection array above.
    public static final int PROJECTION_BEGIN_INDEX = 1;
    public static final int PROJECTION_END_INDEX = 2;
    public static final int PROJECTION_TITLE_INDEX = 3;
    public static final int PROJECTION_DISPLAY_COLOR_INDEX = 4;
    public static final int PROJECTION_CALENDAR_DISPLAY_NAME_INDEX = 5;
    public static final int PROJECTION_CALENDAR_COLOR_INDEX = 6;

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

//        String selection = OWNER_ACCOUNT + " = ?";
//        String[] selectionArgs = new String[] {"yshahak@gmail.com"};

// Construct the query with the desired date range.
        Uri.Builder builder = CONTENT_BY_DAY_URI.buildUpon();
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

    public static ArrayList<String> getInstancesForDate(Activity activity, Calendar cal){
        // Specify the date range you want to search for recurring
// event instances
        Time time = new Time();
        time.set(cal.getTimeInMillis());
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

// Construct the query with the desired date range.
        Uri.Builder builder = CONTENT_BY_DAY_URI.buildUpon();
        ContentUris.appendId(builder, begin);
        ContentUris.appendId(builder, end);

// Submit the query
        cur =  cr.query(builder.build(),
                INSTANCE_PROJECTION,
                null,
                null,
                null);
        if (cur == null) {
            return null;
        }
        ArrayList<String> arrayList = new ArrayList<>();
        while (cur.moveToNext()) {
            String title;
            title = cur.getString(PROJECTION_TITLE_INDEX);
            arrayList.add(title);
            Log.i("TAG", "Event:  " + title);
        }
        cur.close();
        return arrayList;
    }



}
