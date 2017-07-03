package il.co.yshahak.ivricalendar;

import org.junit.Test;

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
}