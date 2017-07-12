package il.co.yshahak.ivricalendar.calendar.jewish;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import net.alexandroid.shpref.MyLog;
import net.sourceforge.zmanim.hebrewcalendar.JewishCalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import il.co.yshahak.ivricalendar.calendar.google.EventInstance;

import static il.co.yshahak.ivricalendar.calendar.jewish.Month.hebrewDateFormatter;

/**
 * Created by yshahak on 09/10/2016.
 */
@Entity
public class Day {

    public static final String ID = "id";
    public static final String LABEL = "label";
    public static final String START_DAT_IN_MILLIS = "startDatInMillis";
    public static final String END_DAY_IN_MILLIS = "endDayInMillis";
    public static final String DAY_IN_MONTH = "dayInMonth";
    public static final String IS_OUT_OF_MONTH_RANGE = "isOutOfMonthRange";
    @Ignore
    private static Calendar calendar = Calendar.getInstance();
    @Ignore
    private List<EventInstance> googleEventInstances = new ArrayList<>();
    @PrimaryKey @ColumnInfo(name = ID)
    int id;
    @ColumnInfo(name = LABEL)
    private String label;
    @ColumnInfo(name = START_DAT_IN_MILLIS)
    private long startDayInMillis;
    @ColumnInfo(name = END_DAY_IN_MILLIS)
    private long endDayInMillis;
    @ColumnInfo(name = DAY_IN_MONTH)
    private int dayInMonth;
    @ColumnInfo(name = IS_OUT_OF_MONTH_RANGE)
    private boolean isOutOfMonthRange;

    public Day() {
    }

    public Day(JewCalendar jewishCalendar) {
        this.dayInMonth = jewishCalendar.getJewishDayOfMonth();
        id = jewishCalendar.monthHashCode() + dayInMonth;
        MyLog.d("id=" + id);
        this.label = hebrewDateFormatter.formatHebrewNumber(dayInMonth);
        setBeginAndEnd(jewishCalendar);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setStartDayInMillis(long startDayInMillis) {
        this.startDayInMillis = startDayInMillis;
    }

    public void setEndDayInMillis(long endDayInMillis) {
        this.endDayInMillis = endDayInMillis;
    }

    public void setDayInMonth(int dayInMonth) {
        this.dayInMonth = dayInMonth;
    }

    public int getId() {
        return id;
    }

    public List<EventInstance> getGoogleEventInstances() {
        return googleEventInstances;
    }

    public void setGoogleEventInstances(List<EventInstance> googleEventInstances) {
        this.googleEventInstances = googleEventInstances;
    }

    public String getLabel() {
        return label;
    }

    public long getStartDayInMillis() {
        return startDayInMillis;
    }

    public int getDayInMonth() {
        return dayInMonth;
    }

    public long getEndDayInMillis() {
        return endDayInMillis;
    }

    public void setOutOfMonthRange(boolean outOfMonthRange) {
        isOutOfMonthRange = outOfMonthRange;
    }

    public boolean isOutOfMonthRange() {
        return isOutOfMonthRange;
    }

    public void setBeginAndEnd(JewishCalendar jewishCalendar) {
        calendar.set(jewishCalendar.getGregorianYear(), jewishCalendar.getGregorianMonth(), jewishCalendar.getGregorianDayOfMonth());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        this.startDayInMillis = calendar.getTimeInMillis();
        this.endDayInMillis = startDayInMillis + TimeUnit.DAYS.toMillis(1);
    }

    public void setBeginAndEnd(Day day) {
        this.startDayInMillis = day.endDayInMillis;
        this.endDayInMillis = startDayInMillis + TimeUnit.DAYS.toMillis(1);
    }
}
