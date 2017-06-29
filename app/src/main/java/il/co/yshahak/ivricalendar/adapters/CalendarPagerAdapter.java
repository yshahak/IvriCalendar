package il.co.yshahak.ivricalendar.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.ViewGroup;

import net.alexandroid.shpref.MyLog;

import java.lang.ref.WeakReference;

import il.co.yshahak.ivricalendar.fragments.BaseCalendarFragment;
import il.co.yshahak.ivricalendar.fragments.FragmentDay;
import il.co.yshahak.ivricalendar.fragments.FragmentHebrewMonth;
import il.co.yshahak.ivricalendar.fragments.FragmentMonth;

/**
 * Created by yshahak
 * on 06/10/2016.
 */
public class CalendarPagerAdapter extends FragmentPagerAdapter {
    public static DISPLAY displayState = DISPLAY.MONTH;
    public static boolean dropPages;
    public final static int FRONT_PAGE = 500;
    public static int selectedPage = FRONT_PAGE;
//    private final FragmentManager mFragmentManager;

//    public static SparseArray<WeakReference<BaseCalendarFragment>> fragmentLoaderSparseArray = new SparseArray<>();


    public CalendarPagerAdapter(FragmentManager supportFragmentManager) {
        super(supportFragmentManager);
//        mFragmentManager = supportFragmentManager;
    }

    @Override
    public void notifyDataSetChanged() {
        if (dropPages) {
//            FragmentTransaction mCurTransaction = mFragmentManager.beginTransaction();
//            if (mCurTransaction != null) {
//                for (Fragment fragment : mFragmentManager.getFragments()) {
//                    if (fragment != null) {
//                        mCurTransaction.remove(fragment);
//                    }
//                }
//                mCurTransaction.commitNow();
//            }
        }
        super.notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
//        Log.d("TAG", "getItem, position: "  + position);
//        BaseCalendarFragment fragment;
//        if (displayState == DISPLAY.MONTH) {
//            fragment = FragmentMonth.newInstance(position);
//        } else {
//            fragment = FragmentDay.newInstance(position);
//        }
        Fragment fragment = FragmentHebrewMonth.newInstance(position);
//        fragmentLoaderSparseArray.put(position, new WeakReference<>(fragment));
        return fragment;
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