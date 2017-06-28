package il.co.yshahak.ivricalendar.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import il.co.yshahak.ivricalendar.fragments.FragmentHebrewMonth;
import il.co.yshahak.ivricalendar.fragments.HebrewMonthView;

/**
 * Created by yshahak
 * on 06/10/2016.
 */
public class CalendarMonthPagerAdapter extends PagerAdapter {
    public static DISPLAY displayState = DISPLAY.MONTH;
    public static boolean dropPages;
    public final static int FRONT_PAGE = 500;
    public static int selectedPage = FRONT_PAGE;



    @Override
    public Object instantiateItem(ViewGroup group, int position) {
        HebrewMonthView hebrewMonthView = new HebrewMonthView(group.getContext(), position);
        group.addView(hebrewMonthView);
//        fragmentLoaderSparseArray.put(position, new WeakReference<>(fragment));
        return hebrewMonthView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        HebrewMonthView hebrewMonthView = (HebrewMonthView) object;
        container.removeView(hebrewMonthView);
    }


//    @Override
//    public int getItemPosition(Object object) {
////        Log.d("TAG", "getItemPosition: "  + object.toString());
//        return dropPages ? POSITION_NONE : super.getItemPosition(object);
//    }

    @Override
    public int getCount() {
        return 1000;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    private POSITION getPosition(int position) {
        if (position > selectedPage) {
            return POSITION.RIGHT;
        } else if (position < selectedPage) {
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


}