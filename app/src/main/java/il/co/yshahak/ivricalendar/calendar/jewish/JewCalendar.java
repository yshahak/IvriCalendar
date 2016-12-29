package il.co.yshahak.ivricalendar.calendar.jewish;

import android.os.Parcel;
import android.os.Parcelable;

import net.sourceforge.zmanim.hebrewcalendar.HebrewDateFormatter;
import net.sourceforge.zmanim.hebrewcalendar.JewishCalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import il.co.yshahak.ivricalendar.fragments.FragmentMonth;

/**
 * Created by yshahk on 12/28/2016.
 */

public class JewCalendar extends JewishCalendar implements Parcelable {

    public static HebrewDateFormatter hebrewDateFormatter = new HebrewDateFormatter();

    static {
        hebrewDateFormatter.setHebrewFormat(true);
    }


    public JewCalendar(int offset){
        shiftMonth(offset);
    }

    public void shiftMonth(int offset){
        if (offset > 0) {
            for (int i = 0; i < offset; i++) {
                int next = getJewishMonth() + 1;
                if (next == 14 || (next == 13 && !isJewishLeapYear())) {
                    next = 1;
                    setJewishYear(getJewishYear() + 1);
                }
                setJewishMonth(next);
            }
        } else if (offset < 0){
            for (int i = offset * (-1); i > 0; i--) {
                int previous = getJewishMonth() - 1;
                if (previous == 0) {
                    setJewishYear(getJewishYear() - 1);
                    previous = isJewishLeapYear() ? 13 : 12;
                }
                setJewishMonth(previous);
            }
        } else {
            FragmentMonth.currentDayOfMonth = getJewishDayOfMonth();
        }
    }

    public String getYearName(){
        return hebrewDateFormatter.formatHebrewNumber(getJewishYear());
    }

    public String getMonthName(){
        return hebrewDateFormatter.formatMonth(this);
    }

    public boolean isFullMonth(){
        return getDaysInJewishMonth() == 30;
    }

    public int getHeadOffset() {
        int dayInMonth = getJewishDayOfMonth() % 7;
        Date date = getTime();
        date.setTime(date.getTime() - 1000 * 60 * 60 * 24 * (--dayInMonth));
        JewishCalendar mockCalendar = new JewishCalendar(date);
        int dayInWeek = mockCalendar.getDayOfWeek();
        return --dayInWeek;
    }

    public int getTrailOffset() {
        JewishCalendar mock = new JewishCalendar(getTime());
        mock.setJewishDayOfMonth(isFullMonth() ? 30 : 29);
        int dayOfWeek = mock.getDayOfWeek();
        return 7 - dayOfWeek;
    }

    public String getDayLabel() {
        return hebrewDateFormatter.formatHebrewNumber(getJewishDayOfMonth());
    }

    public long getBeginInMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    public long getEndInMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    public List<Day> getDays(int offset){
        List<Day> days = new ArrayList<>();
        Calendar calendar = null;
        int dayOfMonth = 0;
        if (offset == 0){
            calendar = Calendar.getInstance();
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        }
        for (int i = 0; i < getDaysInJewishMonth(); i++) {
            setJewishDayOfMonth(i + 1);
            days.add(new Day((JewishCalendar) clone()));
            if (offset == 0){
                calendar.setTime(getTime());
                if (dayOfMonth == calendar.get(Calendar.DAY_OF_MONTH)){
                    FragmentMonth.currentDay = days.get(i);
                }
            }
        }
        return days;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    protected JewCalendar(Parcel in) {
    }

    public static final Parcelable.Creator<JewCalendar> CREATOR = new Parcelable.Creator<JewCalendar>() {
        @Override
        public JewCalendar createFromParcel(Parcel source) {
            return new JewCalendar(source);
        }

        @Override
        public JewCalendar[] newArray(int size) {
            return new JewCalendar[size];
        }
    };
}
