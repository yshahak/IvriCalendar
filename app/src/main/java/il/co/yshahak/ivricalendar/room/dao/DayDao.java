package il.co.yshahak.ivricalendar.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import il.co.yshahak.ivricalendar.calendar.jewish.Day;

/**
 * Created by Yaakov Shahak
 * on 12/07/17.
 */

@Dao
public interface DayDao {

    @Query("SELECT * From Day WHERE id=:hashCode")
    Day getByHashCode(int hashCode);

    @Insert
    void insertDay(Day day);

    @Insert
    void insertAll(List<Day> days);

    @Query("DELETE FROM Day")
    void deleteAll();
}
