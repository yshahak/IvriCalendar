package il.co.yshahak.ivricalendar.calendar.jewish;

import android.app.Activity;

import java.util.ArrayList;

/**
 * Created by yshahak on 07/10/2016.
 */

public class YearsManager {

    private final ArrayList<Year> years = new ArrayList<>();
    private static Year currentYear;


    public YearsManager(Activity context) {
        currentYear = new Year(context);
    }

    public static Month[] getMonthes(Activity context, int offset){
        Month[] months = new Month[3];
        months[0] = new Month(context, offset - 1);
        months[1] = new Month(context, offset);
        months[2] = new Month(context, offset + 1);
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
