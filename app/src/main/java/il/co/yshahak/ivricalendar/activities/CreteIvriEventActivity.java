package il.co.yshahak.ivricalendar.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import il.co.yshahak.ivricalendar.R;
import il.co.yshahak.ivricalendar.fragments.TimePickerFragment;

/**
 * Created by B.E.L on 31/10/2016.
 */

public class CreteIvriEventActivity extends AppCompatActivity {

    @BindView(R.id.header_btn_save) TextView headerBtnSave;
    @BindView(R.id.header_btn_x) ImageView headerBtnX;
    @BindView(R.id.header_edit_text_event_title) EditText headerTitleEditText;
    @BindView(R.id.checkbox_all_day_event) SwitchCompat switchCompatAllDay;
    @BindView(R.id.event_start_day )TextView eventStartDay;
    @BindView(R.id.event_end_day) TextView eventEndDay;
    @BindView(R.id.event_start_time) TextView eventStartTime;
    @BindView(R.id.event_end_time) TextView eventEndTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ivri_event);
        ButterKnife.bind(this);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        eventStartTime.setText(sdf.format(calendar.getTime()));
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);
        eventEndTime.setText(sdf.format(calendar.getTime()));
    }

    @OnClick(R.id.event_start_day)void startDayDialog(){

    }
    @OnClick(R.id.event_end_day)void endDayDialog(){

    }
    @OnClick(R.id.event_start_time) void startTimeDialog(){
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }
    @OnClick(R.id.event_end_time) void endTimeDialog(){

    }


}
