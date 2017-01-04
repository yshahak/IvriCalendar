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
import android.widget.TextView;

import il.co.yshahak.ivricalendar.R;
import il.co.yshahak.ivricalendar.activities.CreteIvriEventActivity;
import il.co.yshahak.ivricalendar.adapters.CalendarPagerAdapter;
import il.co.yshahak.ivricalendar.calendar.jewish.JewCalendar;
import il.co.yshahak.ivricalendar.fragments.BaseCalendarFragment;
import il.co.yshahak.ivricalendar.fragments.PickerFragment;

import static il.co.yshahak.ivricalendar.adapters.CalendarPagerAdapter.FRONT_PAGE;

/**
 * Created by B.E.L on 24/11/2016.
 */

public class HebrewPickerDialog extends DialogFragment {
    public static CreteIvriEventActivity.OnDatePickerDialog onDatePickerDismiss;
    private TextView monthLabel;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_month_picker, container, false);
        monthLabel = (TextView)root.findViewById(R.id.month_label);
        final ViewPager viewPager = (ViewPager) root.findViewById(R.id.view_pager);
        View btnOk = root.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        PickerPagerAdapter pickerPagerAdapter = new PickerPagerAdapter(getChildFragmentManager());
        viewPager.addOnPageChangeListener(pickerPagerAdapter);
        viewPager.setAdapter(pickerPagerAdapter);
        viewPager.setCurrentItem(FRONT_PAGE);
        BaseCalendarFragment fragment = CalendarPagerAdapter.fragmentLoaderSparseArray.get(FRONT_PAGE).get();
        monthLabel.setText(fragment.getJewishCalendar().getMonthName() + " , " + fragment.getJewishCalendar().getYearName());
        return root;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        onDatePickerDismiss = null;
    }

    class PickerPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {
        FragmentManager supportFragmentManager;

        PickerPagerAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
            this.supportFragmentManager = supportFragmentManager;
        }

        @Override
        public Fragment getItem(int position) {
            BaseCalendarFragment item = PickerFragment.newInstance(position);
            if (CalendarPagerAdapter.fragmentLoaderSparseArray.get(position) != null && CalendarPagerAdapter.fragmentLoaderSparseArray.get(position).get() != null) {
                BaseCalendarFragment fragment = CalendarPagerAdapter.fragmentLoaderSparseArray.get(position).get();
                item.setJewishCalendar(fragment.getJewishCalendar());
            } else {
                item.setJewishCalendar(new JewCalendar());
                item.getJewishCalendar().shiftMonth(position - FRONT_PAGE);
            }
            return item;
        }

        @Override
        public int getCount() {
            return 1000;
        }


        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            // Do we already have this fragment?
            String name = "android:switcher:" + R.id.view_pager + ":" + position;// makeFragmentName(container.getId(), itemId);
            BaseCalendarFragment fragment = (BaseCalendarFragment) supportFragmentManager.findFragmentByTag(name);
            if (fragment != null) {
                monthLabel.setText(fragment.getJewishCalendar().getMonthName() + " , " + fragment.getJewishCalendar().getYearName());
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }


}
