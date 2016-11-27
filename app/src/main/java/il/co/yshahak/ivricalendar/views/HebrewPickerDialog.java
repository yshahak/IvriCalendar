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
import il.co.yshahak.ivricalendar.fragments.PickerFragment;

/**
 * Created by B.E.L on 24/11/2016.
 */

public class HebrewPickerDialog extends DialogFragment {
    private final static int CURRENT_PAGE = 500;
    public static Day currentDay;
    public static CreteIvriEventActivity.OnDatePickerDismiss onDatePickerDismiss;


//    @NonNull
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        // Use the Builder class for convenient dialog construction
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        ViewPager viewPager = new ViewPager(getActivity());
//        viewPager.setId(R.id.view_pager_picker);
//        viewPager.setAdapter(new PickerPagerAdapter(getChildFragmentManager()));
//        viewPager.setCurrentItem(CURRENT_PAGE);
//
//        builder
////                .setMessage(R.string.dialog_fire_missiles)
//                .setView(viewPager)
//                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // FIRE ZE MISSILES!
//                    }
//                })
//                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User cancelled the dialog
//                    }
//                });
//
//        return builder.create();
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        HebrewPickerDialog.currentDay = FragmentLoader.currentDay;
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_month_picker, container, false);
        ViewPager viewPager = (ViewPager) root.findViewById(R.id.view_pager);
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
            return PickerFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 1000;
        }


    }


}
