package il.co.yshahak.ivricalendar.components;

import javax.inject.Singleton;

import dagger.Component;
import il.co.yshahak.ivricalendar.MyApplication;
import il.co.yshahak.ivricalendar.activities.CreteIvriEventActivity;
import il.co.yshahak.ivricalendar.fragments.FragmentHebrewMonth;
import il.co.yshahak.ivricalendar.modules.ApplicationModule;
import il.co.yshahak.ivricalendar.repo.DaysRepoImpl;

/**
 * Created by Yaakov Shahak
 * on 21/06/17.
 */

@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    void inject(MyApplication target);

    void inject(FragmentHebrewMonth fragment);

    void inject(CreteIvriEventActivity creteIvriEventActivity);

    void inject(DaysRepoImpl daysRepo);
}
