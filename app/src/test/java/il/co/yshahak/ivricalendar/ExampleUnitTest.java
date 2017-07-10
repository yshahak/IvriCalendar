package il.co.yshahak.ivricalendar;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import il.co.yshahak.ivricalendar.calendar.jewish.Day;
import il.co.yshahak.ivricalendar.calendar.jewish.JewCalendar;

import static junit.framework.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        JewCalendar jewCalendar = new JewCalendar();
        long now = System.currentTimeMillis();
        JewCalendar cal = ((JewCalendar)jewCalendar.clone());
        cal.shiftMonth(10);
//        Month month = new Month(jewCalendar, 10);
        assertEquals((System.currentTimeMillis() - now < 18), true);
//        assertEquals(4, 2 + 2);
    }

    @Test
    public void testTimes(){
        JewCalendar jewCalendar = new JewCalendar();
        jewCalendar.getBeginOfMonth();
        jewCalendar.getEndOfMonth();
    }

    @Test
    public void testDays(){
        JewCalendar jewCalendar = new JewCalendar();
        jewCalendar.setOffsets();
        jewCalendar.setJewishDayOfMonth(1);
        jewCalendar.shiftDay(jewCalendar.getHeadOffset()*(-1));
        List<Day> days = new ArrayList<>();
        for (int i = 0; i < jewCalendar.getHeadOffset(); i++) {
            Day day = new Day(jewCalendar);
            day.setOutOfMonthRange(true);
            jewCalendar.shiftDay(1);
            days.add(day);
        }
        int daysSum = jewCalendar.getDaysInJewishMonth();
        for (int i = 1; i <= daysSum; i++) {
            jewCalendar.setJewishDayOfMonth(i);
            Day day = new Day(jewCalendar);
            days.add(day);
        }
        jewCalendar.shiftDay(1);
        for (int i = 0; i < jewCalendar.getTrailOffset(); i++){
            Day day = new Day(jewCalendar);
            day.setOutOfMonthRange(true);
            jewCalendar.shiftDay(1);
            days.add(day);
        }
    }
}