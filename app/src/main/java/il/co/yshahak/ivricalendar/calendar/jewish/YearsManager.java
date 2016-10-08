package il.co.yshahak.ivricalendar.calendar.jewish;

import net.sourceforge.zmanim.hebrewcalendar.JewishCalendar;

import java.util.ArrayList;

/**
 * Created by yshahak on 07/10/2016.
 */

public class YearsManager {

    private static ArrayList<Year> years = new ArrayList<>();
    private static JewishCalendar currentCalendar = new JewishCalendar();
    private static Year currentYear;
    static {
        currentYear = new Year();
        years.add(currentYear);
    }

    public static Month[] getMonth(int offset){
        Month[] months = new Month[3];
        int currentMonth = currentCalendar.getJewishMonth();
        int desired = currentMonth + offset;
        months[0] = addMonth(desired - 1);
        months[1] = addMonth(desired);
        months[2] = addMonth(desired + 1);
        return months;
    }

    private static Month addMonth(int index){
        if (index < 1 || index > currentYear.getMonths().length) {
            return null;
        } else {
            return currentYear.getMonths()[index];
        }
    }
}
