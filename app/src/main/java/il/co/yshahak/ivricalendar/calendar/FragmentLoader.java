package il.co.yshahak.ivricalendar.calendar;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.sourceforge.zmanim.hebrewcalendar.JewishCalendar;

import il.co.yshahak.ivricalendar.CalendarRecyclerAdapter;
import il.co.yshahak.ivricalendar.DaysHeaderAdapter;
import il.co.yshahak.ivricalendar.DividerItemDecoration;
import il.co.yshahak.ivricalendar.R;
import il.co.yshahak.ivricalendar.calendar.google.Contract;
import il.co.yshahak.ivricalendar.calendar.jewish.Day;
import il.co.yshahak.ivricalendar.calendar.jewish.Month;

import static il.co.yshahak.ivricalendar.DividerItemDecoration.GRID;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.INSTANCE_PROJECTION;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_BEGIN_INDEX;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_END_INDEX;
import static il.co.yshahak.ivricalendar.calendar.google.Contract.PROJECTION_TITLE_INDEX;
import static il.co.yshahak.ivricalendar.calendar.jewish.Month.shiftMonth;

/**
 * Created by yshahak on 10/10/2016.
 */

public class FragmentLoader extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{


    private RecyclerView recyclerView, days;
    private Month month;

    private final static int CURRENT_PAGE = 500;
    private static final String KEY_POSITION = "keyPosition";
    private JewishCalendar jewishCalendar;
    private int position;

    public static Fragment newInstance(int position) {
        Fragment fragment = new FragmentLoader();
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
        jewishCalendar = shiftMonth(new JewishCalendar(), offset);
        month = new Month(jewishCalendar);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_month, container, false);
        Toolbar myToolbar = (Toolbar) root.findViewById(R.id.my_toolbar);
        days = (RecyclerView) root.findViewById(R.id.recycler_view_days);
        recyclerView = (RecyclerView)root.findViewById(R.id.recycler_view);


        days.setLayoutManager(new GridLayoutManager(getActivity(), 7));
        days.addItemDecoration(new DividerItemDecoration(getContext(), GRID));
        days.setHasFixedSize(true);
        days.setAdapter(new DaysHeaderAdapter());

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 7));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), GRID));
        recyclerView.setHasFixedSize(true);

        myToolbar.setTitle(month.getMonthName() + " , " + month.getYearName());
        ((AppCompatActivity)getActivity()).setSupportActionBar(myToolbar);
        getLoaderManager().initLoader(0, null, this);

        return root;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // We only have one loader, so we can ignore the value of i.
        // (It'll be '0', as set in onCreate().)
        String WHERE_CALENDARS_SELECTED = CalendarContract.Calendars.VISIBLE + "=?";
        String[] WHERE_CALENDARS_ARGS = {"1"};
        return new CursorLoader(getActivity(),  // Context
                Contract.asSyncAdapter(jewishCalendar), // URI
                INSTANCE_PROJECTION,                // Projection
                WHERE_CALENDARS_SELECTED,                           // Selection
                WHERE_CALENDARS_ARGS,                           // Selection args
                null); // Sort
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        String title;
        Long  start, end;
        while (cursor.moveToNext()) {
            title = cursor.getString(PROJECTION_TITLE_INDEX);
            start = cursor.getLong((PROJECTION_BEGIN_INDEX));
            end = cursor.getLong((PROJECTION_END_INDEX));
            boolean allDayEvent = (end - start) == 1000*60*60*24;
            if (allDayEvent){
                end = start;
            }
            for (Day day : month.getDays()){
                if (start > day.getStartDayInMillis() && end < day.getEndDayInMillis()){
                    day.getGoogleEvents().add(title);
                    break;
                }
            }
//            DateFormat formatter = new SimpleDateFormat("MM/dd/yy HH:mm", Locale.getDefault());
//            begin = formatter.format(new Date(start));
//            finish = formatter.format(new Date(end));
//            Log.i("TAG", "Event:  " + title + " , start: " + begin + " ,end " + finish );
        }
        setRecyclerView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void setRecyclerView(){
        recyclerView.setAdapter(new CalendarRecyclerAdapter(month));
    }
}
