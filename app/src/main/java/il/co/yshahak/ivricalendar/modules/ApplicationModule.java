package il.co.yshahak.ivricalendar.modules;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import il.co.yshahak.ivricalendar.calendar.EventsProvider;
import il.co.yshahak.ivricalendar.calendar.EventsProviderImpl;
import il.co.yshahak.ivricalendar.calendar.jewish.JewCalendar;
import il.co.yshahak.ivricalendar.repo.DaysRepo;
import il.co.yshahak.ivricalendar.repo.DaysRepoImpl;
import il.co.yshahak.ivricalendar.uihelpers.DividerItemDecoration;

/**
 * Created by Yaakov Shahak
 * on 21/06/17.
 */

@SuppressWarnings("WeakerAccess")
@Module
public class ApplicationModule {

    private Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides
    public JewCalendar provideJewCalendar(){
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

    @Provides @Singleton
    public DaysRepo provideDaysRepo(){
        return new DaysRepoImpl(provideEventInterface(), provideContentResolver());
    }

    @Provides @Singleton
    public ContentResolver provideContentResolver(){
        return application.getContentResolver();
    }

}
