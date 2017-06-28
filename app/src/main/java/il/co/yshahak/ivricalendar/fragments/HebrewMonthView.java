package il.co.yshahak.ivricalendar.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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

public class HebrewMonthView extends LinearLayout {

    private int position;

    public HebrewMonthView(Context context, int position) {
        super(context);
        ((MyApplication)context.getApplicationContext()).getComponent().inject(this);

        this.position = position;
        setOrientation(VERTICAL);
        init();
    }

    public HebrewMonthView(Context context) {
        super(context);
    }

    @Inject
    JewCalendar jewCalendar;

    public void init() {
        jewCalendar.shiftMonth(position - FRONT_PAGE);
        ViewGroup root = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.fragment_month, this, false);
        RecyclerView daysRecycler = (RecyclerView) root.findViewById(R.id.recycler_view_days);
        RecyclerView recyclerView = (RecyclerView)root.findViewById(R.id.recycler_view);

        daysRecycler.setLayoutManager(new GridLayoutManager(getContext(), 7));
        daysRecycler.addItemDecoration(new DividerItemDecoration(getContext(), GRID));
        daysRecycler.setHasFixedSize(true);
        daysRecycler.setAdapter(new DaysHeaderAdapter());

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 7));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), GRID));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new RecyclerAdapterMonth(jewCalendar, getContext().getResources().getColor(android.R.color.transparent), getContext().getResources().getColor(R.color.colorPrimary)));

        addView(root);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        jewCalendar.recycle();
    }

}
