package il.co.yshahak.ivricalendar.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;

import java.lang.ref.WeakReference;

import il.co.yshahak.ivricalendar.fragments.FragmentMonthLoader;

/**
 * Created by yshahak on 06/10/2016.
 */
public class CalendarPagerAdapter extends FragmentStatePagerAdapter {

    public static boolean dropPages;
    public static SparseArray<WeakReference<FragmentMonthLoader>> fragmentLoaderSparseArray = new SparseArray<>();

    public CalendarPagerAdapter(FragmentManager supportFragmentManager) {
        super(supportFragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
//        Log.d("TAG", "getItem, position: "  + position);
        FragmentMonthLoader fragmentLoader = FragmentMonthLoader.newInstance(position);
        fragmentLoaderSparseArray.put(position, new WeakReference<>(fragmentLoader));
        return fragmentLoader;
    }

    @Override
    public int getItemPosition(Object object) {
//        Log.d("TAG", "getItemPosition: "  + object.toString());
        return dropPages ? POSITION_NONE : super.getItemPosition(object) ;
    }



    @Override
    public int getCount() {
        return 1000;
    }


}