package il.co.yshahak.ivricalendar.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import il.co.yshahak.ivricalendar.R;
import il.co.yshahak.ivricalendar.adapters.CalendarPagerAdapter;
import il.co.yshahak.ivricalendar.calendar.google.Contract;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager viewPager;
    private int pagerPosition;
    private LinearLayout createEventFrameLayout;
    private boolean floatBtnPressedState;
    private FloatingActionButton fab;
    private Button eventBtnCreate;
    public static boolean recreateFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.view_pager);

        createEventFrameLayout = (LinearLayout) findViewById(R.id.add_event_layout);
//        Contract.getCalendars(this);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        eventBtnCreate = (Button) findViewById(R.id.event_create_btn);
        eventBtnCreate.setOnClickListener(this);

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Contract.REQUEST_READ_CALENDAR && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Contract.getCalendars(this);
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
        PagerAdapter pagerAdapter = viewPager.getAdapter();
        if (pagerAdapter != null) {
            pagerAdapter.notifyDataSetChanged();
        } else {
            viewPager.setAdapter(new CalendarPagerAdapter(getSupportFragmentManager()));
            viewPager.setCurrentItem(500);
        }
        Log.d("TAG", "onResume");
    }

    @Override
    public void onClick(View v) {
        if (v.equals(fab)) {
            floatBtnPressedState = true;
            v.animate().alpha(0).start();
            viewPager.animate().alpha(0.15f).start();
            fab.setClickable(false);
            createEventFrameLayout.setVisibility(View.VISIBLE);
            createEventFrameLayout.animate().setListener(null).alpha(1).start();
        } else if (v.equals(eventBtnCreate)) {
            Intent intent = new Intent(Intent.ACTION_EDIT)
                    .setData(CalendarContract.Events.CONTENT_URI);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (floatBtnPressedState) {
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
        } else {
            super.onBackPressed();
        }
    }
}
