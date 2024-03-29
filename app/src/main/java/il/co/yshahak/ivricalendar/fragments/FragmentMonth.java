package il.co.yshahak.ivricalendar.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
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

import il.co.yshahak.ivricalendar.uihelpers.DividerItemDecoration;
import il.co.yshahak.ivricalendar.R;
import il.co.yshahak.ivricalendar.adapters.DaysHeaderAdapter;
import il.co.yshahak.ivricalendar.calendar.google.EventInstance;

import static il.co.yshahak.ivricalendar.calendar.google.Contract.INSTANCE_PROJECTION;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_BEGIN_INDEX;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_CALENDAR_COLOR_INDEX;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_CALENDAR_DISPLAY_NAME_INDEX;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_DISPLAY_COLOR_INDEX;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_END_INDEX;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_EVENT_ID;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_TITLE_INDEX;

/**
 * Created by yshahak on 10/10/2016.
 */

public class FragmentMonth extends BaseCalendarFragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private RecyclerView recyclerView;
    private SparseArray<List<EventInstance>> events = new SparseArray<>();
    private ViewGroup root;

    public static FragmentMonth newInstance(int position) {
        FragmentMonth fragment = new FragmentMonth();
        init(fragment, position);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        jewishCalendar = (JewCalendar) MainActivity.currentJewCalendar.clone();
//        jewishCalendar.shiftMonth(position - FRONT_PAGE);
        if (events.size() == 0) {
            getLoaderManager().initLoader(0, null, this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (root != null) {
            return root;
        }
        root = (ViewGroup) inflater.inflate(R.layout.fragment_month, container, false);
        RecyclerView daysRecycler = (RecyclerView) root.findViewById(R.id.recycler_view_days);
        recyclerView = (RecyclerView)root.findViewById(R.id.recycler_view);

        daysRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 7));
        daysRecycler.addItemDecoration(new DividerItemDecoration(getContext()));
        daysRecycler.setHasFixedSize(true);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 7));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
        recyclerView.setHasFixedSize(true);
        daysRecycler.setAdapter(new DaysHeaderAdapter());
        setRecyclerView();
        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("check", "check");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String WHERE_CALENDARS_SELECTED = CalendarContract.Calendars.VISIBLE + " = ? "; //AND " +
        String[] WHERE_CALENDARS_ARGS = {"1"};//
//        Uri uri = GoogleManager.asSyncAdapterMonth(jewishCalendar);
        return new CursorLoader(getActivity(),  // Context
                Uri.EMPTY, // URI
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
//        if (adapter == null) {
//            recyclerView.setAdapter(new CalendarRecyclerAdapterMonth(jewishCalendar, events, (View.OnClickListener) getActivity()));
//        } else {
//            adapter.notifyDataSetChanged();
//        }
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
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
                eventId = cursor.getInt(PROJECTION_EVENT_ID);
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
                EventInstance eventInstance = new EventInstance(eventId, title, allDayEvent, start, end, displayColor, calendarName, calendar.getJewishDayOfMonth());
                List<EventInstance> list = events.get(calendar.getJewishDayOfMonth());
                if (list == null) {
                    list = new ArrayList<>();
                    events.put(calendar.getJewishDayOfMonth(), list);
                }
                Log.d("TAG", eventInstance.getEventTitle() + " , " + simpleDateFormat.format(end)+ " - "
                        + simpleDateFormat.format(start));

                list.add(eventInstance);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            setRecyclerView();
        }
    }

}