package il.co.yshahak.ivricalendar.views;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import il.co.yshahak.ivricalendar.calendar.jewish.Day;
import il.co.yshahak.ivricalendar.calendar.jewish.JewCalendar;
import il.co.yshahak.ivricalendar.repo.DaysRepo;

/**
 * Created by Yaakov Shahak
 * on 10/07/17.
 */

public class MonthViewModel extends ViewModel{


    private final DaysRepo daysRepo;
    private MutableLiveData<List<Day>> dayList;


    public MonthViewModel(DaysRepo daysRepo) {
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
            jewCalendar.shiftMonth(position);
            dayList.postValue(jewCalendar.getMonthDays());
            long start = System.currentTimeMillis();
            daysRepo.setMonthEvents(jewCalendar);
//            MyLog.d( "#2 position=" + position + " | elapsed=" + (System.currentTimeMillis() - start));
            dayList.postValue(jewCalendar.getMonthDays());
        }).start();

    }

}
