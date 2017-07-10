package il.co.yshahak.ivricalendar;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.text.format.Time;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Locale;

import il.co.yshahak.ivricalendar.calendar.EventsProviderImpl;
import il.co.yshahak.ivricalendar.calendar.jewish.JewCalendar;

import static org.junit.Assert.assertTrue;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    private Context cycleContext;

    @Before
    public void setUp(){
        cycleContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void testEvents() {
        JewCalendar jewCalendar = new JewCalendar();
        EventsProviderImpl eventsInterface = new EventsProviderImpl();
        ContentResolver cr = cycleContext.getContentResolver();
        Cursor cursor = eventsInterface.getEvents(cr , jewCalendar.getBeginOfMonth(), jewCalendar.getEndOfMonth());
        assertTrue(cursor != null);

    }

    @Test
    public void testDates(){
        JewCalendar jewCalendar = new JewCalendar();
        jewCalendar.getBeginOfMonth();
        jewCalendar.getEndOfMonth();

        jewCalendar.setJewishDayOfMonth(2);

        Time time = new Time();
        time.set(jewCalendar.getTime().getTime());
        time.allDay = true;
        time.hour = 0;
        time.minute = 0;
        time.second = 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.US);
        long begin = Time.getJulianDay(time.toMillis(true), 0);
        Log.d("TAG", "start: " + simpleDateFormat.format(jewCalendar.getTime()));
        System.out.println("start: " + simpleDateFormat.format(begin));
        if (jewCalendar.getDaysInJewishMonth() == 29){
            time.monthDay += 29;
        } else {
            time.monthDay += 30;
        }
        long end = Time.getJulianDay(time.toMillis(true), 0);
        Log.d("TAG", "end: " + simpleDateFormat.format(time.toMillis(true)));

        System.out.println("end: " + simpleDateFormat.format(end));
    }
}
