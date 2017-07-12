package il.co.yshahak.ivricalendar.room.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import il.co.yshahak.ivricalendar.calendar.jewish.Day;
import il.co.yshahak.ivricalendar.calendar.jewish.Month;
import il.co.yshahak.ivricalendar.room.dao.DayDao;
import il.co.yshahak.ivricalendar.room.dao.MonthDao;

/**
 * Created by Yaakov Shahak
 * on 12/07/17.
 */

@Database(entities = {Month.class, Day.class},  version = 4, exportSchema = false)
public abstract class CalendarDataBase extends RoomDatabase {
    public abstract MonthDao monthDao();
    public abstract DayDao dayDao();
}
