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
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.sourceforge.zmanim.hebrewcalendar.JewishCalendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import il.co.yshahak.ivricalendar.DividerItemDecoration;
import il.co.yshahak.ivricalendar.R;
import il.co.yshahak.ivricalendar.activities.MainActivity;
import il.co.yshahak.ivricalendar.adapters.CalendarRecyclerAdapterMonth;
import il.co.yshahak.ivricalendar.adapters.DaysHeaderAdapter;
import il.co.yshahak.ivricalendar.calendar.google.Event;
import il.co.yshahak.ivricalendar.calendar.google.GoogleManager;
import il.co.yshahak.ivricalendar.calendar.jewish.Day;
import il.co.yshahak.ivricalendar.calendar.jewish.JewCalendar;

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

public class FragmentMonth extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public static Day currentDay;
    public static int currentDayOfMonth;
    private RecyclerView recyclerView, daysRecycler;
//    private Month month;
    private List<Day> days = new ArrayList<>();
    private SparseArray<List<Event>> events = new SparseArray<>();
    private JewCalendar jewishCalendar;

    private final static int CURRENT_PAGE = 500;
    private static final String KEY_POSITION = "keyPosition";
    private int position;

    public static FragmentMonth newInstance(int position) {
        FragmentMonth fragment = new FragmentMonth();
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
//        shiftMonth(new JewishCalendar(), offset);
//        month = new Month(jewishCalendar, offset == 0);
        jewishCalendar = new JewCalendar(offset);
        this.days = jewishCalendar.getDays(offset);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_month, container, false);
        daysRecycler = (RecyclerView) root.findViewById(R.id.recycler_view_days);
        recyclerView = (RecyclerView)root.findViewById(R.id.recycler_view);

        daysRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 7));
        daysRecycler.addItemDecoration(new DividerItemDecoration(getContext(), GRID));
        daysRecycler.setHasFixedSize(true);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 7));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), GRID));
        recyclerView.setHasFixedSize(true);
        MainActivity mainActivity = (MainActivity)getActivity();
        if (mainActivity.getSelectedPage() == position) {
            getActivity().setTitle(jewishCalendar.getMonthName() + " , " + jewishCalendar.getYearName());
        }
        getLoaderManager().initLoader(0, null, this);

        daysRecycler.setAdapter(new DaysHeaderAdapter());
        setRecyclerView();
        return root;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String WHERE_CALENDARS_SELECTED = CalendarContract.Calendars.VISIBLE + " = ? "; //AND " +
        String[] WHERE_CALENDARS_ARGS = {"1"};//
        Uri uri = GoogleManager.asSyncAdapter(jewishCalendar);
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
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter == null) {
            recyclerView.setAdapter(new CalendarRecyclerAdapterMonth(jewishCalendar, events, (View.OnClickListener) getActivity()));
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

//    public Month getMonth() {
//        return month;
//    }


    public JewCalendar getJewishCalendar() {
        return jewishCalendar;
    }

    class ProcessDates extends AsyncTask<Cursor, Void, Void>{

        @Override
        protected Void doInBackground(Cursor... cursors) {
            int eventId;
            String title, calendarName;
            Long  start, end;
            int displayColor, calendarColor;
            Cursor cursor = cursors[0];
            JewishCalendar calendar;

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.US);

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
                calendar = new JewishCalendar(new Date(start));
                Event event = new Event(eventId, title, allDayEvent, start, end, displayColor, calendarName, calendar.getJewishDayOfMonth());
                List<Event> list = events.get(calendar.getJewishDayOfMonth());
                if (list == null) {
                    list = new ArrayList<>();
                    events.put(calendar.getJewishDayOfMonth(), list);
                }
                Log.d("TAG", event.getEventTitle() + " , " + simpleDateFormat.format(end)+ " - "
                        + simpleDateFormat.format(start));

                list.add(event);
//                for (Day day : days){
//                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            setRecyclerView();
        }
    }

}