package il.co.yshahak.ivricalendar.calendar.jewish;

import android.app.Activity;
import android.content.Context;

import net.sourceforge.zmanim.hebrewcalendar.HebrewDateFormatter;
import net.sourceforge.zmanim.hebrewcalendar.JewishCalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import il.co.yshahak.ivricalendar.calendar.google.Contract;

/**
 * Created by yshahak on 07/10/2016.
 */

public class Month {

    public static HebrewDateFormatter hebrewDateFormatter = new HebrewDateFormatter();
    static {
        hebrewDateFormatter.setHebrewFormat(true);
    }
    private final Day[] days;
    private final String yearName;
    private final String monthName;
    private final int monthNumberOfDays;
    private final int headOffsetMonth;
    private boolean isFullMonth;

     /*public Month(Context context,  JewishCalendar jewishCalendar) {
        this.yearName = hebrewDateFormatter.formatHebrewNumber(jewishCalendar.getJewishYear());
        this.monthName = hebrewDateFormatter.formatMonth(jewishCalendar);
        this.monthNumberOfDays = jewishCalendar.getDaysInJewishMonth();
        this.headOffsetMonth = setHeadOffset(jewishCalendar);
        this.isFullMonth = (monthNumberOfDays == 30);

    }*/

    public Month(Context context, int offset) {
        JewishCalendar jewishCalendar = shiftMonth(offset);
        this.yearName = hebrewDateFormatter.formatHebrewNumber(jewishCalendar.getJewishYear());
        this.monthName = hebrewDateFormatter.formatMonth(jewishCalendar);
        this.monthNumberOfDays = jewishCalendar.getDaysInJewishMonth();
        this.headOffsetMonth = setHeadOffset(jewishCalendar);
        this.isFullMonth = (monthNumberOfDays == 30);
        days = isFullMonth ? new Day[30] : new Day[29];
        for (int i = 0 ; i < days.length ; i++){
            jewishCalendar.setJewishDayOfMonth(i + 1);
            String label = hebrewDateFormatter.formatHebrewNumber(i + 1);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(jewishCalendar.getTime());
            ArrayList<String> events = Contract.getInstancesForDate((Activity) context, calendar);
            days[i] = new Day(events, label);
        }
    }

    private int setHeadOffset(JewishCalendar jewishCalendar) {
        int dayInMonth = jewishCalendar.getJewishDayOfMonth() % 7;
        Date date = jewishCalendar.getTime();
        date.setTime(date.getTime() - 1000*60*60*24*(--dayInMonth));
        JewishCalendar mockCalendar = new JewishCalendar(date);
        int dayInWeek = mockCalendar.getDayOfWeek();
        return --dayInWeek;
    }

    private JewishCalendar shiftMonth(int offst){
        JewishCalendar jewishCalendar = new JewishCalendar();
        int currentMonth = jewishCalendar.getJewishMonth();
        int desiredMonth = currentMonth + offst;
        if (desiredMonth < 0){

        }
        else if (desiredMonth < 13){

        }
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, offst);
        return new JewishCalendar(calendar);
    }

    public String getYearName() {
        return yearName;
    }

    public String getMonthName() {
        return monthName;
    }

    public int getMonthNumberOfDays() {
        return monthNumberOfDays;
    }

    public int getHeadOffsetMonth() {
        return headOffsetMonth;
    }

    public Day[] getDays() {
        return days;
    }
}
