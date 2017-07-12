package il.co.yshahak.ivricalendar.calendar.jewish;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import net.sourceforge.zmanim.hebrewcalendar.HebrewDateFormatter;
import net.sourceforge.zmanim.hebrewcalendar.JewishCalendar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yshahak on 07/10/2016.
 */
@Entity
public class Month {
    @Ignore
    public static HebrewDateFormatter hebrewDateFormatter = new HebrewDateFormatter();

    static {
        hebrewDateFormatter.setHebrewFormat(true);
    }
    @Ignore
    private List<Day> days = new ArrayList<>();
    @Ignore
    private int monthNumberOfDays;

    @PrimaryKey @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "year_name")
    private String yearName;
    @ColumnInfo(name = "month_name")
    private String monthName;
    @ColumnInfo (name = "head_offset")
    private int headOffst;
    @ColumnInfo(name = "trail_offset")
    private int trailOffse;
    @ColumnInfo(name = "full_month")
    private boolean isFullMonth;
    @ColumnInfo(name = "begin")
    private long beginOfMonth;
    @ColumnInfo(name = "end")
    private long endOfMonth;


    public Month() {
    }

    public Month(JewCalendar jewishCalendar) {
        this.id = jewishCalendar.monthHashCode();
        this.yearName = hebrewDateFormatter.formatHebrewNumber(jewishCalendar.getJewishYear());
        this.monthName = hebrewDateFormatter.formatMonth(jewishCalendar);
        int monthNumberOfDays = jewishCalendar.getDaysInJewishMonth();
        this.isFullMonth = (monthNumberOfDays == 30);
        this.headOffst = jewishCalendar.getHeadOffset();
        this.trailOffse = jewishCalendar.getHeadOffset();
        this.beginOfMonth = jewishCalendar.getBeginOfMonth();
        this.endOfMonth = jewishCalendar.getEndOfMonth();
    }


    public Month(JewCalendar jewishCalendar, int offset) {
        int currrentYear = jewishCalendar.getJewishYear();
        int currentMonth = jewishCalendar.getJewishMonth();
        int curretnDay = jewishCalendar.getJewishDayOfMonth();
        shiftMonth(jewishCalendar, offset);
        this.yearName = hebrewDateFormatter.formatHebrewNumber(jewishCalendar.getJewishYear());
        this.monthName = hebrewDateFormatter.formatMonth(jewishCalendar);
        this.monthNumberOfDays = jewishCalendar.getDaysInJewishMonth();
        this.isFullMonth = (monthNumberOfDays == 30);
        setDays(jewishCalendar);
        setOffsets(jewishCalendar);
        jewishCalendar.setJewishDate(currrentYear, currentMonth, curretnDay);
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
//        Log.d("TAG",  hebrewDateFormatter.formatHebrewNumber(jewishCalendar.getJewishYear()) + " , "
//                + hebrewDateFormatter.formatMonth(jewishCalendar) + " , "
//                + hebrewDateFormatter.formatHebrewNumber(jewishCalendar.getJewishDayOfMonth()));
        return jewishCalendar;
    }

    public void shiftMonth(JewCalendar jewishCalendar, int offset){
        if (offset > 0) {
            for (int i = 0; i < offset; i++) {
                shiftMonthForward(jewishCalendar);
            }
        } else if (offset < 0){
            for (int i = offset * (-1); i > 0; i--) {
                shiftMonthBackword(jewishCalendar);
            }
        }
    }

    public void shiftMonthForward(JewCalendar jewishCalendar){
        int next = jewishCalendar.getJewishMonth() + 1;
        if (next == 14 || (next == 13 && !jewishCalendar.isJewishLeapYear())) {
            next = 1;
            jewishCalendar.setJewishYear(jewishCalendar.getJewishYear() + 1);
        }
        jewishCalendar.setJewishMonth(next);
    }

    public void shiftMonthBackword(JewCalendar jewishCalendar) {
        int previous = jewishCalendar.getJewishMonth() - 1;
        if (previous == 0) {
            jewishCalendar.setJewishYear(jewishCalendar.getJewishYear() - 1);
            previous = jewishCalendar.isJewishLeapYear() ? 13 : 12;
        }
        jewishCalendar.setJewishMonth(previous);
    }

    private void setOffsets(JewCalendar jewishCalendar){
        { //calculate head
            int dayInMonth = jewishCalendar.getJewishDayOfMonth() % 7;
            Date date = jewishCalendar.getTime();
            date.setTime(date.getTime() - 1000 * 60 * 60 * 24 * (--dayInMonth));
            JewishCalendar mockCalendar = new JewishCalendar(date);
            int dayInWeek = mockCalendar.getDayOfWeek();
            headOffst = --dayInWeek;
        }
        {//calculate trail
            JewishCalendar mock = new JewishCalendar(jewishCalendar.getTime());
            mock.setJewishDayOfMonth(jewishCalendar.isFullMonth() ? 30 : 29);
            int dayOfWeek = mock.getDayOfWeek();
            trailOffse =  7 - dayOfWeek;
        }
//        Log.d("TAG", getMonthName() +  ", headOffst:" + headOffst + ", trailOffse:" + trailOffse);
    }

    private void setDays(JewCalendar jewCalendar) {
        int daysSum = isFullMonth ? 30 : 29;
        for (int i = 1; i <= daysSum ; i++){
            jewCalendar.setJewishDayOfMonth(i);
            Day day = new Day(jewCalendar);
            days.add(day);
        }
    }

    public void setMonthNumberOfDays(int monthNumberOfDays) {
        this.monthNumberOfDays = monthNumberOfDays;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setYearName(String yearName) {
        this.yearName = yearName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    public void setHeadOffst(int headOffst) {
        this.headOffst = headOffst;
    }

    public void setTrailOffse(int trailOffse) {
        this.trailOffse = trailOffse;
    }

    public void setFullMonth(boolean fullMonth) {
        isFullMonth = fullMonth;
    }

    public void setBeginOfMonth(long beginOfMonth) {
        this.beginOfMonth = beginOfMonth;
    }

    public void setEndOfMonth(long endOfMonth) {
        this.endOfMonth = endOfMonth;
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

    public List<Day> getDays() {
        return days;
    }

    public static HebrewDateFormatter getHebrewDateFormatter() {
        return hebrewDateFormatter;
    }

    public int getId() {
        return id;
    }

    public int getHeadOffst() {
        return headOffst;
    }

    public int getTrailOffse() {
        return trailOffse;
    }

    public boolean isFullMonth() {
        return isFullMonth;
    }

    public long getBeginOfMonth() {
        return beginOfMonth;
    }

    public long getEndOfMonth() {
        return endOfMonth;
    }
}