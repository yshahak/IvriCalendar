package il.co.yshahak.ivricalendar.activities;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import il.co.yshahak.ivricalendar.R;
import il.co.yshahak.ivricalendar.calendar.google.GoogleManager;
import il.co.yshahak.ivricalendar.fragments.TimePickerFragment;

/**
 * Created by B.E.L on 31/10/2016.
 */

public class CreteIvriEventActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, KeyboardVisibilityEventListener {

    @BindView(R.id.header_btn_save) TextView headerBtnSave;
    @BindView(R.id.header_btn_x) ImageView headerBtnX;
    @BindView(R.id.header_edit_text_event_title) EditText headerTitleEditText;
    @BindView(R.id.checkbox_all_day_event) SwitchCompat switchCompatAllDay;
    @BindView(R.id.event_start_day )TextView eventStartDay;
    @BindView(R.id.event_end_day) TextView eventEndDay;
    @BindView(R.id.event_start_time) TextView eventStartTime;
    @BindView(R.id.event_end_time) TextView eventEndTime;

    private PICKER_STATE pickerState;
    private SimpleDateFormat sdf;
    private Calendar calendarStartTime, calendarEndTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ivri_event);
        ButterKnife.bind(this);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        KeyboardVisibilityEvent.setEventListener(this, this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        calendarStartTime = Calendar.getInstance();
        calendarStartTime.set(Calendar.MINUTE, 0);
        eventStartTime.setText(sdf.format(calendarStartTime.getTime()));
        calendarEndTime = Calendar.getInstance();
        calendarEndTime.set(Calendar.MINUTE, 0);
        calendarEndTime.set(Calendar.HOUR_OF_DAY, calendarEndTime.get(Calendar.HOUR_OF_DAY) + 1);
        eventEndTime.setText(sdf.format(calendarEndTime.getTime()));
    }

    @OnClick({R.id.event_start_day, R.id.event_end_day})void openDayDialog(){

    }

    @OnClick({R.id.event_start_time, R.id.event_end_time}) void openTimeDialog(TextView text){
        DialogFragment newFragment =  new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
        pickerState = (text.equals(eventStartTime)) ? PICKER_STATE.STATE_START_TIME : PICKER_STATE.STATE_END_TIME;
    }


    @OnClick(R.id.header_btn_save) void click(){
        boolean save = headerBtnSave.getText().equals("שמור");
        if (save){
            saveEvent();
        } else {
            InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.hideSoftInputFromWindow(headerTitleEditText.getWindowToken(), 0);
            headerBtnSave.setText("שמור");

        }
    }


    @OnClick(R.id.header_btn_x) void clickX(){
        finish();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        switch (pickerState){
            case STATE_START_TIME:
                calendarStartTime.set(Calendar.MINUTE, minute);
                calendarStartTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                eventStartTime.setText(sdf.format(calendarStartTime.getTime()));
                calendarEndTime.set(Calendar.MINUTE, minute);
                calendarEndTime.set(Calendar.HOUR_OF_DAY, ++hourOfDay);
                eventEndTime.setText(sdf.format(calendarEndTime.getTime()));
                break;
            case STATE_END_TIME:
                calendarEndTime.set(Calendar.MINUTE, minute);
                calendarEndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                eventEndTime.setText(sdf.format(calendarEndTime.getTime()));
        }
        pickerState = null;
    }


    private void saveEvent() {
        String title = headerTitleEditText.getText().toString();
        int startHour = calendarStartTime.get(Calendar.HOUR_OF_DAY);
        int startMinute = calendarStartTime.get(Calendar.MINUTE);
        int endHour = calendarEndTime.get(Calendar.HOUR_OF_DAY);
        int endMinute = calendarEndTime.get(Calendar.MINUTE);
        GoogleManager.addHebrewEventToGoogleServer(this, title, startHour, startMinute, endHour, endMinute);
        finish();
    }

    @Override
    public void onBackPressed() {
        headerBtnSave.setText("שמור");
        if (pickerState != null){
            pickerState = null;
        }
        super.onBackPressed();
    }

    //KeyBoard visibilty listener
    @Override
    public void onVisibilityChanged(boolean isOpen) {
        headerBtnSave.setText(isOpen? "בוצע" : "שמור");
        headerTitleEditText.setCursorVisible(isOpen);
    }

    private enum PICKER_STATE {
        STATE_START_TIME,
        STATE_END_TIME,
        STATE_START_DATE,
        STATE_END_DATE
    }
}
