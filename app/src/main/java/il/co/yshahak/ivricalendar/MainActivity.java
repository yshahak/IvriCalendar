package il.co.yshahak.ivricalendar;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import il.co.yshahak.ivricalendar.calendar.google.Contract;
import il.co.yshahak.ivricalendar.calendar.jewish.Month;
import il.co.yshahak.ivricalendar.calendar.jewish.Year;

public class MainActivity extends AppCompatActivity{

    private ViewPager viewPager;
    private int pagerPosition;
    public static Year year = new Year();
    public static Month currentMonth = year.getCurrentMonth();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager)findViewById(R.id.view_pager);
        viewPager.setAdapter(new CalendarPagerAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(500);
//        Contract.getCalendars(this);
        Contract.getInstances(this, 3L);

    }

       public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
           if (requestCode == Contract.REQUEST_READ_CALENDAR && grantResults[0] == PackageManager.PERMISSION_GRANTED){
               Contract.getCalendars(this);
           }
       }

}
