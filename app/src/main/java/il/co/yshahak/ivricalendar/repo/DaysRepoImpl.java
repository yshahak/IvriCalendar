package il.co.yshahak.ivricalendar.repo;

import android.content.ContentResolver;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    @Inject
    EventsProvider eventsProvider;
    @Inject
    ContentResolver contentResolver;

    @Override
    public List<Day> getMonthDays(JewCalendar jewCalendar) {
        List<Day> days = new ArrayList<>();
        int daysSum = jewCalendar.getDaysInJewishMonth();
        for (int i = 1; i <= daysSum; i++) {
            jewCalendar.setJewishDayOfMonth(i);
            Day day = new Day(jewCalendar);
            days.add(day);
        }
        Cursor cursor = eventsProvider.getEvents(contentResolver, jewCalendar.getBeginOfMonth(), jewCalendar.getEndOfMonth());
        final HashMap<Integer, List<EventInstance>> eventsMap = EventsHelper.getEventsMap(cursor);
        for (int key : eventsMap.keySet()) {
            days.get(key).setGoogleEventInstances(eventsMap.get(key));
        }
        return days;
    }
}
