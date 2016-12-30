package il.co.yshahak.ivricalendar.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.util.SparseArray;

import java.lang.ref.WeakReference;

import il.co.yshahak.ivricalendar.calendar.jewish.JewCalendar;
import il.co.yshahak.ivricalendar.fragments.FragmentDay;
import il.co.yshahak.ivricalendar.fragments.FragmentMonth;

/**
 * Created by yshahak on 06/10/2016.
 */
public class CalendarPagerAdapter extends FragmentPagerAdapter {
    public static DISPLAY displayState = DISPLAY.MONTH;
    public static boolean dropPages;
    public final static int FRONT_PAGE = 500;
    public static int selectedPage = FRONT_PAGE;

    public static SparseArray<WeakReference<FragmentMonth>> fragmentLoaderSparseArray = new SparseArray<>();

    public static DIRECTION direction = DIRECTION.NULL;

    public CalendarPagerAdapter(FragmentManager supportFragmentManager) {
        super(supportFragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        Log.d("TAG", "getItem, position: "  + position);
        JewCalendar jewCalendar = new JewCalendar();
        if (displayState == DISPLAY.MONTH) {
            FragmentMonth fragmentLoader = FragmentMonth.newInstance(position);
            jewCalendar.shiftMonth(position - FRONT_PAGE);
            fragmentLoader.setJewishCalendar(jewCalendar);
            fragmentLoaderSparseArray.put(position, new WeakReference<>(fragmentLoader));
            return fragmentLoader;
        } else {
            FragmentDay fragmentDay = FragmentDay.newInstance(position);

        }
        return null;
    }

    @Override
    public int getItemPosition(Object object) {
//        Log.d("TAG", "getItemPosition: "  + object.toString());
        return dropPages ? POSITION_NONE : super.getItemPosition(object);
    }

    @Override
    public int getCount() {
        return 1000;
    }

    private POSITION getPosition(int position){
        if (position > selectedPage){
            return POSITION.RIGHT;
        } else if (position < selectedPage){
            return POSITION.LEFT;
        }
        return POSITION.MIDDLE;
    }

    private enum POSITION {
        LEFT,
        MIDDLE,
        RIGHT
    }

    public enum DISPLAY {
        MONTH,
        WEEK,
        DAY
    }

    public enum DIRECTION {
        LEFT,
        RIGHT,
        NULL
    }

}