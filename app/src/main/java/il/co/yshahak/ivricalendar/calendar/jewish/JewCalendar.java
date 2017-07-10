package il.co.yshahak.ivricalendar.calendar.jewish;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.util.Pools;
import android.util.SparseArray;

import net.alexandroid.shpref.MyLog;
import net.sourceforge.zmanim.hebrewcalendar.HebrewDateFormatter;
import net.sourceforge.zmanim.hebrewcalendar.JewishCalendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static il.co.yshahak.ivricalendar.adapters.CalendarPagerAdapter.FRONT_PAGE;

/**
 * Created by yshahak
 * on 12/28/2016.
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class JewCalendar extends JewishCalendar implements Parcelable {

    public static HebrewDateFormatter hebrewDateFormatter = new HebrewDateFormatter();

    static {
        hebrewDateFormatter.setHebrewFormat(true);
    }

    private int headOffst, trailOffse;

    private static final Pools.SynchronizedPool<JewCalendar> sPool = new Pools.SynchronizedPool<>(5);
    private static SparseArray<JewCalendar> jewCalendarSparseArray = new SparseArray<>();
    private int oldPosition;
    private boolean isRcycled;
    public boolean flagCurrentMonth;

    public JewCalendar(int jewishYear, int jewishMonth, int day) {
        super(jewishYear, jewishMonth, day);
    }

    public JewCalendar(Calendar calendar) {
        super(calendar);
    }

    public static JewCalendar obtain() {
        JewCalendar instance = sPool.acquire();
        if ((instance == null)) {
            MyLog.d("create new JewCalendar");
            instance = new JewCalendar();
        }
        instance.isRcycled = false;
        return instance;
    }

    public void recycle() {
        if (!isRcycled) {
            sPool.release(this);
        }
        isRcycled = true;
    }

    public boolean isRecycled() {
        return isRcycled;
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
        jewCalendarSparseArray.put(position + FRONT_PAGE, this);
        int offset = position - oldPosition;
        if (offset > 0) {
//            shiftForward(offset);
            for (int i = 0; i < offset; i++) {
                shiftMonthForward();
            }
        } else if (offset < 0) {
//            shiftBackward(offset);
            for (int i = offset * (-1); i > 0; i--) {
                shiftMonthBackword();
            }
        } else {
            flagCurrentMonth = true;
        }
        setOffsets();
        oldPosition = position;
        return this;
    }

    private void shiftForward(int offset) {
        int current = getJewishMonth();
        int next = getJewishMonth() + offset;
        if (next >= 7 && current < 7) {
            setJewishYear(getJewishYear() + 1);
        } else if (next >= 14 || (next >= 13 && !isJewishLeapYear())) {
            next = next - (isJewishLeapYear() ? 13 : 12);
        }
        setJewishMonth(next);
    }

    private void shiftBackward(int offset) {
        int current = getJewishMonth();
        int previous = getJewishMonth() + offset;
        if (previous <= 0) {
            previous = previous + (isJewishLeapYear() ? 13 : 12);
        } else if (current > 6 && previous <= 6) {
            setJewishYear(getJewishYear() - 1);
        }
        setJewishMonth(previous);
    }


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
            headOffst = --dayInWeek;
        }
        {//calculate trail
            JewishCalendar mock = new JewishCalendar(getTime());
            mock.setJewishDayOfMonth(isFullMonth() ? 30 : 29);
            int dayOfWeek = mock.getDayOfWeek();
            trailOffse = 7 - dayOfWeek;
        }
//        Log.d("TAG", getMonthName() +  ", headOffst:" + headOffst + ", trailOffse:" + trailOffse);
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
        return headOffst;
    }

    public int getTrailOffset() {
        return trailOffse;
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
        JewCalendar copy = (JewCalendar) clone();
        copy.setJewishDayOfMonth(1);
        copy.shiftDay(copy.getHeadOffset()*(-1));
        List<Day> days = new ArrayList<>();
        for (int i = 0; i < copy.getHeadOffset(); i++) {
            Day day = new Day(copy);
            day.setOutOfMonthRange(true);
            copy.shiftDay(1);
            days.add(day);
        }
        int daysSum = copy.getDaysInJewishMonth();
        for (int i = 1; i <= daysSum; i++) {
            copy.setJewishDayOfMonth(i);
            Day day = new Day(copy);
            days.add(day);
        }
        copy.shiftDay(1);
        for (int i = 0; i < copy.getTrailOffset(); i++){
            Day day = new Day(copy);
            day.setOutOfMonthRange(true);
            copy.shiftDay(1);
            days.add(day);
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
        clone.headOffst = this.headOffst;
        clone.trailOffse = this.trailOffse;
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
        copy.shiftForward(1);
        copy.setJewishDate(copy.getJewishYear(), copy.getJewishMonth(), 1);
//        System.out.println(hebrewDateFormatter.format(copy));
        Date date = copy.getTime(true);
//        System.out.println(simpleDateFormat.format(date));
        return date.getTime();
    }

    public static String getTitle(int position) {
        JewCalendar current = jewCalendarSparseArray.get(position);
        if (current == null) {
            MyLog.d("current is null");
            current = new JewCalendar();
        }
        return current.getMonthName() + " , " + current.getYearName();
    }
}
