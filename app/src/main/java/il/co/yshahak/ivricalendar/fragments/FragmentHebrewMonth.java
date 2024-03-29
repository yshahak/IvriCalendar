package il.co.yshahak.ivricalendar.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import il.co.yshahak.ivricalendar.MonthViewModelFactory;
import il.co.yshahak.ivricalendar.MyApplication;
import il.co.yshahak.ivricalendar.R;
import il.co.yshahak.ivricalendar.adapters.DaysHeaderAdapter;
import il.co.yshahak.ivricalendar.adapters.RecyclerAdapterMonth;
import il.co.yshahak.ivricalendar.calendar.EventsHelper;
import il.co.yshahak.ivricalendar.calendar.EventsProvider;
import il.co.yshahak.ivricalendar.calendar.google.EventInstance;
import il.co.yshahak.ivricalendar.calendar.jewish.Day;
import il.co.yshahak.ivricalendar.calendar.jewish.JewCalendar;
import il.co.yshahak.ivricalendar.room.database.CalendarDataBase;
import il.co.yshahak.ivricalendar.uihelpers.DividerItemDecoration;
import il.co.yshahak.ivricalendar.views.MonthViewModel;

import static il.co.yshahak.ivricalendar.adapters.CalendarPagerAdapter.FRONT_PAGE;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.INSTANCE_PROJECTION;


/**
 * Created by Yaakov Shahak
 * on 21/06/17.
 */

public class FragmentHebrewMonth extends BaseCalendarFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private MonthViewModel monthViewModel;

    public static BaseCalendarFragment newInstance(int position) {
        FragmentHebrewMonth fragment = new FragmentHebrewMonth();
        init(fragment, position);
        return fragment;
    }

    @Inject
    JewCalendar jewCalendar;
    @Inject
    DividerItemDecoration itemDecoration;
    @Inject
    EventsProvider eventsProvider;
    @Inject
    MonthViewModelFactory viewModelFactory;
    @Inject
    CalendarDataBase calendarDataBase;

    private RecyclerView recyclerView;
    private LiveData<List<Day>> dayList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MyApplication) getActivity().getApplication()).getComponent().inject(this);
        jewCalendar.shiftMonth(position - FRONT_PAGE);
//        Month month = calendarDataBase.monthDao().getByHashCode(jewCalendar.monthHashCode());
        monthViewModel = ViewModelProviders.of(this, viewModelFactory).get(MonthViewModel.class);
        dayList = monthViewModel.getDayList(jewCalendar, position - FRONT_PAGE);
        dayList.observe(FragmentHebrewMonth.this,
                days -> setRecyclerView());

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_month, container, false);
        RecyclerView daysRecycler = (RecyclerView) root.findViewById(R.id.recycler_view_days);
        daysRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 7));
        daysRecycler.addItemDecoration(itemDecoration);
        daysRecycler.setHasFixedSize(true);
        daysRecycler.setAdapter(new DaysHeaderAdapter());
        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 7));
        recyclerView.addItemDecoration(itemDecoration);
        setRecyclerView();
        return root;
    }

    private void setRecyclerView() {
        if (dayList.getValue() != null && dayList.getValue().size() > 0) {
            recyclerView.setAdapter(new RecyclerAdapterMonth(dayList.getValue(), getActivity().getResources().getColor(android.R.color.transparent), getActivity().getResources().getColor(R.color.colorPrimary), recyclerView.getHeight()));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        jewCalendar.recycle();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri uri = eventsProvider.getInstanceUriForJewishMonth(jewCalendar);
        String WHERE_CALENDARS_SELECTED = CalendarContract.Calendars.VISIBLE + " = ? "; //AND " +
        String[] WHERE_CALENDARS_ARGS = {"1"};//
        return new CursorLoader(getActivity(),
                uri,
                INSTANCE_PROJECTION,
                WHERE_CALENDARS_SELECTED,
                WHERE_CALENDARS_ARGS,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor cursor) {
        if (!isAdded()) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                final HashMap<Integer, List<EventInstance>> eventsMap = EventsHelper.getEventsMap(cursor);
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (isAdded()) {
//                            MyLog.d("size = " + eventsMap.size());
//                            RecyclerAdapterMonth adapterMonth = (RecyclerAdapterMonth) recyclerView.getAdapter();
//                            if (adapterMonth == null) {
//                                adapterMonth = new RecyclerAdapterMonth(jewCalendar, getActivity().getResources().getColor(android.R.color.transparent), getActivity().getResources().getColor(R.color.colorPrimary), recyclerView.getHeight());
//                                recyclerView.setAdapter(adapterMonth);
//                            }
//                            adapterMonth.setEventsMap(eventsMap);
//                        }
//                    }
//                });
            }
        }).start();


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loader.reset();
    }
}
