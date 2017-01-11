package il.co.yshahak.ivricalendar.fragments;

import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import il.co.yshahak.ivricalendar.R;
import il.co.yshahak.ivricalendar.activities.MainActivity;
import il.co.yshahak.ivricalendar.adapters.CalendarRecyclerAdapterDaily;
import il.co.yshahak.ivricalendar.adapters.CalendarRecyclerAdapterDayHours;
import il.co.yshahak.ivricalendar.calendar.google.Event;
import il.co.yshahak.ivricalendar.calendar.google.GoogleManager;
import il.co.yshahak.ivricalendar.calendar.jewish.JewCalendar;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static il.co.yshahak.ivricalendar.adapters.CalendarPagerAdapter.FRONT_PAGE;
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

public class FragmentDay extends BaseCalendarFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        View.OnClickListener{

    private RecyclerView recyclerViewEvents;

    private ArrayList<Event> dayEvents = new ArrayList<>();
    private SparseArray<List<Event>> eventToHourMap = new SparseArray<>();
    private ArrayList<Section> sections = new ArrayList<>();
    private boolean enableScrolling = true;


    public static FragmentDay newInstance(int position) {
        FragmentDay fragment = new FragmentDay();
        init(fragment, position);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jewishCalendar = (JewCalendar) MainActivity.currentJewCalendar.clone();
        jewishCalendar.shiftDay(position - FRONT_PAGE);
        getLoaderManager().initLoader(0, null, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_day, container, false);
        recyclerViewEvents = (RecyclerView)root.findViewById(R.id.recycler_view_events);

        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewEvents.setHasFixedSize(true);
        ((TextView)root.findViewById(R.id.day_label)).setText(jewishCalendar.getDayLabel() + "\n"
                + JewCalendar.hebrewDateFormatter.formatDayOfWeek(jewishCalendar));
        final RecyclerView recyclerViewHours = (RecyclerView)root.findViewById(R.id.recycler_view_hours);
        recyclerViewHours.setLayoutManager(new LinearLayoutManager(getActivity())
        {
            @Override
            public boolean canScrollVertically() {
                return enableScrolling;
            }
        });
        recyclerViewHours.setHasFixedSize(true);
        recyclerViewHours.setAdapter(new CalendarRecyclerAdapterDayHours());
        recyclerViewEvents.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                recyclerViewHours.scrollBy(dx, dy);
            }
        });
        recyclerViewHours.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                enableScrolling = newState != SCROLL_STATE_DRAGGING;

            }
        });
        setRecyclerView();
        return root;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String WHERE_CALENDARS_SELECTED = CalendarContract.Calendars.VISIBLE + " = ? "; //AND " +
        String[] WHERE_CALENDARS_ARGS = {"1"};//
        Uri uri = GoogleManager.asSyncAdapterDay(jewishCalendar);
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
        RecyclerView.Adapter adapter = recyclerViewEvents.getAdapter();
        if (adapter == null) {
            recyclerViewEvents.setAdapter(new CalendarRecyclerAdapterDaily(sections));
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    public RecyclerView getRecyclerViewEvents() {
        return recyclerViewEvents;
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
//                Log.d("TAG", "begin: " + start + " end: " + end);
                Event event = new Event(eventId, title, allDayEvent, start, end, displayColor, calendarName);
                event.setBeginDate(new Date(start));
                event.setEndDate(new Date(end));
                int endHour = event.getEndDate().getMinutes() > 0 ? event.getEndDate().getHours() + 1 : event.getEndDate().getHours();
                for (int i = event.getBeginDate().getHours() ; i < endHour; i++){
                    List<Event> eventList= eventToHourMap.get(i);
                    if (eventList == null) {
                        eventList = new ArrayList<>();
                        eventToHourMap.put(i, eventList);
                    }
                    eventList.add(event);
                }
                dayEvents.add(event);

            }
            processDay();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            setRecyclerView();
        }
    }

    private void processDay() {
        sections.clear();
        Section section = new Section();
        boolean activeRange = false;
        section.range.x = 0;
        for (int hour = 0; hour < 23; hour++) {
            List<Event> events = eventToHourMap.get(hour);
            if (events == null || events.size() == 0) {
                if (activeRange) {
                    activeRange = false;
                    Section newSection = new Section();
                    for (Event event : section.sectionEvents){
                        int eventTime = (event.getEndDate().getHours() * 60) + event.getEndDate().getMinutes();
                        if (section.range.y == 0){
                            section.range.y = eventTime;
                            newSection.range.x = eventTime;
                        }
                        else if (eventTime > section.range.y){
                            section.range.y = eventTime;
                            newSection.range.x = eventTime;
                        }
                    }
                    sections.add(section);
                    section = newSection;
                }
            } else {
                if (!activeRange) {
                    activeRange = true;
                    Section newSection = new Section();
                    for(Event event : events){
                        int eventTime = (event.getBeginDate().getHours() * 60) + event.getBeginDate().getMinutes();
                        if (newSection.range.x == 0){
                            section.range.y = eventTime;
                            newSection.range.x = eventTime;
                        }
                        else if (eventTime > newSection.range.x){
                            newSection.range.x = eventTime;
                        }
                    }
                    sections.add(section);
                    section = newSection;
                }
                for (Event event : events) {
                    if (!section.sectionEvents.contains(event)) {
                        section.sectionEvents.add(event);
                    }
                }
            }
        }
        section.range.y = 24 * 60;
        sections.add(section);
    }

    public class Section {
        public List<Event> sectionEvents = new ArrayList<>();
        public Point range = new Point();
    }

}