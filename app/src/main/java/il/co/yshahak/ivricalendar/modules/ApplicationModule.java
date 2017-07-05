package il.co.yshahak.ivricalendar.modules;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import il.co.yshahak.ivricalendar.DividerItemDecoration;
import il.co.yshahak.ivricalendar.calendar.EventsProvider;
import il.co.yshahak.ivricalendar.calendar.EventsProviderImpl;
import il.co.yshahak.ivricalendar.calendar.jewish.JewCalendar;

/**
 * Created by Yaakov Shahak
 * on 21/06/17.
 */

@Module
public class ApplicationModule {

    private Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides
    public JewCalendar provideJewCalendar(){
        Log.d("TAG", "jew calendar created");
        return JewCalendar.obtain();
    }

    @Provides @Singleton
    public DividerItemDecoration getItemDecoration(Context context){
        return new DividerItemDecoration(context);
    }

    @Provides @Singleton
    public Context provideContext() {
        return application;
    }

    @Provides @Singleton
    public EventsProvider provideEventInterface(){
        return new EventsProviderImpl();
    }

}
