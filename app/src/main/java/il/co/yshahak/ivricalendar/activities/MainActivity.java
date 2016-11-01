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
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import net.sourceforge.zmanim.hebrewcalendar.JewishCalendar;

import il.co.yshahak.ivricalendar.R;
import il.co.yshahak.ivricalendar.adapters.CalendarPagerAdapter;
import il.co.yshahak.ivricalendar.calendar.google.Contract;
import il.co.yshahak.ivricalendar.calendar.google.GoogleManager;

import static il.co.yshahak.ivricalendar.calendar.google.Contract.KEY_HEBREW_CALENDAR_CLIENT_API_ID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager viewPager;
    private int pagerPosition;
    private LinearLayout createEventFrameLayout;
    private boolean floatBtnPressedState;
    private FloatingActionButton fab;
    private Button eventBtnCreate;
    public static boolean recreateFlag;
    private RadioGroup formatGroupChoiser;
    private SharedPreferences prefs;
    public static JewishCalendar currentJewishCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        currentJewishCalendar = new JewishCalendar();
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        createEventFrameLayout = (LinearLayout) findViewById(R.id.add_event_layout);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        eventBtnCreate = (Button) findViewById(R.id.event_create_btn);
        eventBtnCreate.setOnClickListener(this);
        formatGroupChoiser = (RadioGroup) findViewById(R.id.radio_group_format);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
}
