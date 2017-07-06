package il.co.yshahak.ivricalendar.repo;

import java.util.List;

import il.co.yshahak.ivricalendar.calendar.jewish.Day;
import il.co.yshahak.ivricalendar.calendar.jewish.JewCalendar;

/**
 * Created by Yaakov Shahak
 * on 06/07/17.
 */

public interface DaysRepo {

    List<Day> getMonthDays(JewCalendar jewCalendar);

}
