package il.co.yshahak.ivricalendar.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import il.co.yshahak.ivricalendar.DividerItemDecoration;
import il.co.yshahak.ivricalendar.R;
import il.co.yshahak.ivricalendar.adapters.CalendarRecyclerAdapterPicker;
import il.co.yshahak.ivricalendar.adapters.DaysHeaderAdapter;
import il.co.yshahak.ivricalendar.calendar.jewish.Day;
import il.co.yshahak.ivricalendar.calendar.jewish.JewCalendar;

import static il.co.yshahak.ivricalendar.DividerItemDecoration.GRID;

/**
 * Created by B.E.L on 27/11/2016.
 */

public class PickerFragment extends Fragment {
    private final static int CURRENT_PAGE = 500;
    private static final String KEY_POSITION = "keyPosition";
    private static final String KEY_JEW_CALENDAR = "keyJewCalendar";
//    private Month month;
    private List<Day> days;
    private JewCalendar jewishCalendar;


    public static PickerFragment newInstance(int position) {
        PickerFragment fragment = new PickerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_POSITION, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static PickerFragment newInstance(int position, JewCalendar jewCalendar) {
        PickerFragment fragment = new PickerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_POSITION, position);
        bundle.putParcelable(KEY_JEW_CALENDAR, jewCalendar);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jewishCalendar = getArguments().getParcelable(KEY_JEW_CALENDAR);
        int position = getArguments().getInt(KEY_POSITION);
        int offset = position - CURRENT_PAGE;

        if (jewishCalendar == null) {
            jewishCalendar = new JewCalendar(offset);
        }
        this.days = jewishCalendar.getDays(offset);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_month, container, false);
        RecyclerView days = (RecyclerView) root.findViewById(R.id.recycler_view_days);
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);


        days.setLayoutManager(new GridLayoutManager(getActivity(), 7));
        days.addItemDecoration(new DividerItemDecoration(getContext(), GRID));
        days.setHasFixedSize(true);


        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 7));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), GRID));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new CalendarRecyclerAdapterPicker(jewishCalendar, this.days));
        days.setAdapter(new DaysHeaderAdapter());
        return root;
    }
}
