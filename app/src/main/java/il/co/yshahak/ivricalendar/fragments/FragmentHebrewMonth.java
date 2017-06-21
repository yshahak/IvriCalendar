package il.co.yshahak.ivricalendar.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import il.co.yshahak.ivricalendar.DividerItemDecoration;
import il.co.yshahak.ivricalendar.MyApplication;
import il.co.yshahak.ivricalendar.R;
import il.co.yshahak.ivricalendar.adapters.DaysHeaderAdapter;
import il.co.yshahak.ivricalendar.adapters.RecyclerAdapterMonth;
import il.co.yshahak.ivricalendar.calendar.jewish.JewCalendar;

import static il.co.yshahak.ivricalendar.DividerItemDecoration.GRID;
import static il.co.yshahak.ivricalendar.adapters.CalendarPagerAdapter.FRONT_PAGE;

/**
 * Created by Yaakov Shahak
 * on 21/06/17.
 */

public class FragmentHebrewMonth extends BaseCalendarFragment {

    public static BaseCalendarFragment newInstance(int position) {
        FragmentHebrewMonth fragment = new FragmentHebrewMonth();
        init(fragment, position);
        return fragment;
    }

    @Inject
    JewCalendar jewCalendar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MyApplication)getActivity().getApplication()).getComponent().inject(this);
        jewCalendar.shiftMonth(position - FRONT_PAGE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_month, container, false);
        RecyclerView daysRecycler = (RecyclerView) root.findViewById(R.id.recycler_view_days);
        RecyclerView recyclerView = (RecyclerView)root.findViewById(R.id.recycler_view);

        daysRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 7));
        daysRecycler.addItemDecoration(new DividerItemDecoration(getContext(), GRID));
        daysRecycler.setHasFixedSize(true);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 7));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), GRID));
        recyclerView.setHasFixedSize(true);
        daysRecycler.setAdapter(new DaysHeaderAdapter());

        recyclerView.setAdapter(new RecyclerAdapterMonth(jewCalendar, getActivity().getResources().getColor(android.R.color.transparent), getActivity().getResources().getColor(R.color.colorPrimary)));

        return root;
    }
}
