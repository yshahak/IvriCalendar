package il.co.yshahak.ivricalendar.calendar.jewish;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import net.sourceforge.zmanim.ZmanimCalendar;
import net.sourceforge.zmanim.hebrewcalendar.JewishCalendar;

import java.util.Calendar;
import java.util.Date;

import static il.co.yshahak.ivricalendar.calendar.jewish.Month.hebrewDateFormatter;

/**
 * Created by B.E.L on 29/11/2016.
 */

public class JewishDbManager {

    public static void addMonth(Context context, JewishCalendar jewishCalendar){
        ZmanimCalendar zmanimCalendar = new ZmanimCalendar();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(jewishCalendar.getTime());
        zmanimCalendar.setCalendar(calendar);
        int year = jewishCalendar.getJewishYear();
        int month = jewishCalendar.getJewishMonth();
        // First, check if the location with this city name exists in the db
        Cursor dateCursor = context.getContentResolver().query(
                JewishCalendarContract.DateEntry.CONTENT_URI,
                new String[]{JewishCalendarContract.DateEntry._ID},
                JewishCalendarContract.DateEntry.COLUMN_DATE_YEAR + " = ? AND " +
                        JewishCalendarContract.DateEntry.COLUMN_DATE_MONTH + " = ?",
                new String[]{Integer.toString(year), Integer.toString(month)},
                null);
        if (dateCursor != null && dateCursor.moveToFirst()) {
            Log.d("TAG", "record of this date and location already exists");
        } else {
            int size = jewishCalendar.getDaysInJewishMonth();
            ContentValues[] values = new ContentValues[size];
            for (int i = 1; i <= size; i++){
                jewishCalendar.setJewishDate(year, month, i);
                ContentValues contentValues = new ContentValues();
                String parahsh = null;
                int dayInWeek = jewishCalendar.getDayOfWeek();
                contentValues.put(JewishCalendarContract.DateEntry.COLUMN_DATE_YEAR, year);
                contentValues.put(JewishCalendarContract.DateEntry.COLUMN_DATE_MONTH, month);
                contentValues.put(JewishCalendarContract.DateEntry.COLUMN_DATE_DAY_IN_MONTH, i);
                contentValues.put(JewishCalendarContract.DateEntry.COLUMN_DATE_DAY_IN_WEEK, dayInWeek);
                contentValues.put(JewishCalendarContract.DateEntry.COLUMN_DATE_YEAR_LABEL, hebrewDateFormatter.formatHebrewNumber(jewishCalendar.getJewishYear()));
                contentValues.put(JewishCalendarContract.DateEntry.COLUMN_DATE_MONTH_LABEL, hebrewDateFormatter.formatMonth(jewishCalendar));
                contentValues.put(JewishCalendarContract.DateEntry.COLUMN_DATE_DAY_LABEL, hebrewDateFormatter.formatHebrewNumber(i));
                if (dayInWeek == 6){
                    Date shift24 = new Date(jewishCalendar.getTime().getTime() + 1000*60*60*24);
                    jewishCalendar.setDate(shift24);
                    JewishCalendar shiftCalendar = new JewishCalendar();
                    shiftCalendar.setDate(shift24);
                    parahsh = hebrewDateFormatter.formatParsha(shiftCalendar);
                }
                addTimesToValues(zmanimCalendar, contentValues, parahsh);
                values[i - 1] = contentValues;
            }
            context.getContentResolver().bulkInsert(JewishCalendarContract.DateEntry.CONTENT_URI, values);
        }
        if (dateCursor != null) {
            dateCursor.close();
        }
    }

    private static void addTimesToValues(ZmanimCalendar zmanimCalendar, ContentValues dateValues, String parasha) {
        Date dusk = new Date(zmanimCalendar.getSunset().getTime() +  1000 * 60 * 20); //zet hacochavim is 20 minutes after sunset)
        dateValues.put(JewishCalendarContract.DateEntry.COLUMN_ALOS, zmanimCalendar.getAlosHashachar().getTime());
        dateValues.put(JewishCalendarContract.DateEntry.COLUMN_SUNRISE, zmanimCalendar.getSunrise().getTime());
        dateValues.put(JewishCalendarContract.DateEntry.COLUMN_SOf_ZMAN_SHMA_MGA, zmanimCalendar.getSofZmanShmaMGA().getTime());
        dateValues.put(JewishCalendarContract.DateEntry.COLUMN_SOf_ZMAN_SHMA_GRA, zmanimCalendar.getSofZmanShmaGRA().getTime());
        dateValues.put(JewishCalendarContract.DateEntry.COLUMN_SOf_ZMAN_TFILA_MGA, zmanimCalendar.getSofZmanTfilaMGA().getTime());
        dateValues.put(JewishCalendarContract.DateEntry.COLUMN_SOf_ZMAN_TFILA_GRA, zmanimCalendar.getSofZmanTfilaGRA().getTime());
        dateValues.put(JewishCalendarContract.DateEntry.COLUMN_MINCHA_GDOLA, zmanimCalendar.getMinchaGedola().getTime());
        dateValues.put(JewishCalendarContract.DateEntry.COLUMN_MINCHA_KTANA, zmanimCalendar.getMinchaKetana().getTime());
        dateValues.put(JewishCalendarContract.DateEntry.COLUMN_SUNSET, zmanimCalendar.getSunset().getTime());
        dateValues.put(JewishCalendarContract.DateEntry.COLUMN_DUSK, dusk.getTime());
        if (parasha != null){
            dateValues.put(JewishCalendarContract.DateEntry.COLUMN_KNISAT_SHABBAT, zmanimCalendar.getCandleLighting().getTime() + 20);
            dateValues.put(JewishCalendarContract.DateEntry.COLUMN_YEZIAT_SHABBAT, zmanimCalendar.getTzais().getTime());
            dateValues.put(JewishCalendarContract.DateEntry.COLUMN_PARASHA, parasha);
        }
    }
}
