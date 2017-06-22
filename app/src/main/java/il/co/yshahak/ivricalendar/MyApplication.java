package il.co.yshahak.ivricalendar;

import android.app.Application;

import net.alexandroid.shpref.MyLog;
import net.alexandroid.shpref.ShPref;

import il.co.yshahak.ivricalendar.components.ApplicationComponent;
import il.co.yshahak.ivricalendar.components.DaggerApplicationComponent;
import il.co.yshahak.ivricalendar.modules.ApplicationModule;

/**
 * Created by yshahak
 * on 08/10/2016.
 */

public class MyApplication extends Application {

    private ApplicationComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        ShPref.init(this, ShPref.APPLY);
        MyLog.setTag("ZAQ");
        MyLog.showLogs(BuildConfig.DEBUG);
        component = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
        component.inject(this);
    }

    public ApplicationComponent getComponent() {
        return component;
    }
}
