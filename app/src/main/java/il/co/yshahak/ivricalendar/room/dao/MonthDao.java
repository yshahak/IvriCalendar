package il.co.yshahak.ivricalendar.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import il.co.yshahak.ivricalendar.calendar.jewish.Month;

/**
 * Created by Yaakov Shahak
 * on 12/07/17.
 */

@Dao
public interface MonthDao {

    @Query("SELECT * From Month WHERE id=:hashCode")
    Month getByHashCode(int hashCode);

    @Query("SELECT id From Month")
    int[] getCount();

    @Insert
    void insertMonth(Month month);

    @Insert
    void insertAll(List<Month> months);

    @Query("DELETE FROM Month")
    void deleteAll();
}
