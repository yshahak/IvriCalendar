package il.co.yshahak.ivricalendar.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.sourceforge.zmanim.hebrewcalendar.JewishCalendar;

import il.co.yshahak.ivricalendar.DividerItemDecoration;
import il.co.yshahak.ivricalendar.R;
import il.co.yshahak.ivricalendar.activities.MainActivity;
import il.co.yshahak.ivricalendar.adapters.DaysHeaderAdapter;
import il.co.yshahak.ivricalendar.adapters.RecyclerAdapterMonth;
import il.co.yshahak.ivricalendar.calendar.google.Event;
import il.co.yshahak.ivricalendar.calendar.jewish.Day;
import il.co.yshahak.ivricalendar.calendar.jewish.JewishCalendarContract;
import il.co.yshahak.ivricalendar.calendar.jewish.JewishDbManager;
import il.co.yshahak.ivricalendar.calendar.jewish.Week;

import static il.co.yshahak.ivricalendar.DividerItemDecoration.GRID;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_BEGIN_INDEX;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_CALENDAR_COLOR_INDEX;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_CALENDAR_DISPLAY_NAME_INDEX;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_DISPLAY_COLOR_INDEX;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_END_INDEX;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_ID_INDEX;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_TITLE_INDEX;
import static il.co.yshahak.ivricalendar.calendar.jewish.JewishCalendarContract.DateEntry.COLUMN_INDEX_MONTH_LABEL;
import static il.co.yshahak.ivricalendar.calendar.jewish.JewishCalendarContract.DateEntry.COLUMN_INDEX_YEAR_LABEL;
import static il.co.yshahak.ivricalendar.calendar.jewish.Month.shiftMonth;

/**
 * Created by yshahak on 10/10/2016.
 */

public class FragmentMonthLoader extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public static DISPLAY displayState = DISPLAY.MONTH;
    public static Day currentDay;
    private RecyclerView recyclerView, days;
//    private Month month;
    private Week week;
    private static JewishCalendar ivriCalendar = new JewishCalendar();

    private final static int CURRENT_PAGE = 500;
    private static final String KEY_POSITION = "keyPosition";
    private JewishCalendar jewishCalendar;
    private int position;

    public static FragmentMonthLoader newInstance(int position) {
        FragmentMonthLoader fragment = new FragmentMonthLoader();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_POSITION, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.position = getArguments().getInt(KEY_POSITION);
        int offset = position - CURRENT_PAGE;
        jewishCalendar = shiftMonth(new JewishCalendar(), offset);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_month, container, false);
        days = (RecyclerView) root.findViewById(R.id.recycler_view_days);
        recyclerView = (RecyclerView)root.findViewById(R.id.recycler_view);


        days.setLayoutManager(new GridLayoutManager(getActivity(), displayState == DISPLAY.WEEK ? 8 : 7));
        days.addItemDecoration(new DividerItemDecoration(getContext(), GRID));
        days.setHasFixedSize(true);


        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), displayState == DISPLAY.WEEK ? 8 : 7));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), GRID));
        recyclerView.setHasFixedSize(true);

        getLoaderManager().initLoader(0, null, this);
        if (displayState == DISPLAY.WEEK) {
            days.setAdapter(new DaysHeaderAdapter(week));
        } else {
            days.setAdapter(new DaysHeaderAdapter());
        }
        return root;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String WHERE_CALENDARS_SELECTED = JewishCalendarContract.DateEntry.COLUMN_DATE_YEAR + " = ? AND " +
                JewishCalendarContract.DateEntry.COLUMN_DATE_MONTH + " = ?";
        String[] WHERE_CALENDARS_ARGS = new String[]{Integer.toString(jewishCalendar.getJewishYear()), Integer.toString(jewishCalendar.getJewishMonth())};//

        Uri uri = JewishCalendarContract.DateEntry.CONTENT_URI;
        return new CursorLoader(getActivity(),  // Context
                uri, // URI
                null,                // Projection
                WHERE_CALENDARS_SELECTED,                           // Selection
                WHERE_CALENDARS_ARGS,                           // Selection args
                null); // Sort
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (isAdded()) {
            if (cursor != null && cursor.moveToNext()) {
                MainActivity mainActivity = (MainActivity)getActivity();
                if (mainActivity.getSelectedPage() == position) {
                    getActivity().setTitle(cursor.getString(COLUMN_INDEX_MONTH_LABEL) + " , " + cursor.getString(COLUMN_INDEX_YEAR_LABEL));
                }
                setRecyclerView(cursor);
            } else {
                JewishDbManager.addMonth(getActivity(), jewishCalendar);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void setRecyclerView(Cursor cursor){
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter == null) {
            recyclerView.setAdapter(new RecyclerAdapterMonth(cursor));
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter != null) {
            ((RecyclerAdapterMonth)adapter).getMonthCursor().close();
        }
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

//    public Month getMonth() {
//        return month;
//    }

    class ProcessDates extends AsyncTask<Cursor, Void, Void>{

        @Override
        protected Void doInBackground(Cursor... cursors) {
            int eventId;
            String title, calendarName;
            Long  start, end;
            int displayColor, calendarColor;
            Cursor cursor = cursors[0];
            while (cursor.moveToNext()) {
                eventId = cursor.getInt(PROJECTION_ID_INDEX);
                title = cursor.getString(PROJECTION_TITLE_INDEX);
                start = cursor.getLong((PROJECTION_BEGIN_INDEX));
                end = cursor.getLong((PROJECTION_END_INDEX));
                calendarName = cursor.getString(PROJECTION_CALENDAR_DISPLAY_NAME_INDEX);
                displayColor = cursor.getInt(PROJECTION_DISPLAY_COLOR_INDEX);
                calendarColor = cursor.getInt(PROJECTION_CALENDAR_COLOR_INDEX);
                boolean allDayEvent = (end - start) == 1000*60*60*24;
                if (allDayEvent){
                    end = start;
                }
                Event event = new Event(eventId, title, allDayEvent, start, end, displayColor, calendarName);

                if (displayState == DISPLAY.WEEK) {
                    for (Day day : week.getDays()){
                        if (start > day.getStartDayInMillis() && end < day.getEndDayInMillis()){
                            day.getGoogleEvents().add(event);
                            break;
                        }
                    }
                } else {
//                    for (Day day : month.getDays()){
//                        if (start > day.getStartDayInMillis() && end < day.getEndDayInMillis()){
//                            day.getGoogleEvents().add(event);
//                            break;
//                        }
//                    }
                }
            }
             return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            setRecyclerView();
        }
    }

    public enum DISPLAY{
        MONTH,
        WEEK,
        DAY
    }
}
