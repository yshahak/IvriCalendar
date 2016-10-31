package il.co.yshahak.ivricalendar.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import il.co.yshahak.ivricalendar.fragments.FragmentLoader;

/**
 * Created by yshahak on 06/10/2016.
 */
public class CalendarPagerAdapter extends FragmentStatePagerAdapter {

    public static DIRECTION direction = DIRECTION.NULL;
    public static boolean dropPages;

    public CalendarPagerAdapter(FragmentManager supportFragmentManager) {
        super(supportFragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
//        Log.d("TAG", "getItem, position: "  + position);
        return FragmentLoader.newInstance(position);
    }

    @Override
    public int getItemPosition(Object object) {
//        Log.d("TAG", "getItemPosition: "  + object.toString());
        return dropPages ? POSITION_NONE : super.getItemPosition(object) ;

//        if (direction != DIRECTION.NULL){
//            int currentPosition = ((FragmentMonth)object).getPosition();
//            return (direction == DIRECTION.LEFT) ? --currentPosition : ++currentPosition;
//        }
//        return super.getItemPosition(object);
    }



//    @Override
//    public long getItemId(int position) {
//        if (direction != DIRECTION.NULL){
//            return (direction == DIRECTION.LEFT) ? --position : ++position;
//        }
//        return super.getItemId(position);
//    }

    @Override
    public int getCount() {
        return 1000;
    }

    public static enum CALENDAR_MODE{
        MONTH,
        WEEK,
        DAY
    }

    public static enum DIRECTION{
        LEFT,
        RIGHT,
        NULL
    }
}
