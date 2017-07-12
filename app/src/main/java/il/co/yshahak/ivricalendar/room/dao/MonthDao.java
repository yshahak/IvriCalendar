package il.co.yshahak.ivricalendar.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import il.co.yshahak.ivricalendar.calendar.jewish.Month;

/**
 * Created by Yaakov Shahak
 * on 12/07/17.
 */

@Dao
public interface MonthDao {

    @Query("SELECT * From Month WHERE hash_code = :hashCode")
    Month getByHashCode(int hashCode);

    @Insert
    void insertMonth(Month month);

    @Query("DELETE FROM Month")
    void deleteAll();
}
