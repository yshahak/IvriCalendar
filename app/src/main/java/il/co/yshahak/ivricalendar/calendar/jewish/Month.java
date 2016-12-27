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
 * Created by yshahak on 07/10/2016.
 */

public class Month extends JewishCalendar implements Parcelable {

    public static HebrewDateFormatter hebrewDateFormatter = new HebrewDateFormatter();

    static {
        hebrewDateFormatter.setHebrewFormat(true);
    }
//    private JewishCalendar jewishCalendar;
    private List<Day> days = new ArrayList<>();
    private String yearName;
    private String monthName;
    private int monthNumberOfDays;
    private int headOffsetMonth;
    private int trailOffsetMonth;
    private boolean isFullMonth;

    public Month(){

    }

    public Month(int offset) {
        Month month = new Month();
        int currentMonth = month.getJewishMonth();
        int currentYear = month.getJewishYear();

        int desire = currentMonth + offset;
        if (currentMonth < 7 && desire >= 7) {
            month.setJewishYear(++currentYear);
        } else if (currentMonth > 6 && desire <= 6) {
            month.setJewishYear(--currentYear);
        }
        if (desire < 1) {
            boolean leap = month.isJewishLeapYear();
            desire += leap ? 13 : 12;
            if (desire <= 6 && leap){
                leap =  false;
            }
            month.setJewishMonth(leap ? 13 : 12);
            new Month(desire - (leap ? 13 : 12));
        } else if (desire > (month.isJewishLeapYear() ? 13 : 12)) {
            boolean leap = month.isJewishLeapYear();
            if (desire > 6 && leap){
                leap =  false;
                desire--;
            }
            desire -= leap ? 12 : 13;
            month.setJewishMonth(1);
            new Month(desire);
        }
        month.setJewishMonth(desire);
        init(offset == 0);
    }


    private void init(boolean isCurrentMonth) {
        Calendar calendar = null;
        int dayOfMonth = 0;
        if (isCurrentMonth){
            calendar = Calendar.getInstance();
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        }
        this.yearName = hebrewDateFormatter.formatHebrewNumber(getJewishYear());
        this.monthName = hebrewDateFormatter.formatMonth(this);
        this.monthNumberOfDays = getDaysInJewishMonth();
        this.isFullMonth = (monthNumberOfDays == 30);
        this.headOffsetMonth = setHeadOffset(this);
        this.trailOffsetMonth = setTrailOffset(this);
        int days = isFullMonth ? 30 : 29;

        for (int i = 0; i < days; i++) {
            setJewishDayOfMonth(i + 1);
            this.days.add(new Day((JewishCalendar) clone()));
            if (isCurrentMonth){
                calendar.setTime(getTime());
                if (dayOfMonth == calendar.get(Calendar.DAY_OF_MONTH)){
                    FragmentMonth.currentDay = this.days.get(i);
                }
            }
        }
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

    public List<Day> getDays() {
        return days;
    }

//    public JewishCalendar getJewishCalendar() {
//        return jewishCalendar;
//    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeParcelable(this.jewishCalendar, flags);
        dest.writeList(this.days);
        dest.writeString(this.yearName);
        dest.writeString(this.monthName);
        dest.writeInt(this.monthNumberOfDays);
        dest.writeInt(this.headOffsetMonth);
        dest.writeInt(this.trailOffsetMonth);
        dest.writeByte(this.isFullMonth ? (byte) 1 : (byte) 0);
    }

    protected Month(Parcel in) {
//        this.jewishCalendar = in.readParcelable(JewishCalendar.class.getClassLoader());
        this.days = new ArrayList<>();
        in.readList(this.days, Day.class.getClassLoader());
        this.yearName = in.readString();
        this.monthName = in.readString();
        this.monthNumberOfDays = in.readInt();
        this.headOffsetMonth = in.readInt();
        this.trailOffsetMonth = in.readInt();
        this.isFullMonth = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Month> CREATOR = new Parcelable.Creator<Month>() {
        @Override
        public Month createFromParcel(Parcel source) {
            return new Month(source);
        }

        @Override
        public Month[] newArray(int size) {
            return new Month[size];
        }
    };
}
