package il.co.yshahak.ivricalendar.views;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import il.co.yshahak.ivricalendar.R;
import il.co.yshahak.ivricalendar.activities.CreteIvriEventActivity;
import il.co.yshahak.ivricalendar.calendar.jewish.Day;
import il.co.yshahak.ivricalendar.fragments.FragmentLoader;

/**
 * Created by B.E.L on 24/11/2016.
 */

public class HebrewPickerDialog extends DialogFragment {
    private final static int CURRENT_PAGE = 500;
    public static Day currentDay;
    public static CreteIvriEventActivity.OnDatePickerDialog onDatePickerDismiss;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        HebrewPickerDialog.currentDay = FragmentLoader.currentDay;
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_month_picker, container, false);
        final ViewPager viewPager = (ViewPager) root.findViewById(R.id.view_pager);
        View btnOk = root.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentLoader.currentDay = currentDay;
                onDatePickerDismiss.onBtnOkPressed();
                dismiss();
            }
        });
        View btnCancel = root.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        viewPager.setAdapter(new PickerPagerAdapter(getChildFragmentManager()));
        viewPager.setCurrentItem(CURRENT_PAGE);
        return root;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        onDatePickerDismiss = null;
    }

    class PickerPagerAdapter extends FragmentPagerAdapter {


        PickerPagerAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
//            try {
//                FragmentLoader fragment = CalendarPagerAdapter.fragmentLoaderSparseArray.get(position).get();
//                return PickerFragment.newInstance(position, fragment.getMonth());
//            } catch (Exception e) {
//                e.printStackTrace();
//                return PickerFragment.newInstance(position);
//            }
            return  null;
        }

        @Override
        public int getCount() {
            return 1000;
        }


    }


}
