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

public class MonthViewModel extends ViewModel {

    private DaysRepo daysRepo;
    private MutableLiveData<List<Day>> dayList;

//    public MonthViewModel(DaysRepo daysRepo) {
//        this.daysRepo = daysRepo;
//    }


    public void setDaysRepo(DaysRepo daysRepo) {
        this.daysRepo = daysRepo;
    }

    public LiveData<List<Day>> getDayList(JewCalendar jewCalendar) {
        if (dayList == null) {
            dayList = new MutableLiveData<>();
            loadDays(jewCalendar);
        }
        return dayList;
    }

    private void loadDays(JewCalendar jewCalendar) {
        List<Day> days =daysRepo.getMonthDays(jewCalendar);
        dayList.postValue(days);
    }
}
