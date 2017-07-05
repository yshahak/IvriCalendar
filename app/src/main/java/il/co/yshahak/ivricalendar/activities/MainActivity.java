package il.co.yshahak.ivricalendar.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;


import il.co.yshahak.ivricalendar.R;
import il.co.yshahak.ivricalendar.adapters.CalendarPagerAdapter;
import il.co.yshahak.ivricalendar.calendar.google.Contract;
import il.co.yshahak.ivricalendar.calendar.google.GoogleManager;
import il.co.yshahak.ivricalendar.calendar.jewish.JewCalendar;
import il.co.yshahak.ivricalendar.uihelpers.DrawerHelper;

import static il.co.yshahak.ivricalendar.adapters.CalendarPagerAdapter.FRONT_PAGE;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.KEY_HEBREW_CALENDAR_CLIENT_API_ID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {

//    public static boolean needToRefreshCalendarVisibility;
//    public static JewCalendar currentJewCalendar = new JewCalendar();

    private ViewPager viewPager;
    private DrawerLayout drawerLayout;
    private LinearLayout calendarsList;
    private LinearLayout createEventFrameLayout;
    private boolean floatBtnPressedState;
    private FloatingActionButton fab;
    private Button eventBtnCreate;
    public static boolean recreateFlag;
    public static boolean backToMonthDisplay;
    private RadioGroup formatGroupChoiser, displayChooser;
    private SharedPreferences prefs;
    private Toolbar myToolbar;
    private ActionBarDrawerToggle mDrawertToggle;
    private TextView currentDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Stetho.initializeWithDefaults(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.activity_main);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.addOnPageChangeListener(this);
        viewPager.setOffscreenPageLimit(1);
        createEventFrameLayout = (LinearLayout) findViewById(R.id.add_event_layout);
        calendarsList = (LinearLayout) findViewById(R.id.calender_list);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        eventBtnCreate = (Button) findViewById(R.id.event_create_btn);
        eventBtnCreate.setOnClickListener(this);
        formatGroupChoiser = (RadioGroup) findViewById(R.id.radio_group_format);
        displayChooser = (RadioGroup) findViewById(R.id.radio_group_display);
        displayChooser.setOnCheckedChangeListener(this);
        currentDay = (TextView) findViewById(R.id.current_day);
        currentDay.setOnClickListener(this);
//        setTitle(currentJewCalendar.getMonthName() + " , " + currentJewCalendar.getYearName());

        setDrawerMenu();

    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (permissions.length == 0 || grantResults.length == 0) {
            return;
        }
        if (requestCode == Contract.REQUEST_READ_CALENDAR && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            GoogleManager.getCalendars(this);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawertToggle.syncState();
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
        if (prefs.getString(KEY_HEBREW_CALENDAR_CLIENT_API_ID, null) == null) {
            startActivity(new Intent(this, GoogleSignInActivity.class));
        }
        setPagerAdapter();
        DrawerHelper.setDrawerMenu(calendarsList, this);
//        currentDay.setText(currentJewCalendar.getDayLabel());
//        Log.d("TAG", "onResume");
    }

    private void setPagerAdapter() {
        PagerAdapter pagerAdapter = viewPager.getAdapter();
        if (pagerAdapter != null) {
            CalendarPagerAdapter.dropPages = recreateFlag;
            pagerAdapter.notifyDataSetChanged();
            CalendarPagerAdapter.dropPages = false;
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            viewPager.setAdapter(new CalendarPagerAdapter(getSupportFragmentManager()));
            viewPager.setCurrentItem(FRONT_PAGE);
        }
        recreateFlag = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                fadeInEventCreationLayout();
                break;
            case R.id.event_create_btn:
                switch (formatGroupChoiser.getCheckedRadioButtonId()) {
                    case R.id.event_loazi:
                        Intent intent = new Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI);
                        startActivity(intent);
                        break;
                    case R.id.event_ivri:
                        if (CalendarPagerAdapter.selectedPage == FRONT_PAGE){
//                            Date date = currentJewCalendar.getTime();
//                            CreteIvriEventActivity.currentCalendar = new JewCalendar(date);

                        } else {
//                            WeakReference<BaseCalendarFragment> weakReference = CalendarPagerAdapter.fragmentLoaderSparseArray.get(CalendarPagerAdapter.selectedPage);
//                            if (weakReference != null) {
//                                BaseCalendarFragment fragmentLoader = weakReference.get();
//                                if (fragmentLoader != null) {
//                                    Date date = fragmentLoader.getJewishCalendar().getTime();
//                                    CreteIvriEventActivity.currentCalendar = new JewCalendar(date);
//                                }
//                            }
                        }
                        startActivity(new Intent(this, CreteIvriEventActivity.class));
                        break;
                }
                fadeOutEventCreationLayout();
                break;
            case R.id.cell_root:
                CalendarPagerAdapter.displayState = CalendarPagerAdapter.DISPLAY.DAY;
                displayChooser.check(R.id.display_day);
                int monthDay = (int) v.getTag(R.string.tag_month_position);
                if (monthDay != 0) {
                    viewPager.setCurrentItem(FRONT_PAGE + monthDay, true);
                }
                backToMonthDisplay = true;
                break;
            case R.id.current_day:
                viewPager.setCurrentItem(FRONT_PAGE, true);
                break;
        }

    }

    @Override
    public void onBackPressed() {
        if (floatBtnPressedState) {
            fadeOutEventCreationLayout();
        } else if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (backToMonthDisplay) {
            CalendarPagerAdapter.displayState = CalendarPagerAdapter.DISPLAY.MONTH;
//            WeakReference<BaseCalendarFragment> weakReference = CalendarPagerAdapter.fragmentLoaderSparseArray.get(CalendarPagerAdapter.selectedPage);
//            if (weakReference != null && weakReference.get() != null) {
//                JewCalendar current = weakReference.get().getJewishCalendar();
//                int shift = JewCalendar.getMonthDifference(current, new JewCalendar());
//                viewPager.setCurrentItem(FRONT_PAGE + shift);
//                setTitle(current.getMonthName() + " , " + current.getYearName());
//            }
            displayChooser.check(R.id.display_month);
            backToMonthDisplay = false;

        } else {
            super.onBackPressed();
        }
    }


    private void fadeInEventCreationLayout() {
        floatBtnPressedState = true;
        fab.animate().alpha(0).start();
        viewPager.animate().alpha(0.15f).start();
        fab.setClickable(false);
        createEventFrameLayout.setVisibility(View.VISIBLE);
        createEventFrameLayout.animate().setListener(null).alpha(1).start();
    }

    private void fadeOutEventCreationLayout() {
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawertToggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                myToolbar,
                0,
                0) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (recreateFlag) {
                    setPagerAdapter();
                }
                syncState();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                syncState();
            }
        };
        drawerLayout.setDrawerListener(mDrawertToggle);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
//        Log.d("TAG", "onPageSelected: " + position);
        CalendarPagerAdapter.selectedPage = position;
        String title = JewCalendar.getTitle(position);
        setTitle(title);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        drawerLayout.closeDrawers();
        switch (checkedId) {
            case R.id.display_month:
                CalendarPagerAdapter.displayState = CalendarPagerAdapter.DISPLAY.MONTH;
                break;
            case R.id.display_day:
                CalendarPagerAdapter.displayState = CalendarPagerAdapter.DISPLAY.DAY;
                break;
        }
        recreateFlag = true;
        setPagerAdapter();
    }
}