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
import il.co.yshahak.ivricalendar.fragments.FragmentLoader;
import il.co.yshahak.ivricalendar.uihelpers.DrawerHelper;

import static il.co.yshahak.ivricalendar.calendar.google.Contract.KEY_HEBREW_CALENDAR_CLIENT_API_ID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    public static boolean needToRefreshCalendarVisibility;
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
    private boolean mSlideState; //indicate the current state of the drawer
    private int selectedPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.addOnPageChangeListener(this);
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
        setPagerAdapter();
        setDrawerMenu();
//        Log.d("TAG", "onResume");
    }

    private void setPagerAdapter(){
        PagerAdapter pagerAdapter = viewPager.getAdapter();
        if (pagerAdapter != null) {
            CalendarPagerAdapter.dropPages = true;
            pagerAdapter.notifyDataSetChanged();
            CalendarPagerAdapter.dropPages = false;
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            viewPager.setAdapter(new CalendarPagerAdapter(getSupportFragmentManager()));
            viewPager.setCurrentItem(500);
        }
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
                    WeakReference<FragmentLoader> weakReference = CalendarPagerAdapter.fragmentLoaderSparseArray.get(selectedPage);
                    if (weakReference != null) {
                        FragmentLoader fragmentLoader = weakReference.get();
                        if (fragmentLoader != null) {
                            if (fragmentLoader.getMonth().getDays().contains(FragmentLoader.currentDay)){

                            }
                        }
                    }
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
                if (needToRefreshCalendarVisibility){
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
        this.selectedPage = position;
        WeakReference<FragmentLoader> weakReference = CalendarPagerAdapter.fragmentLoaderSparseArray.get(position);
        if (weakReference != null) {
            FragmentLoader fragmentLoader = weakReference.get();
            if (fragmentLoader != null) {
                setTitle(fragmentLoader.getMonth().getMonthName() + " , " + fragmentLoader.getMonth().getYearName());
                fragmentLoader.getRecyclerView().getAdapter().notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public int getSelectedPage() {
        return selectedPage;
    }
}
