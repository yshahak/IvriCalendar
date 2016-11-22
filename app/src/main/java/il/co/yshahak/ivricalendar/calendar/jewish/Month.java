package il.co.yshahak.ivricalendar.calendar.jewish;

import android.util.Log;

import net.sourceforge.zmanim.hebrewcalendar.HebrewDateFormatter;
import net.sourceforge.zmanim.hebrewcalendar.JewishCalendar;

import java.util.Date;

/**
 * Created by yshahak on 07/10/2016.
 */

public class Month {

    public static HebrewDateFormatter hebrewDateFormatter = new HebrewDateFormatter();

    static {
        hebrewDateFormatter.setHebrewFormat(true);
    }
    private JewishCalendar jewishCalendar;
    private Day[] days;
    private String yearName;
    private String monthName;
    private int monthNumberOfDays;
    private int headOffsetMonth;
    private int trailOffsetMonth;
    private boolean isFullMonth;
    private boolean isCurrentMonth;

    public Month(JewishCalendar jewishCalendar, boolean isCurrentMonth) {
        this.jewishCalendar = (JewishCalendar) jewishCalendar.clone();
        this.yearName = hebrewDateFormatter.formatHebrewNumber(jewishCalendar.getJewishYear());
        this.monthName = hebrewDateFormatter.formatMonth(jewishCalendar);
        this.monthNumberOfDays = jewishCalendar.getDaysInJewishMonth();
        this.isFullMonth = (monthNumberOfDays == 30);
        this.headOffsetMonth = setHeadOffset(jewishCalendar);
        this.trailOffsetMonth = setTrailOffset(jewishCalendar);
        days = isFullMonth ? new Day[30] : new Day[29];

        for (int i = 0; i < days.length; i++) {
            jewishCalendar.setJewishDayOfMonth(i + 1);
            days[i] = new Day((JewishCalendar) jewishCalendar.clone());
        }
        this.isCurrentMonth = isCurrentMonth;
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
        Log.d("TAG",  hebrewDateFormatter.formatHebrewNumber(jewishCalendar.getJewishYear()) + " , "
                + hebrewDateFormatter.formatMonth(jewishCalendar) + " , "
                + hebrewDateFormatter.formatHebrewNumber(jewishCalendar.getJewishDayOfMonth()));
        return jewishCalendar;
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

    public int getTrailOffsetMonth() {
        return trailOffsetMonth;
    }

    public Day[] getDays() {
        return days;
    }

    public JewishCalendar getJewishCalendar() {
        return jewishCalendar;
    }

    public boolean isCurrentMonth() {
        return isCurrentMonth;
    }

}
