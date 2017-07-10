package il.co.yshahak.ivricalendar.views;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import net.alexandroid.shpref.MyLog;

import java.util.List;

import il.co.yshahak.ivricalendar.calendar.jewish.Day;
import il.co.yshahak.ivricalendar.calendar.jewish.JewCalendar;
import il.co.yshahak.ivricalendar.repo.DaysRepo;

/**
 * Created by Yaakov Shahak
 * on 10/07/17.
 */

public class MonthViewModel extends ViewModel {

    private DaysRepo daysRepo;
    private MutableLiveData<List<Day>> dayList;

    public void setDaysRepo(DaysRepo daysRepo) {
        this.daysRepo = daysRepo;
    }

    public LiveData<List<Day>> getDayList(JewCalendar jewCalendar, int position) {
        if (dayList == null) {
            dayList = new MutableLiveData<>();
            loadDays(jewCalendar, position);
        }
        return dayList;
    }

    private void loadDays(JewCalendar jewCalendar, int position) {
        new Thread(() -> {
            long start = System.currentTimeMillis();
            jewCalendar.shiftMonth(position);
            MyLog.d( "#1 position=" + position + " | elapsed=" + (System.currentTimeMillis() - start));
            start = System.currentTimeMillis();
            List<Day> days = daysRepo.getMonthDays(jewCalendar);
            MyLog.d( "#2 position=" + position + " | elapsed=" + (System.currentTimeMillis() - start));
            dayList.postValue(days);
        }).start();

    }
}
