package il.co.yshahak.ivricalendar.fragments;

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

import net.alexandroid.shpref.MyLog;

import java.util.List;

import javax.inject.Inject;

import il.co.yshahak.ivricalendar.DividerItemDecoration;
import il.co.yshahak.ivricalendar.MyApplication;
import il.co.yshahak.ivricalendar.R;
import il.co.yshahak.ivricalendar.adapters.DaysHeaderAdapter;
import il.co.yshahak.ivricalendar.adapters.RecyclerAdapterMonth;
import il.co.yshahak.ivricalendar.calendar.EventsRepo;
import il.co.yshahak.ivricalendar.calendar.google.EventInstance;
import il.co.yshahak.ivricalendar.calendar.jewish.JewCalendar;

import static il.co.yshahak.ivricalendar.adapters.CalendarPagerAdapter.FRONT_PAGE;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.INSTANCE_PROJECTION;


/**
 * Created by Yaakov Shahak
 * on 21/06/17.
 */

public class FragmentHebrewMonth extends BaseCalendarFragment implements LoaderManager.LoaderCallbacks<Cursor>{

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
    EventsRepo eventsRepo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MyApplication) getActivity().getApplication()).getComponent().inject(this);

        final ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_month, container, false);
        RecyclerView daysRecycler = (RecyclerView) root.findViewById(R.id.recycler_view_days);

        daysRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 7));
        daysRecycler.addItemDecoration(itemDecoration);
        daysRecycler.setHasFixedSize(true);
        daysRecycler.setAdapter(new DaysHeaderAdapter());
        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 7));
        recyclerView.addItemDecoration(itemDecoration);

        new Thread(new Runnable() {
            @Override
            public void run() {
                jewCalendar.shiftMonth(position - FRONT_PAGE);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getLoaderManager().initLoader(0, null, FragmentHebrewMonth.this);
                        recyclerView.setAdapter(new RecyclerAdapterMonth(jewCalendar, getActivity().getResources().getColor(android.R.color.transparent), getActivity().getResources().getColor(R.color.colorPrimary)));
                    }
                });
            }
        }).start();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        jewCalendar.recycle();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri uri = eventsRepo.getInstanceUriForJewishMonth(jewCalendar);
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
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        List<EventInstance> list = eventsRepo.getEvents(cursor);
        MyLog.d("size = " + list.size());
        for (EventInstance event: list){
            MyLog.d(event.toString());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
