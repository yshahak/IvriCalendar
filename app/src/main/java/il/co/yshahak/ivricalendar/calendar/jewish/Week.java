package il.co.yshahak.ivricalendar.calendar.jewish;

import net.sourceforge.zmanim.hebrewcalendar.JewishCalendar;

import java.util.Date;

/**
 * Created by B.E.L on 22/11/2016.
 */

public class Week {

    private final static long DAY_IN_MS = 1000 * 60 *60 * 24;

    private Day[] days = new Day[7];

    public Week(JewishCalendar jewishCalendar, int offst) {
        Date date = jewishCalendar.getTime();
        date.setTime(date.getTime() + offst * 7 * DAY_IN_MS);
        jewishCalendar = new JewishCalendar(date);
        int dayOfWeek = jewishCalendar.getDayOfWeek();
        for (int i = 0 ; i < 7 ; i++){
            date = jewishCalendar.getTime();
            date.setTime(date.getTime() + (i + 1 - dayOfWeek) * DAY_IN_MS);
            JewishCalendar calendar = new JewishCalendar(date);
            days[i] = new Day(calendar);
        }

    }

    public Day[] getDays() {
        return days;
    }
}
