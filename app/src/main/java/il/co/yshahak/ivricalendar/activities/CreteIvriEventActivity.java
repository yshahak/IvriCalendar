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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import il.co.yshahak.ivricalendar.R;
import il.co.yshahak.ivricalendar.fragments.TimePickerFragment;

/**
 * Created by B.E.L on 31/10/2016.
 */

public class CreteIvriEventActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ivri_event);
        ButterKnife.bind(this);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        eventStartTime.setText(sdf.format(calendar.getTime()));
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);
        eventEndTime.setText(sdf.format(calendar.getTime()));
    }

    @OnClick({R.id.event_start_day, R.id.event_end_day})void openDayDialog(){

    }

    @OnClick({R.id.event_start_time, R.id.event_end_time}) void openTimeDialog(TextView text){
        DialogFragment newFragment =  new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
        pickerState = (text.equals(eventStartTime)) ? PICKER_STATE.STATE_START_TIME : PICKER_STATE.STATE_END_TIME;
    }

    @OnTextChanged(R.id.header_edit_text_event_title) void eventTitleChanged(CharSequence text){
        headerBtnSave.setText(text.length() > 0 ? "בוצע" : "שמור");
    }

    @OnClick(R.id.header_btn_save) void click(){
        boolean save = headerBtnSave.getText().equals("שמור");
        if (save){
            saveEvent();
        } else {
            InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.hideSoftInputFromWindow(headerTitleEditText.getWindowToken(), 0);
            headerBtnSave.setText("שמור");

//            headerTitleEditText.setCursorVisible(false);
        }
    }


    @OnClick(R.id.header_btn_x) void clickX(){
        finish();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        switch (pickerState){
            case STATE_START_TIME:
                eventStartTime.setText(sdf.format(calendar.getTime()));
                calendar.set(Calendar.HOUR_OF_DAY, ++hourOfDay);
                eventEndTime.setText(sdf.format(calendar.getTime()));
                break;
            case STATE_END_TIME:
                eventEndTime.setText(sdf.format(calendar.getTime()));
        }
        pickerState = null;
    }
    private void saveEvent() {
        long calID = 3;
        long startMillis = 0;
        long endMillis = 0;
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2012, 9, 14, 7, 30);
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(2012, 9, 14, 8, 45);
        endMillis = endTime.getTimeInMillis();

//        ContentResolver cr = getContentResolver();
//        ContentValues values = new ContentValues();
//        values.put(Events.DTSTART, startMillis);
//        values.put(Events.DTEND, endMillis);
//        values.put(Events.TITLE, "Jazzercise");
//        values.put(Events.DESCRIPTION, "Group workout");
//        values.put(Events.CALENDAR_ID, calID);
//        values.put(Events.EVENT_TIMEZONE, "America/Los_Angeles");
//        Uri uri = cr.insert(Events.CONTENT_URI, values);
//
//        long eventID = Long.parseLong(uri.getLastPathSegment());

    }



    @Override
    public void onBackPressed() {
        if (pickerState != null){
            pickerState = null;
        }
        super.onBackPressed();
    }

    private enum PICKER_STATE {
        STATE_START_TIME,
        STATE_END_TIME,
        STATE_START_DATE,
        STATE_END_DATE
    }
}
