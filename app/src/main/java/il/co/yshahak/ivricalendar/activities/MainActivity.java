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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import java.lang.ref.WeakReference;

import il.co.yshahak.ivricalendar.R;
import il.co.yshahak.ivricalendar.adapters.CalendarPagerAdapter;
import il.co.yshahak.ivricalendar.calendar.google.Contract;
import il.co.yshahak.ivricalendar.calendar.google.GoogleManager;
import il.co.yshahak.ivricalendar.fragments.FragmentMonth;
import il.co.yshahak.ivricalendar.uihelpers.DrawerHelper;

import static il.co.yshahak.ivricalendar.calendar.google.Contract.KEY_HEBREW_CALENDAR_CLIENT_API_ID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {

    public static boolean needToRefreshCalendarVisibility;
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
    private boolean mSlideState; //indicate the current state of the drawer
    private int selectedPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Stetho.initializeWithDefaults(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.addOnPageChangeListener(this);
        createEventFrameLayout = (LinearLayout) findViewById(R.id.add_event_layout);
        calendarsList = (LinearLayout) findViewById(R.id.calender_list);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        eventBtnCreate = (Button) findViewById(R.id.event_create_btn);
        eventBtnCreate.setOnClickListener(this);
        formatGroupChoiser = (RadioGroup) findViewById(R.id.radio_group_format);
        displayChooser = (RadioGroup) findViewById(R.id.radio_group_display);
        displayChooser.setOnCheckedChangeListener(this);
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
        setDrawerMenu();
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
            viewPager.setCurrentItem(500);
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
                        Intent intent = new Intent(Intent.ACTION_INSERT)
                                .setData(CalendarContract.Events.CONTENT_URI);
                        startActivity(intent);
                        break;
                    case R.id.event_ivri:
                        Intent intentIvri = new Intent(this, CreteIvriEventActivity.class);
//                    WeakReference<FragmentMonth> weakReference = CalendarPagerAdapter.fragmentLoaderSparseArray.get(selectedPage);
//                    if (weakReference != null) {
//                        FragmentMonth fragmentLoader = weakReference.get();
//                        if (fragmentLoader != null) {
//                            if (fragmentLoader.getMonth().getDays().contains(FragmentMonth.currentDay)){
//                                intentIvri.putExtra(CreteIvriEventActivity.EXTRA_USE_CURRENT_DAY, true);
//                            }
//                        }
//                    }
                        startActivity(intentIvri);
                        break;
                }
                fadeOutEventCreationLayout();
                break;
            case R.id.cell_root:
                CalendarPagerAdapter.displayState = CalendarPagerAdapter.DISPLAY.DAY;
                displayChooser.check(R.id.display_day);
                backToMonthDisplay = true;
                break;
        }

    }

    @Override
    public void onBackPressed() {
        if (floatBtnPressedState) {
            fadeOutEventCreationLayout();
        } else if (mSlideState) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (backToMonthDisplay) {
            CalendarPagerAdapter.displayState = CalendarPagerAdapter.DISPLAY.MONTH;
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
        drawerLayout.setDrawerListener(new ActionBarDrawerToggle(this,
                drawerLayout,
                null,
                0,
                0) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mSlideState = false;//is Closed
                if (needToRefreshCalendarVisibility) {
                    setPagerAdapter();
                    needToRefreshCalendarVisibility = false;
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mSlideState = true;//is Opened
            }
        });
        DrawerHelper.setDrawerMenu(calendarsList, this);

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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        Log.d("TAG", "onPageSelected: " + position);
        if (this.selectedPage != 0) {
            if (position > this.selectedPage) {
                CalendarPagerAdapter.direction = CalendarPagerAdapter.DIRECTION.RIGHT;
            } else if (position < this.selectedPage) {
                CalendarPagerAdapter.direction = CalendarPagerAdapter.DIRECTION.LEFT;
            }
        }
        this.selectedPage = position;
        CalendarPagerAdapter.selectedPage = position;
        WeakReference<FragmentMonth> weakReference = CalendarPagerAdapter.fragmentLoaderSparseArray.get(position);
        if (weakReference != null) {
            FragmentMonth fragmentLoader = weakReference.get();
            if (fragmentLoader != null) {
                setTitle(fragmentLoader.getJewishCalendar().getMonthName() + " , " + fragmentLoader.getJewishCalendar().getYearName());
                RecyclerView.Adapter adapter = fragmentLoader.getRecyclerView().getAdapter();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public int getSelectedPage() {
        return selectedPage;
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