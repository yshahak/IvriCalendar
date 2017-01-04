package il.co.yshahak.ivricalendar.activities;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import il.co.yshahak.ivricalendar.calendar.jewish.JewCalendar;
import il.co.yshahak.ivricalendar.fragments.TimePickerFragment;
import il.co.yshahak.ivricalendar.views.HebrewPickerDialog;
import me.angrybyte.numberpicker.view.ActualNumberPicker;

import static il.co.yshahak.ivricalendar.calendar.jewish.Month.hebrewDateFormatter;

/**
 * Created by B.E.L on 31/10/2016.
 */

public class CreteIvriEventActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener
        , KeyboardVisibilityEventListener, PopupMenu.OnMenuItemClickListener{

//    public static final String EXTRA_USE_CURRENT_DAY = "EXTRA_USE_CURRENT_DAY" ;
    public static JewCalendar currentCalendar;
    @BindView(R.id.header_btn_save) TextView headerBtnSave;
    @BindView(R.id.header_btn_x) ImageView headerBtnX;
    @BindView(R.id.header_edit_text_event_title) EditText headerTitleEditText;
    @BindView(R.id.checkbox_all_day_event) SwitchCompat switchCompatAllDay;
    @BindView(R.id.event_start_day )TextView eventStartDay;
    @BindView(R.id.event_end_day) TextView eventEndDay;
    @BindView(R.id.event_start_time) TextView eventStartTime;
    @BindView(R.id.event_end_time) TextView eventEndTime;
    @BindView(R.id.event_instances) TextView textViewRepeat;
    @BindView(R.id.event_count_title)TextView eventCountTitle;
    //    @BindView(R.id.progress_bar)ProgressBar progressBar;
    @BindView(R.id.countPicker) ActualNumberPicker eventCountPicker;

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
        textViewRepeat.setTag(R.id.repeat_single);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setDates();

    }

    private void setDates() {
//        boolean useCurrentDay = getIntent().getBooleanExtra(EXTRA_USE_CURRENT_DAY, false);
        if (currentCalendar == null ){
            currentCalendar = new JewCalendar();
        }
        String date = hebrewDateFormatter.formatHebrewNumber(currentCalendar.getJewishDayOfMonth()) + " " +
                hebrewDateFormatter.formatMonth(currentCalendar)   ;
        hebrewDateFormatter.setLongWeekFormat(true);
        String day = hebrewDateFormatter.formatDayOfWeek(currentCalendar) + " , " + date;
        eventStartDay.setText(day);
        eventEndDay.setText(day);
        calendarStartTime = Calendar.getInstance();
        calendarStartTime.setTime(currentCalendar.getTime());
        calendarStartTime.set(Calendar.MINUTE, 0);
        eventStartTime.setText(sdf.format(calendarStartTime.getTime()));
        calendarEndTime = Calendar.getInstance();
        calendarEndTime.setTime(currentCalendar.getTime());
        calendarEndTime.set(Calendar.MINUTE, 0);
        calendarEndTime.set(Calendar.HOUR_OF_DAY, calendarEndTime.get(Calendar.HOUR_OF_DAY) + 1);
        eventEndTime.setText(sdf.format(calendarEndTime.getTime()));
    }

    @OnClick({R.id.event_start_day, R.id.event_end_day})void openDayDialog(){
        DialogFragment dialog = new HebrewPickerDialog();
        HebrewPickerDialog.onDatePickerDismiss = onDatePickerDismiss;

        dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");

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


    @OnClick(R.id.event_instances) void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_event_instances, popup.getMenu());
        int repeatId = (int) textViewRepeat.getTag();
        int position = 0;
        switch (repeatId){
            case R.id.repeat_single:
                break;
            case R.id.repeat_daily:
                position = 1;
                break;
            case R.id.repeat_weekly:
                position = 2;
                break;
            case R.id.repeat_monthly:
                position = 3;
                break;
            case R.id.repeat_yearly:
                position = 4;
                break;
        }
        popup.getMenu().getItem(position).setChecked(true);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        textViewRepeat.setText(item.getTitle());
        textViewRepeat.setTag(item.getItemId());
        if (item.getItemId() != R.id.repeat_single){
            eventCountPicker.setVisibility(View.VISIBLE);
            eventCountTitle.setVisibility(View.VISIBLE);
        }
        switch (item.getItemId()){
            case R.id.repeat_single:
                eventCountPicker.setVisibility(View.GONE);
                eventCountTitle.setVisibility(View.GONE);
                break;
            case R.id.repeat_daily:
                eventCountPicker.setValue(365);
                break;
            case R.id.repeat_weekly:
                eventCountPicker.setValue(36);
                break;
            case R.id.repeat_monthly:
                eventCountPicker.setValue(12);
                break;
            case R.id.repeat_yearly:
                eventCountPicker.setValue(10);
                break;
        }
        return false;
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
        int repeatId = (int) textViewRepeat.getTag();
        GoogleManager.addHebrewEventToGoogleServer(this, title, repeatId, calendarStartTime, calendarEndTime, String.valueOf(eventCountPicker.getValue()));
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

    OnDatePickerDialog onDatePickerDismiss = new OnDatePickerDialog() {
        @Override
        public void onBtnOkPressed() {
            setDates();
        }

        @Override
        public void onAttached() {
//            progressBar.setVisibility(View.GONE);
        }
    };




    private enum PICKER_STATE {
        STATE_START_TIME,
        STATE_END_TIME,
        STATE_START_DATE,
        STATE_END_DATE
    }

    public interface OnDatePickerDialog {
        void onBtnOkPressed();
        void onAttached();
    }

}