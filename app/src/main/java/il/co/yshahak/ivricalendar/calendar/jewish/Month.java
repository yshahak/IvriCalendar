package il.co.yshahak.ivricalendar.calendar.jewish;

import android.content.Context;

import net.sourceforge.zmanim.hebrewcalendar.HebrewDateFormatter;
import net.sourceforge.zmanim.hebrewcalendar.JewishCalendar;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by yshahak on 07/10/2016.
 */

public class Month {

    public static HebrewDateFormatter hebrewDateFormatter = new HebrewDateFormatter();

    static {
        hebrewDateFormatter.setHebrewFormat(true);
    }

    private Day[] days;
    private String yearName;
    private String monthName;
    private int monthNumberOfDays;
    private int headOffsetMonth;
    private int trailOffsetMonth;
    private boolean isFullMonth;

    public Month(JewishCalendar jewishCalendar) {
        this.yearName = hebrewDateFormatter.formatHebrewNumber(jewishCalendar.getJewishYear());
        this.monthName = hebrewDateFormatter.formatMonth(jewishCalendar);
        this.monthNumberOfDays = jewishCalendar.getDaysInJewishMonth();
        this.isFullMonth = (monthNumberOfDays == 30);
        this.headOffsetMonth = setHeadOffset(jewishCalendar);
        this.trailOffsetMonth = setTrailOffset(jewishCalendar);
        days = isFullMonth ? new Day[30] : new Day[29];
        for (int i = 0; i < days.length; i++) {
            jewishCalendar.setJewishDayOfMonth(i + 1);
            String label = hebrewDateFormatter.formatHebrewNumber(i + 1);
            days[i] = new Day(label, getBeginAndEnd(jewishCalendar));
        }
    }


    public Month(Context context, JewishCalendar jewishCalendar) {
        init(context, jewishCalendar);
    }

    public Month(Context context, int offset) {
//        JewishCalendar jewishCalendar = shiftMonth(offset);
//        init(context, jewishCalendar);
    }

    private void init(Context context, JewishCalendar jewishCalendar) {
        this.yearName = hebrewDateFormatter.formatHebrewNumber(jewishCalendar.getJewishYear());
        this.monthName = hebrewDateFormatter.formatMonth(jewishCalendar);
        this.monthNumberOfDays = jewishCalendar.getDaysInJewishMonth();
        this.isFullMonth = (monthNumberOfDays == 30);
        this.headOffsetMonth = setHeadOffset(jewishCalendar);
        this.trailOffsetMonth = setTrailOffset(jewishCalendar);
        days = isFullMonth ? new Day[30] : new Day[29];
//        for (int i = 0; i < days.length; i++) {
//            jewishCalendar.setJewishDayOfMonth(i + 1);
//            String label = hebrewDateFormatter.formatHebrewNumber(i + 1);
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(jewishCalendar.getTime());
//            ArrayList<String> events = Contract.getInstancesForDate((Activity) context, calendar);
//            days[i] = new Day(events, label);
//        }
    }

    private int setHeadOffset(JewishCalendar jewishCalendar) {
        int dayInMonth = jewishCalendar.getJewishDayOfMonth() % 7;
        Date date = jewishCalendar.getTime();
        date.setTime(date.getTime() - 1000 * 60 * 60 * 24 * (--dayInMonth));
        JewishCalendar mockCalendar = new JewishCalendar(date);
        int dayInWeek = mockCalendar.getDayOfWeek();
        return --dayInWeek;
    }

    private int setTrailOffset(JewishCalendar jewishCalendar) {
        JewishCalendar mock = new JewishCalendar(jewishCalendar.getTime());
        mock.setJewishDayOfMonth(isFullMonth ? 30 : 29);
        int dayOfWeek = mock.getDayOfWeek();
        return 7 - dayOfWeek;
    }

    public static JewishCalendar shiftMonth(JewishCalendar jewishCalendar, int offst) {
        int currentMonth = jewishCalendar.getJewishMonth();
        int currentYear = jewishCalendar.getJewishYear();

        int desire = currentMonth + offst;
        if (currentMonth < 7 && desire >= 7) {
            jewishCalendar.setJewishYear(++currentYear);
        } else if (currentMonth > 6 && desire <= 6) {
            jewishCalendar.setJewishYear(--currentYear);
        }

        if (desire < 1) {
            boolean leap = jewishCalendar.isJewishLeapYear();
            desire += leap ? 13 : 12;
            if (desire <= 6 && leap){
                leap =  false;
            }
            jewishCalendar.setJewishMonth(leap ? 13 : 12);
            return shiftMonth(jewishCalendar, desire - (leap ? 13 : 12));
        } else if (desire > (jewishCalendar.isJewishLeapYear() ? 13 : 12)) {
            boolean leap = jewishCalendar.isJewishLeapYear();
            if (desire > 6 && leap){
                leap =  false;
                desire--;
            }
            desire -= leap ? 12 : 13;
            jewishCalendar.setJewishMonth(1);
            return shiftMonth(jewishCalendar, desire);
        }

        jewishCalendar.setJewishMonth(desire);
        return jewishCalendar;
    }


   /* public static JewishCalendar shiftMonth(int offst){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, offst);
        return new JewishCalendar(calendar);
    }*/

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

    public int getTrailOffsetMonth() {
        return trailOffsetMonth;
    }

    public Day[] getDays() {
        return days;
    }

    /*private long[] getBeginAndEnd(JewishCalendar jewishCalendar){
        long[] longs = new long[2];
        Time time = new Time();
        time.set(jewishCalendar.getTime().getTime());
        time.allDay = true;
        time.hour = 0;
        time.minute = 0;
        time.second = 0;
        longs[0] = Time.getJulianDay(time.toMillis(true), 0);
        time.monthDay +=1;
        longs[1] = Time.getJulianDay(time.toMillis(true), 0);
        return longs;
    }*/

    private long[] getBeginAndEnd(JewishCalendar jewishCalendar) {
        long[] longs = new long[2];
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(jewishCalendar.getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        longs[0] = calendar.getTimeInMillis();
        ;
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        longs[1] = calendar.getTimeInMillis();
        return longs;
    }
}
