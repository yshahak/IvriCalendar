package il.co.yshahak.ivricalendar.repo;

import android.content.ContentResolver;
import android.database.Cursor;

import net.alexandroid.shpref.MyLog;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import il.co.yshahak.ivricalendar.calendar.EventsHelper;
import il.co.yshahak.ivricalendar.calendar.EventsProvider;
import il.co.yshahak.ivricalendar.calendar.google.EventInstance;
import il.co.yshahak.ivricalendar.calendar.jewish.Day;
import il.co.yshahak.ivricalendar.calendar.jewish.JewCalendar;

/**
 * Created by Yaakov Shahak
 * on 06/07/17.
 */

public class DaysRepoImpl implements DaysRepo {

    private final EventsProvider eventsProvider;
    private final ContentResolver contentResolver;

    @Inject
    public DaysRepoImpl(EventsProvider eventsProvider, ContentResolver contentResolver) {
        this.eventsProvider = eventsProvider;
        this.contentResolver = contentResolver;
    }
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.US);

    @Override
    public List<Day> getMonthDays(JewCalendar jewCalendar) {
        MyLog.d("getDays");
        List<Day> days = jewCalendar.getMonthDays();
        long start = days.get(0).getStartDayInMillis();
//        MyLog.d(simpleDateFormat.format(new Date(start)));
        long end = days.get(days.size() - 1).getEndDayInMillis();
        Cursor cur = eventsProvider.getEvents(contentResolver, start, end);
        if (cur.moveToFirst()){
            do {
                EventInstance eventInstance = EventsHelper.convertCursorToEvent(cur);
                for (Day day : days){
                    if (eventInstance.getBegin() >= day.getStartDayInMillis() && eventInstance.getEnd() <= day.getEndDayInMillis()){
                        day.getGoogleEventInstances().add(eventInstance);
                        break;
                    }
//                    MyLog.w("event not matched month range:" + eventInstance.toString());
                }
            }while (cur.moveToNext());
        }
        return days;
    }
}
