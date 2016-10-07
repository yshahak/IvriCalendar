package il.co.yshahak.ivricalendar;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import il.co.yshahak.ivricalendar.calendar.Month;
import il.co.yshahak.ivricalendar.calendar.Year;

import static android.support.v4.view.ViewPager.SCROLL_STATE_DRAGGING;
import static android.support.v4.view.ViewPager.SCROLL_STATE_IDLE;
import static android.support.v4.view.ViewPager.SCROLL_STATE_SETTLING;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{

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
        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(500);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        this.pagerPosition = position;
        Log.d("TAG", "onPageSelected, position: "  + position);
//        if (position != 1){
//            viewPager.setCurrentItem(1);
//        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state){
            case SCROLL_STATE_IDLE:
//                if (pagerPosition != 1){
//                    CalendarPagerAdapter.direction = (pagerPosition == 0) ? CalendarPagerAdapter.DIRECTION.LEFT : CalendarPagerAdapter.DIRECTION.RIGHT;
//                    viewPager.getAdapter().notifyDataSetChanged();
//                    CalendarPagerAdapter.direction = CalendarPagerAdapter.DIRECTION.NULL;
//                    viewPager.setCurrentItem(1);
//                }
                Log.d("TAG", "onPageScrollStateChanged: "  + "SCROLL_STATE_IDLE");
                break;
            case SCROLL_STATE_DRAGGING:
                break;
            case SCROLL_STATE_SETTLING:
                Log.d("TAG", "onPageScrollStateChanged: "  + "SCROLL_STATE_SETTLING");
                break;

        }

    }
}
