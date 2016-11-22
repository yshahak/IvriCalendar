package il.co.yshahak.ivricalendar.uihelpers;

import android.content.res.ColorStateList;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import il.co.yshahak.ivricalendar.R;
import il.co.yshahak.ivricalendar.activities.MainActivity;
import il.co.yshahak.ivricalendar.calendar.google.CalendarAccount;
import il.co.yshahak.ivricalendar.calendar.google.GoogleManager;

/**
 * Created by B.E.L on 22/11/2016.
 */

public class DrawerHelper {

    /**
     * setting the drawer category menu of the store
     */
    public static void setDrawerMenu(LinearLayout calendarsList, final MainActivity activity) {

        calendarsList.removeAllViews();
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        for (String calendarAccountNmae : GoogleManager.accountListNames.keySet()){
            TextView header = (TextView) layoutInflater.inflate(R.layout.calendar_accont_header, calendarsList, false);
            header.setText(calendarAccountNmae);
            calendarsList.addView(header);
            for (final CalendarAccount calendarAccount : GoogleManager.accountListNames.get(calendarAccountNmae)){
                final AppCompatCheckBox checkBox = (AppCompatCheckBox) layoutInflater.inflate(R.layout.calendar_visibility_row, calendarsList, false);
                calendarsList.addView(checkBox);
                ColorStateList colorStateList = new ColorStateList(
                        new int[][]{
                                new int[]{-android.R.attr.state_enabled}, //disabled
                                new int[]{android.R.attr.state_enabled} //enabled
                        },
                        new int[] {
                                calendarAccount.getCalendarColor() //disabled
                                ,calendarAccount.getCalendarColor() //enabled

                        }
                );
                checkBox.setSupportButtonTintList(colorStateList);

                checkBox.setChecked(calendarAccount.isCalendarIsVisible());
                checkBox.setText(calendarAccount.getCalendarDisplayName());
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        GoogleManager.updateCalendarVisibility(activity.getContentResolver(), calendarAccount, isChecked);
                        MainActivity.needToRefreshCalendarVisibility = true;
                    }
                });

            }
        }

    }
}
