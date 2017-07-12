package il.co.yshahak.ivricalendar;

import android.app.Application;

import com.facebook.stetho.Stetho;

import net.alexandroid.shpref.MyLog;
import net.alexandroid.shpref.ShPref;

import javax.inject.Inject;

import il.co.yshahak.ivricalendar.calendar.jewish.JewCalendar;
import il.co.yshahak.ivricalendar.calendar.jewish.Month;
import il.co.yshahak.ivricalendar.components.ApplicationComponent;
import il.co.yshahak.ivricalendar.components.DaggerApplicationComponent;
import il.co.yshahak.ivricalendar.modules.ApplicationModule;
import il.co.yshahak.ivricalendar.room.database.CalendarDataBase;

/**
 * Created by yshahak
 * on 08/10/2016.
 */

public class MyApplication extends Application {

    private ApplicationComponent component;
    @Inject
    CalendarDataBase calendarDataBase;

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        ShPref.init(this, ShPref.APPLY);
        MyLog.setTag("ZAQ");
        MyLog.showLogs(BuildConfig.DEBUG);
        component = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
        component.inject(this);
        checkDataBase();
        JewCalendar.initPool();
    }

    public ApplicationComponent getComponent() {
        return component;
    }

    private void checkDataBase() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                calendarDataBase.monthDao().deleteAll();
                int numOfMonthInDb = calendarDataBase.monthDao().getCount().length;
                if (numOfMonthInDb > 5){
                    return;
                }
                JewCalendar calendar = new JewCalendar();
                calendar.setJewishDayOfMonth(1);
                for (int i = 0; i <= 20; i++) {
                    Month month = calendarDataBase.monthDao().getByHashCode(calendar.monthHashCode());
                    if (month == null) {
                        calendar.setOffsets();
                        month = new Month(calendar);
                        calendarDataBase.monthDao().insertMonth(month);
                        calendar.setMonthDays();
                        calendarDataBase.dayDao().insertAll(calendar.getMonthDays());
                    }
                    calendar.shiftMonthForward();
                }
                calendar = new JewCalendar();
                for (int i = 0; i >= -20; i--) {
                    calendar.shiftMonthBackword();
                    Month month = calendarDataBase.monthDao().getByHashCode(calendar.monthHashCode());
                    if (month == null) {
                        calendar.setOffsets();
                        month = new Month(calendar);
                        calendarDataBase.monthDao().insertMonth(month);
                        calendar.setMonthDays();
                        calendarDataBase.dayDao().insertAll(calendar.getMonthDays());
                    }
                }
            }
        }).start();

    }
}
