package il.co.yshahak.ivricalendar.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import il.co.yshahak.ivricalendar.calendar.jewish.JewCalendar;

/**
 * Created by yshah on 1/3/2017.
 */

public  class BaseCalendarFragment extends Fragment {
    static final String KEY_POSITION = "keyPosition";

//    JewCalendar jewishCalendar;

    int position;

    public static void init(Fragment fragment, int position){
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_POSITION, position);
        fragment.setArguments(bundle);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.position = getArguments().getInt(KEY_POSITION);
    }

//    public void setJewishCalendar(JewCalendar jewishCalendar){
//        this.jewishCalendar = jewishCalendar;
//    }
//
//    public JewCalendar getJewishCalendar() {
//        return jewishCalendar;
//    }

}
