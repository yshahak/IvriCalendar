package il.co.yshahak.ivricalendar.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import il.co.yshahak.ivricalendar.DividerItemDecoration;
import il.co.yshahak.ivricalendar.R;
import il.co.yshahak.ivricalendar.adapters.DaysHeaderAdapter;

/**
 * Created by B.E.L on 27/11/2016.
 */

public class PickerFragment extends BaseCalendarFragment {

    public static PickerFragment newInstance(int position) {
        PickerFragment fragment = new PickerFragment();
        init(fragment, position);
        return fragment;
    }

//    @Override
//    public void setJewishCalendar(JewCalendar jewishCalendar) {
//        this.jewishCalendar = jewishCalendar;
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_month, container, false);
        RecyclerView days = (RecyclerView) root.findViewById(R.id.recycler_view_days);
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);


        days.setLayoutManager(new GridLayoutManager(getActivity(), 7));
        days.addItemDecoration(new DividerItemDecoration(getContext()));
        days.setHasFixedSize(true);
        days.setAdapter(new DaysHeaderAdapter());

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 7));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
        recyclerView.setHasFixedSize(true);
//        recyclerView.setAdapter(new CalendarRecyclerAdapterPicker(jewishCalendar));
        return root;
    }
}
