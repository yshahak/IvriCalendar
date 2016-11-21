package il.co.yshahak.ivricalendar.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import net.sourceforge.zmanim.hebrewcalendar.JewishCalendar;

import il.co.yshahak.ivricalendar.R;
import il.co.yshahak.ivricalendar.adapters.CalendarPagerAdapter;
import il.co.yshahak.ivricalendar.calendar.google.CalendarAccount;
import il.co.yshahak.ivricalendar.calendar.google.Contract;
import il.co.yshahak.ivricalendar.calendar.google.GoogleManager;
import il.co.yshahak.ivricalendar.calendar.jewish.Day;

import static il.co.yshahak.ivricalendar.calendar.google.Contract.KEY_HEBREW_CALENDAR_CLIENT_API_ID;
import static il.co.yshahak.ivricalendar.calendar.jewish.Month.hebrewDateFormatter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager viewPager;
    private DrawerLayout drawerLayout;
    private LinearLayout calendarsList;
    private LinearLayout createEventFrameLayout;
    private boolean floatBtnPressedState;
    private FloatingActionButton fab;
    private Button eventBtnCreate;
    public static boolean recreateFlag;
    private RadioGroup formatGroupChoiser;
    private SharedPreferences prefs;
    public static Day currentJewishDays;
    private boolean mSlideState; //indicate the current state of the drawer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        JewishCalendar jewishCalendar =  new JewishCalendar();
        String monthName = hebrewDateFormatter.formatMonth(jewishCalendar);
        String label = hebrewDateFormatter.formatHebrewNumber(jewishCalendar.getJewishDayOfMonth());
        currentJewishDays = new Day(monthName, label, null, jewishCalendar.getJewishDayOfMonth());
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        createEventFrameLayout = (LinearLayout) findViewById(R.id.add_event_layout);
        calendarsList = (LinearLayout)findViewById(R.id.calender_list);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        eventBtnCreate = (Button) findViewById(R.id.event_create_btn);
        eventBtnCreate.setOnClickListener(this);
        formatGroupChoiser = (RadioGroup) findViewById(R.id.radio_group_format);
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (permissions.length == 0 || grantResults.length == 0){
            return;
        }
        if (requestCode == Contract.REQUEST_READ_CALENDAR && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            GoogleManager.getCalendars(this);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("TAG", "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        GoogleManager.getCalendars(this);
        if (prefs.getString(KEY_HEBREW_CALENDAR_CLIENT_API_ID, null) == null){
            startActivity(new Intent(this, GoogleSignInActivity.class));
        }
        PagerAdapter pagerAdapter = viewPager.getAdapter();
        if (pagerAdapter != null) {
            CalendarPagerAdapter.dropPages = true;
            pagerAdapter.notifyDataSetChanged();
            CalendarPagerAdapter.dropPages = false;
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            viewPager.setAdapter(new CalendarPagerAdapter(getSupportFragmentManager()));
            viewPager.setCurrentItem(500);
        }
        setDrawerMenu();

//        Log.d("TAG", "onResume");
    }

    @Override
    public void onClick(View v) {
        if (v.equals(fab)) {
            fadeInEventCreationLayout();
        } else if (v.equals(eventBtnCreate)) {
            switch (formatGroupChoiser.getCheckedRadioButtonId()){
                case R.id.event_loazi:
                    Intent intent = new Intent(Intent.ACTION_INSERT)
                            .setData(CalendarContract.Events.CONTENT_URI);
                    startActivity(intent);
                    break;
                case R.id.event_ivri:
                    Intent intentIvri = new Intent(this, CreteIvriEventActivity.class);
                    startActivity(intentIvri);
                    break;
            }
            fadeOutEventCreationLayout();
        }
    }

    @Override
    public void onBackPressed() {
        if (floatBtnPressedState) {
           fadeOutEventCreationLayout();
        }
        else if (mSlideState) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void fadeInEventCreationLayout(){
        floatBtnPressedState = true;
        fab.animate().alpha(0).start();
        viewPager.animate().alpha(0.15f).start();
        fab.setClickable(false);
        createEventFrameLayout.setVisibility(View.VISIBLE);
        createEventFrameLayout.animate().setListener(null).alpha(1).start();
    }

    private void fadeOutEventCreationLayout(){
        floatBtnPressedState = false;
        fab.animate().alpha(1).start();
        viewPager.animate().alpha(1).start();
        fab.setClickable(true);
        createEventFrameLayout.animate().alpha(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                onAnimationEnd(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                createEventFrameLayout.setVisibility(View.GONE);
                animation.removeAllListeners();
            }
        }).start();
    }

    /**
     * setting the drawer category menu of the store
     */
    private void setDrawerMenu() {
        drawerLayout.setDrawerListener(new ActionBarDrawerToggle(this,
                drawerLayout,
                null,
                0,
                0) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mSlideState = false;//is Closed
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mSlideState = true;//is Opened
            }
        });
        calendarsList.removeAllViews();
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        for (String calendarAccountNmae : GoogleManager.accountListNames.keySet()){
            TextView header = (TextView) layoutInflater.inflate(R.layout.calendar_accont_header, calendarsList, false);
            header.setText(calendarAccountNmae);
            calendarsList.addView(header);
            for (CalendarAccount calendarAccount : GoogleManager.accountListNames.get(calendarAccountNmae)){
                AppCompatCheckBox checkBox = (AppCompatCheckBox) layoutInflater.inflate(R.layout.calendar_visibility_row, calendarsList, false);
                calendarsList.addView(checkBox);
                checkBox.setText(calendarAccount.getCalendarDisplayName());
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

            }
        }

    }

    /**
     * open or close the drawer menu
     *
     * @param view the hamburger icon clicked
     */
    public void clickEventSlide(View view) {
        if (mSlideState) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }
}
