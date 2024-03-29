package il.co.yshahak.ivricalendar.calendar.jewish;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.util.Pools;

import net.alexandroid.shpref.MyLog;
import net.sourceforge.zmanim.hebrewcalendar.HebrewDateFormatter;
import net.sourceforge.zmanim.hebrewcalendar.JewishCalendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import il.co.yshahak.ivricalendar.room.database.CalendarDataBase;

import static il.co.yshahak.ivricalendar.adapters.CalendarPagerAdapter.FRONT_PAGE;

/**
 * Created by yshahak
 * on 12/28/2016.
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class JewCalendar extends JewishCalendar implements Parcelable {

    @Inject
    CalendarDataBase calendarDataBase;

    public static HebrewDateFormatter hebrewDateFormatter = new HebrewDateFormatter();
    private static JewCalendar calculatorCalendar = new JewCalendar();
    static {
        hebrewDateFormatter.setHebrewFormat(true);
    }

    private int headOffset, trailOffset;

    private static final int POOL_SIZE = 5;
    private static final Pools.SynchronizedPool<JewCalendar> sPool = new Pools.SynchronizedPool<>(POOL_SIZE);
    private static HashMap<Integer, JewCalendar> jewCalendarMap = new HashMap<>();
    private int oldPosition;
    private boolean isRecycled;
    public boolean flagCurrentMonth;
    private List<Day> dayList = new ArrayList<>();

    public JewCalendar(int jewishYear, int jewishMonth, int day) {
        super(jewishYear, jewishMonth, day);
    }

    public JewCalendar(Calendar calendar) {
        super(calendar);
    }

    public static void initPool(){
        List<JewCalendar> calendars = new ArrayList<>();
        for (int i = 0 ; i < POOL_SIZE; i++){
            JewCalendar instance = obtain();
            instance.shiftMonth(POOL_SIZE / 2 + i);
            calendars.add(instance);
        }
        for (JewCalendar jewCalendar: calendars){
            jewCalendar.recycle();
        }
    }

    public static JewCalendar obtain() {
        JewCalendar instance = sPool.acquire();
        if ((instance == null)) {
            MyLog.d("create new JewCalendar");
            instance = new JewCalendar();
        }
        instance.isRecycled = false;
        return instance;
    }

    public void recycle() {
        if (!isRecycled) {
            sPool.release(this);
        }
        isRecycled = true;
    }

    public boolean isRecycled() {
        return isRecycled;
    }

    public JewCalendar(int offset) {
        shiftMonth(offset);
    }

    public JewCalendar(Date date) {
        super(date);
        setOffsets();
    }

    public JewCalendar() {
        setOffsets();
    }

    public JewCalendar shiftMonth(int position) {
        jewCalendarMap.put(position + FRONT_PAGE, this);
        int offset = position - oldPosition;
        MyLog.d("offset=" + offset);
        if (offset > 0) {
            for (int i = 0; i < offset; i++) {
                shiftMonthForward();
            }
        } else if (offset < 0) {
            for (int i = offset * (-1); i > 0; i--) {
                shiftMonthBackword();
            }
        } else {
            flagCurrentMonth = true;
        }
//        setOffsets();
//        setMonthDays();
        oldPosition = position;
        return this;
    }

//    private void shiftForward(int offset) {
//        int current = getJewishMonth();
//        int next = getJewishMonth() + offset;
//        if (next >= 7 && current < 7) {
//            setJewishYear(getJewishYear() + 1);
//        } else if (next >= 14 || (next >= 13 && !isJewishLeapYear())) {
//            next = next - (isJewishLeapYear() ? 13 : 12);
//        }
//        setJewishMonth(next);
//    }
//
//    private void shiftBackward(int offset) {
//        int current = getJewishMonth();
//        int previous = getJewishMonth() + offset;
//        if (previous <= 0) {
//            previous = previous + (isJewishLeapYear() ? 13 : 12);
//        } else if (current > 6 && previous <= 6) {
//            setJewishYear(getJewishYear() - 1);
//        }
//        setJewishMonth(previous);
//    }


    public void shiftDay(int offset) {
        if (offset > 0) {
            for (int i = 0; i < offset; i++) {
                shiftDayForward();
            }
        } else if (offset < 0) {
            for (int i = offset * (-1); i > 0; i--) {
                shiftDayBackword();
            }
        }
    }

    private void shiftMonthForward(int offset) {
        int currentMonth = getJewishMonth();
        int next = getJewishMonth() + 1;
        if (next == 7) {
            setJewishYear(getJewishYear() + 1);
        } else if (next == 14 || (next == 13 && !isJewishLeapYear())) {
            next = 1;
        }
        setJewishMonth(next);
    }

    public void shiftMonthForward() {
        int next = getJewishMonth() + 1;
        if (next == 7) {
            setJewishYear(getJewishYear() + 1);
        } else if (next == 14 || (next == 13 && !isJewishLeapYear())) {
            next = 1;
        }
        setJewishMonth(next);
    }

    public void shiftMonthBackword() {
        int previous = getJewishMonth() - 1;
        if (previous == 0) {
            previous = isJewishLeapYear() ? 13 : 12;
        } else if (previous == 6) {
            setJewishYear(getJewishYear() - 1);
        }
        setJewishMonth(previous);
    }

    private void shiftDayForward() {
        int next = getJewishDayOfMonth() + 1;
        if (next == 31 || (next == 30 && !isFullMonth())) {
            next = 1;
            shiftMonthForward();
        }
        setJewishDayOfMonth(next);
    }

    private void shiftDayBackword() {
        int previous = getJewishDayOfMonth() - 1;
        if (previous == 0) {
            shiftMonthBackword();
            previous = isFullMonth() ? 30 : 29;
        }
        setJewishDayOfMonth(previous);
    }


    public void setOffsets() {
        { //calculate head
            int dayInMonth = getJewishDayOfMonth() % 7;
            Date date = getTime();
            date.setTime(date.getTime() - 1000 * 60 * 60 * 24 * (--dayInMonth));
            JewishCalendar mockCalendar = new JewishCalendar(date);
            int dayInWeek = mockCalendar.getDayOfWeek();
            headOffset = --dayInWeek;
        }
        {//calculate trail
            JewishCalendar mock = new JewishCalendar(getTime());
            mock.setJewishDayOfMonth(isFullMonth() ? 30 : 29);
            int dayOfWeek = mock.getDayOfWeek();
            trailOffset = 7 - dayOfWeek;
        }
//        Log.d("TAG", getMonthName() +  ", headOffset:" + headOffset + ", trailOffset:" + trailOffset);
    }

    public void setHour(int hour) {
        if (hour >= 0 && hour < 24) {
            setJewishDate(getJewishYear(), getJewishMonth(), getJewishDayOfMonth(), hour, getTime().getMinutes(), 0);
        }
    }

    public String getYearName() {
        MyLog.d("year=" + getJewishYear());
        return hebrewDateFormatter.formatHebrewNumber(getJewishYear());
    }

    public String getMonthName() {
        return hebrewDateFormatter.formatMonth(this);
    }

    public boolean isFullMonth() {
        return getDaysInJewishMonth() == 30;
    }

    public int getHeadOffset() {
        return headOffset;
    }

    public int getTrailOffset() {
        return trailOffset;
    }

    public String getDayLabel() {
        return hebrewDateFormatter.formatHebrewNumber(getJewishDayOfMonth());
    }

    public long getBeginOfDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public long getEndOfDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    public List<Day> getMonthDays() {
        return dayList;
    }

    public void setMonthDays() {
        dayList.clear();
        JewCalendar copy = this;
        copy.setJewishDayOfMonth(1);
        copy.shiftDay(copy.getHeadOffset()*(-1));
        for (int i = 0; i < copy.getHeadOffset(); i++) {
            Day day = new Day(copy);
            day.setOutOfMonthRange(true);
            copy.shiftDay(1);
            if (dayList.size() ==0 ){
                day.setBeginAndEnd(copy);
            } else {
                day.setBeginAndEnd(dayList.get(dayList.size() - 1));
            }
            dayList.add(day);
        }
        int daysSum = copy.getDaysInJewishMonth();
        for (int i = 1; i <= daysSum; i++) {
            copy.setJewishDayOfMonth(i);
            Day day = new Day(copy);
            if (dayList.size() ==0 ){
                day.setBeginAndEnd(copy);
            } else {
                day.setBeginAndEnd(dayList.get(dayList.size() - 1));
            }
            dayList.add(day);
        }
        copy.shiftDay(1);
        for (int i = 0; i < copy.getTrailOffset(); i++){
            Day day = new Day(copy);
            day.setOutOfMonthRange(true);
            day.setBeginAndEnd(dayList.get(dayList.size() - 1));
            copy.shiftDay(1);
            dayList.add(day);
        }
        copy.shiftMonthBackword();
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

    public static int getDaysDifference(JewCalendar baseCalendar, JewCalendar compareCalendar) {
        int shift = 0, sign;
        if (baseCalendar.getJewishYear() != compareCalendar.getJewishYear()) {
            sign = baseCalendar.getJewishYear() > compareCalendar.getJewishYear() ? 1 : -1;
            while (baseCalendar.getJewishYear() != compareCalendar.getJewishYear()) {
                compareCalendar.shiftDay(sign);
                shift += sign;
            }
        }
        if (baseCalendar.getJewishMonth() != compareCalendar.getJewishMonth()) {
            sign = baseCalendar.getJewishMonth() > compareCalendar.getJewishMonth() ? 1 : -1;
            while (baseCalendar.getJewishMonth() != compareCalendar.getJewishMonth()) {
                compareCalendar.shiftDay(sign);
                shift += sign;
            }

        }
        if (baseCalendar.getJewishDayOfMonth() != compareCalendar.getJewishDayOfMonth()) {
            sign = baseCalendar.getJewishDayOfMonth() > compareCalendar.getJewishDayOfMonth() ? 1 : -1;
            while (baseCalendar.getJewishDayOfMonth() != compareCalendar.getJewishDayOfMonth()) {
                compareCalendar.shiftDay(sign);
                shift += sign;
            }
        }
        return shift;
    }

    public static int getMonthDifference(JewCalendar baseCalendar, JewCalendar compareCalendar) {
        int shift = 0, sign;
        if (baseCalendar.getJewishYear() != compareCalendar.getJewishYear()) {
            sign = baseCalendar.getJewishYear() > compareCalendar.getJewishYear() ? 1 : -1;
            while (baseCalendar.getJewishYear() != compareCalendar.getJewishYear()) {
                compareCalendar.shiftMonth(sign);
                shift += sign;
            }
        }
        if (baseCalendar.getJewishMonth() != compareCalendar.getJewishMonth()) {
            sign = baseCalendar.getJewishMonth() > compareCalendar.getJewishMonth() ? 1 : -1;
            while (baseCalendar.getJewishMonth() != compareCalendar.getJewishMonth()) {
                compareCalendar.shiftMonth(sign);
                shift += sign;
            }

        }
        return shift;
    }

    public Object clone() {
        JewCalendar clone;
        clone = (JewCalendar) super.clone();
        clone.headOffset = this.headOffset;
        clone.trailOffset = this.trailOffset;
        return clone;
    }

    public static int getDayOfMonth(long date) {
        JewCalendar jewCalendar = new JewCalendar(new Date(date));
        return jewCalendar.getJewishDayOfMonth();
    }


    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.US);

    public Date getTime(boolean reset) {
        if (reset) {
            Calendar cal = Calendar.getInstance();
            cal.set(this.getGregorianYear(), this.getGregorianMonth(), getGregorianDayOfMonth(), 0, 0, 0);
            return cal.getTime();
        } else {
            return super.getTime();
        }
    }

    public long getBeginOfMonth() {
        JewCalendar copy = (JewCalendar) clone();
        copy.setJewishDate(getJewishYear(), getJewishMonth(), 1);
//        System.out.println(hebrewDateFormatter.format(copy));
        Date date = copy.getTime(true);
//        System.out.println(simpleDateFormat.format(date));
        return date.getTime();
    }

    public long getEndOfMonth() {
        JewCalendar copy = (JewCalendar) clone();
        copy.shiftMonthForward(1);
        copy.setJewishDate(copy.getJewishYear(), copy.getJewishMonth(), 1);
//        System.out.println(hebrewDateFormatter.format(copy));
        Date date = copy.getTime(true);
//        System.out.println(simpleDateFormat.format(date));
        return date.getTime();
    }

    public static String getTitle(int position) {
        JewCalendar current = jewCalendarMap.get(position);
        if (current == null) {
            MyLog.d("current is null");
            current = new JewCalendar();
        }
        return current.getMonthName() + " , " + current.getYearName();
    }

    public int monthHashCode() {
        return (getJewishYear() - 5700) * 1000 + getJewishMonth() * 100;
    }
}
