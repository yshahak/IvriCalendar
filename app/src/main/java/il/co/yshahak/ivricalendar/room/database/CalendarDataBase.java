package il.co.yshahak.ivricalendar.room.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import il.co.yshahak.ivricalendar.calendar.jewish.Month;
import il.co.yshahak.ivricalendar.room.dao.MonthDao;

/**
 * Created by Yaakov Shahak
 * on 12/07/17.
 */

@Database(entities = {Month.class},  version = 2, exportSchema = false)
public abstract class CalendarDataBase extends RoomDatabase {
    public abstract MonthDao monthDao();
}
