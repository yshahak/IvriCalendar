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
import il.co.yshahak.ivricalendar.calendar.jewish.Day;
import il.co.yshahak.ivricalendar.calendar.jewish.Week;

import static il.co.yshahak.ivricalendar.calendar.google.Contract.Calendar_PROJECTION;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.HEBREW_CALENDAR_SUMMERY_TITLE;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.KEY_HEBREW_ID;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_ACCOUNTNAME_INDEX;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_COLOR_INDEX;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_DISPLAY_NAME_INDEX;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_ID_INDEX;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_OWNER_ACCOUNT_INDEX;
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


    @SuppressWarnings("MissingPermission")
    private static void syncCalendars(Context context){
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

    public static Uri asSyncAdapter(Week week) {
        int firstDay = 0, lastDay = week.getDays().length - 1;
        for (int i = 0 ; i < week.getDays().length ;i ++){
            if (week.getDays()[i] != null){
                firstDay = i;
                break;
            }
        }
        for (int i = lastDay ; i >= 0 ;i--){
            if (week.getDays()[i] != null){
                lastDay = i;
                break;
            }
        }
        Time time = new Time();

        time.set(week.getDays()[firstDay].getJewishCalendar().getTime().getTime());
        time.allDay = true;
        time.hour = 0;
        time.minute = 0;
        time.second = 0;
        long begin = Time.getJulianDay(time.toMillis(true), 0);
        time.monthDay += (lastDay - firstDay);

        long end = Time.getJulianDay(time.toMillis(true), 0);

        Uri.Builder builder = CalendarContract.Instances.CONTENT_BY_DAY_URI.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, "com.google");
        ContentUris.appendId(builder, begin);
        ContentUris.appendId(builder, end);
        return builder.build();
    }

    public static Uri asSyncAdapter(Day day) {
        Time time = new Time();

        time.set(day.getJewishCalendar().getTime().getTime());
        time.allDay = true;
        time.hour = 0;
        time.minute = 0;
        time.second = 0;
        long begin = Time.getJulianDay(time.toMillis(true), 0);

        long end = Time.getJulianDay(time.toMillis(true), 0);

        Uri.Builder builder = CalendarContract.Instances.CONTENT_BY_DAY_URI.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, "com.google");
        ContentUris.appendId(builder, begin);
        ContentUris.appendId(builder, end);
        return builder.build();
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

    public static void updateCalendarVisibility(ContentResolver contentResolver,  CalendarAccount calendarAccount, boolean visibility) {
        Uri.Builder builder = CalendarContract.Calendars.CONTENT_URI.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER,"true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, calendarAccount.getCalendarName())
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, "com.google");
        ContentUris.appendId(builder, calendarAccount.getAccountId());
        ContentValues contentValues = new ContentValues();
        contentValues.put(CalendarContract.Calendars.VISIBLE, visibility);
        contentResolver.update(builder.build(), contentValues, null, null);
    }

    @SuppressWarnings("MissingPermission")
    public static void addHebrewEventToGoogleServer(Context context, String title, int repeatId, Calendar start, Calendar end, String count){
        long calID = PreferenceManager.getDefaultSharedPreferences(context).getLong(KEY_HEBREW_ID, -1L);
        if (calID == -1L){
            return;
        }
        ContentValues values;
        ContentResolver cr = context.getContentResolver();
        switch (repeatId){
            case R.id.repeat_single:
            case R.id.repeat_daily:
            case R.id.repeat_weekly:
                values = getContentValueForSingleEvent(title, calID, repeatId, start, end, count);
                cr.insert(CalendarContract.Events.CONTENT_URI, values);
                break;
            case R.id.repeat_monthly:
                ContentValues[] contentValues = new ContentValues[Integer.valueOf(count)];
                Calendar calStart = (Calendar) start.clone();
                Calendar endStart = (Calendar) end.clone();
                for (int i = 0 ; i < contentValues.length ; i++) {
                    start.setTime(shiftMonth(new JewishCalendar(calStart), i).getTime());
                    end.setTime(shiftMonth(new JewishCalendar(endStart), i).getTime());
                    contentValues[i] = getContentValueForSingleEvent(title, calID, R.id.repeat_single, start, end, count);
                }
                cr.bulkInsert(CalendarContract.Events.CONTENT_URI, contentValues);
                syncCalendars(context);
                break;
            case R.id.repeat_yearly:
                contentValues = new ContentValues[Integer.valueOf(count)];
                JewishCalendar jewishCalendarStart = new JewishCalendar(start);
                JewishCalendar jewishCalendarEnd = new JewishCalendar(end);
                for (int i = 0 ; i < contentValues.length ; i++) {
                    Log.d("TAG",  hebrewDateFormatter.formatHebrewNumber(jewishCalendarStart.getJewishYear()) + " , "
                            + hebrewDateFormatter.formatMonth(jewishCalendarStart) + " , "
                            + hebrewDateFormatter.formatHebrewNumber(jewishCalendarStart.getJewishDayOfMonth()));
                    contentValues[i] = getContentValueForSingleEvent(title, calID, R.id.repeat_single, start, end, count);
                    jewishCalendarStart.setJewishYear(jewishCalendarStart.getJewishYear() + 1);
                    start.setTime(jewishCalendarStart.getTime());
                    jewishCalendarEnd.setJewishYear(jewishCalendarEnd.getJewishYear() + 1);
                    end.setTime(jewishCalendarEnd.getTime());
                }
                cr.bulkInsert(CalendarContract.Events.CONTENT_URI, contentValues);
                break;
        }
        syncCalendars(context);
    }

    @SuppressWarnings("MissingPermission")
    private static ContentValues getContentValueForSingleEvent(String title, long calID, int repeatId, Calendar start, Calendar end, String count){
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, start.getTimeInMillis());

        switch (repeatId){
            case R.id.repeat_single:
                values.put(CalendarContract.Events.DTEND, end.getTimeInMillis());
                break;
            case R.id.repeat_daily:
                values.put(CalendarContract.Events.DURATION, "PT1H0M");
                values.put(CalendarContract.Events.RRULE, "FREQ=DAILY;COUNT=" + count);//;BYDAY=TU   "FREQ=WEEKLY;BYDAY=TU;UNTIL=20150428"
                break;
            case R.id.repeat_weekly:
                values.put(CalendarContract.Events.DURATION, "PT1H0M");
                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;COUNT=" + count);
                break;
        }
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        return values;
    }

}
