package il.co.yshahak.ivricalendar.calendar.jewish;

import android.app.Activity;

import net.sourceforge.zmanim.hebrewcalendar.JewishCalendar;

/**
 * Created by yshahak on 07/10/2016.
 */

public class Year {

    private final Month[] months;
    private final int jewishYear;
    private final boolean isLeapYear;

    public Year(Activity ativity) {
        JewishCalendar jewishCalendar = new JewishCalendar();
        this.isLeapYear = jewishCalendar.isJewishLeapYear();
        this.months = new Month[isLeapYear ? 13 : 12];
        this.jewishYear = jewishCalendar.getJewishYear();
        for (int i = 0 ; i < months.length; i++){
            jewishCalendar.setJewishMonth(i + 1);
//            Month month = new Month(ativity, jewishCalendar);
//            months[i] = month;
        }
    }

    public Month getCurrentMonth() {
        return months[new JewishCalendar().getJewishMonth() - 1];
    }

    public Month getMonth(int offset) {
        int currentMonth = new JewishCalendar().getJewishMonth();
        int desired = currentMonth + offset;
        if (desired < 1 || desired > months.length) {
            return null;
        }
        return months[desired - 1];
    }

    public Month[] getMonths() {
        return months;
    }
}
