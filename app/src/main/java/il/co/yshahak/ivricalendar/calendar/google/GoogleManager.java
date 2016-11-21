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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import il.co.yshahak.ivricalendar.R;

import static android.provider.CalendarContract.Instances.CONTENT_BY_DAY_URI;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.Calendar_PROJECTION;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.HEBREW_CALENDAR_SUMMERY_TITLE;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.KEY_HEBREW_ID;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_ACCOUNTNAME_INDEX;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_COLOR_INDEX;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_DISPLAY_NAME_INDEX;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_Events_ID_INDEX;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_ID_INDEX;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_OWNER_ACCOUNT_INDEX;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_TITLE;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_VISIBLE_INDEX;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.REQUEST_READ_CALENDAR;
import static il.co.yshahak.ivricalendar.calendar.jewish.Month.hebrewDateFormatter;
import static il.co.yshahak.ivricalendar.calendar.jewish.Month.shiftMonth;

/**
 * Created by B.E.L on 01/11/2016.
 */

public class GoogleManager {

    public static HashMap<String, List<CalendarAccount>> accountListNames = new HashMap<>();

    public static void getCalendars(Activity activity) {

        // Submit the query and get a Cursor object back.
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            syncCalendars(activity);
            // Run query
            Cursor cur;
            ContentResolver cr = activity.getContentResolver();
            Uri uri = CalendarContract.Calendars.CONTENT_URI;
            String selection = "(" + CalendarContract.Calendars.ACCOUNT_TYPE + " = ? " + ")";
            String[] selectionArgs = new String[]{"com.google"};

            cur = cr.query(uri, Calendar_PROJECTION, selection, selectionArgs, null);
            if (cur != null) {
                accountListNames.clear();
                while (cur.moveToNext()) {
                    int calID , color;
                    String displayName = null;
                    String accountName = null;
                    String ownerName = null;
                    boolean visible;

                    // Get the field values
                    calID = cur.getInt(PROJECTION_ID_INDEX);
                    displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
                    accountName = cur.getString(PROJECTION_ACCOUNTNAME_INDEX);

                    ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);
                    color = cur.getInt(PROJECTION_COLOR_INDEX);
                    visible = cur.getInt(PROJECTION_VISIBLE_INDEX) == 1;
                    CalendarAccount calendarAccount = new CalendarAccount();
                    calendarAccount.setAccountId(calID);
                    calendarAccount.setCalendarName(accountName);
                    calendarAccount.setCalendarDisplayName(displayName);
                    calendarAccount.setCalendarOwnerName(ownerName);
                    calendarAccount.setCalendarColor(color);
                    calendarAccount.setCalendarIsVisible(visible);
                    if (accountListNames.get(accountName) == null){
                        List<CalendarAccount> accountList = new ArrayList<>();
                        accountList.add(calendarAccount);
                        accountListNames.put(accountName, accountList);
                    } else {
                        accountListNames.get(accountName).add(calendarAccount);
                    }
                    Log.d("TAG", "calID: " + calID + " , displayName: " + displayName + ", accountName: " + accountName
                            + " , ownerName: " + ownerName);

                    if (displayName.equals(HEBREW_CALENDAR_SUMMERY_TITLE)){
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

    public static int getCalendarNumber(){
        int[] sizes = getCalendarNumbers();
        int count = 0;
        for (Integer i : sizes){
            count += i;
        }
        return count;
    }

    public static int[] getCalendarNumbers(){
        int[] sizes = new int[accountListNames.size()];
        int i = 0;
        for (String name : accountListNames.keySet()){
            sizes[i] =  accountListNames.get(name).size();
            i++;
        }
        return sizes;
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
    public static void addHebrewEventToGoogleServer(Context context, String title, int repeatId, Calendar start, Calendar end){
        long calID = PreferenceManager.getDefaultSharedPreferences(context).getLong(KEY_HEBREW_ID, -1L);
        if (calID == -1L){
            return;
        }

        ContentResolver cr = context.getContentResolver();
        Uri uri = null;
        switch (repeatId){
            case R.id.repeat_single:
            case R.id.repeat_daily:
            case R.id.repeat_weekly:
                ContentValues values = getContentValueForSingleEvent(title, calID, repeatId, start, end);
                uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
                if (uri != null) {
                    syncCalendars(context);
                }
            case R.id.repeat_monthly:
                ContentValues[] contentValues = new ContentValues[12];
                Calendar calStart = (Calendar) start.clone();
                Calendar endStart = (Calendar) end.clone();
                for (int i = 0 ; i < contentValues.length ; i++) {
                    start.setTime(shiftMonth(new JewishCalendar(calStart), i).getTime());
                    end.setTime(shiftMonth(new JewishCalendar(endStart), i).getTime());
                    contentValues[i] = getContentValueForSingleEvent(title, calID, R.id.repeat_single, start, end);
                }
                cr.bulkInsert(CalendarContract.Events.CONTENT_URI, contentValues);
                syncCalendars(context);
                break;
            case R.id.repeat_yearly:
                contentValues = new ContentValues[12];
                JewishCalendar jewishCalendarStart = new JewishCalendar(start);
                JewishCalendar jewishCalendarEnd = new JewishCalendar(end);
                for (int i = 0 ; i < contentValues.length ; i++) {
                    Log.d("TAG",  hebrewDateFormatter.formatHebrewNumber(jewishCalendarStart.getJewishYear()) + " , "
                            + hebrewDateFormatter.formatMonth(jewishCalendarStart) + " , "
                            + hebrewDateFormatter.formatHebrewNumber(jewishCalendarStart.getJewishDayOfMonth()));
                    contentValues[i] = getContentValueForSingleEvent(title, calID, R.id.repeat_single, start, end);
                    jewishCalendarStart.setJewishYear(jewishCalendarStart.getJewishYear() + 1);
                    start.setTime(jewishCalendarStart.getTime());
                    jewishCalendarEnd.setJewishYear(jewishCalendarEnd.getJewishYear() + 1);
                    end.setTime(jewishCalendarEnd.getTime());
                }
                cr.bulkInsert(CalendarContract.Events.CONTENT_URI, contentValues);
                syncCalendars(context);
                break;
        }



    }

    @SuppressWarnings("MissingPermission")
    private static ContentValues getContentValueForSingleEvent(String title, long calID, int repeatId, Calendar start, Calendar end){
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, start.getTimeInMillis());

        switch (repeatId){
            case R.id.repeat_single:
                values.put(CalendarContract.Events.DTEND, end.getTimeInMillis());
                break;
            case R.id.repeat_daily:
                values.put(CalendarContract.Events.DURATION, "PT1H0M");
                values.put(CalendarContract.Events.RRULE, "FREQ=DAILY;COUNT=40");//;BYDAY=TU   "FREQ=WEEKLY;BYDAY=TU;UNTIL=20150428"
                break;
            case R.id.repeat_weekly:
                values.put(CalendarContract.Events.DURATION, "PT1H0M");
                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;COUNT=16");
                break;
        }
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        return values;
    }

}
