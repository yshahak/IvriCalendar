package il.co.yshahak.ivricalendar.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
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
import il.co.yshahak.ivricalendar.calendar.google.Event;
import il.co.yshahak.ivricalendar.calendar.google.GoogleManager;
import il.co.yshahak.ivricalendar.calendar.jewish.Day;
import il.co.yshahak.ivricalendar.calendar.jewish.JewCalendar;
import il.co.yshahak.ivricalendar.calendar.jewish.Month;
import il.co.yshahak.ivricalendar.calendar.jewish.Week;

import static il.co.yshahak.ivricalendar.DividerItemDecoration.GRID;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.INSTANCE_PROJECTION;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_BEGIN_INDEX;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_CALENDAR_COLOR_INDEX;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_CALENDAR_DISPLAY_NAME_INDEX;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_DISPLAY_COLOR_INDEX;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_END_INDEX;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_ID_INDEX;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_TITLE_INDEX;

/**
 * Created by yshahak on 10/10/2016.
 */

public class FragmentWeek extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        View.OnClickListener{

    public static Day currentDay;
    private RecyclerView recyclerView, days;
    private Month month;
    private Week week;

    private final static int CURRENT_PAGE = 500;
    private static final String KEY_POSITION = "keyPosition";
    private JewCalendar jewishCalendar;
    private int position;

    public static FragmentWeek newInstance(int position) {
        FragmentWeek fragment = new FragmentWeek();
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
        jewishCalendar = new JewCalendar(offset);
        month = new Month(jewishCalendar, offset == 0);
        week = new Week(new JewishCalendar(), offset);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_month, container, false);
        days = (RecyclerView) root.findViewById(R.id.recycler_view_days);
        recyclerView = (RecyclerView)root.findViewById(R.id.recycler_view);

        days.setLayoutManager(new GridLayoutManager(getActivity(), 8 ));
        days.addItemDecoration(new DividerItemDecoration(getContext(), GRID));
        days.setHasFixedSize(true);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),  8));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), GRID));
        recyclerView.setHasFixedSize(true);
        MainActivity mainActivity = (MainActivity)getActivity();
        if (mainActivity.getSelectedPage() == position) {
            getActivity().setTitle(month.getMonthName() + " , " + month.getYearName());
        }
        getLoaderManager().initLoader(0, null, this);
        days.setAdapter(new DaysHeaderAdapter(week));

        setRecyclerView();
        return root;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String WHERE_CALENDARS_SELECTED = CalendarContract.Calendars.VISIBLE + " = ? "; //AND " +
        String[] WHERE_CALENDARS_ARGS = {"1"};//
        Uri uri = GoogleManager.asSyncAdapter(week);
        return new CursorLoader(getActivity(),  // Context
                uri, // URI
                INSTANCE_PROJECTION,                // Projection
                WHERE_CALENDARS_SELECTED,                           // Selection
                WHERE_CALENDARS_ARGS,                           // Selection args
                null); // Sort
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        new ProcessDates().execute(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void setRecyclerView(){
//        RecyclerView.Adapter adapter = recyclerView.getAdapter();
//        if (adapter == null) {
//            recyclerView.setAdapter(new CalendarRecyclerAdapterMonth(month, this));
//        } else {
//            adapter.notifyDataSetChanged();
//        }
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public Month getMonth() {
        return month;
    }

    @Override
    public void onClick(View v) {

    }

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
                for (Day day : month.getDays()){
                    if (start > day.getStartDayInMillis() && end < day.getEndDayInMillis()){
                        day.getGoogleEvents().add(event);
                        break;
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            setRecyclerView();
        }
    }

}