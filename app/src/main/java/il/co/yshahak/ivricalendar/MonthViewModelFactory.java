package il.co.yshahak.ivricalendar;

import android.arch.lifecycle.ViewModelProvider;

import javax.inject.Inject;

import il.co.yshahak.ivricalendar.repo.DaysRepo;
import il.co.yshahak.ivricalendar.views.MonthViewModel;

/**
 * Created by Yaakov Shahak
 * on 11/07/17.
 */

public class MonthViewModelFactory implements ViewModelProvider.Factory {

    private DaysRepo daysRepo;

    @Inject
    public MonthViewModelFactory(DaysRepo daysRepo) {
        this.daysRepo = daysRepo;
    }

    @Override
    public MonthViewModel create(Class modelClass) {
        return new MonthViewModel(daysRepo);
    }
}