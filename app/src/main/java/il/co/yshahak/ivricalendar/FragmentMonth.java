package il.co.yshahak.ivricalendar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import il.co.yshahak.ivricalendar.calendar.jewish.Month;

import static il.co.yshahak.ivricalendar.DividerItemDecoration.GRID;

/**
 * Created by yshahak on 06/10/2016.
 */
public class FragmentMonth extends Fragment{

    private final static int CURRENT_PAGE = 500;
    private static final String KEY_POSITION = "keyPosition";
    private int position;

    public static Fragment newInstance(int position) {
        Fragment fragment = new FragmentMonth();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_POSITION, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.position = getArguments().getInt(KEY_POSITION);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_month, container, false);
        RecyclerView recyclerView = (RecyclerView)root.findViewById(R.id.recycler_view);
        Toolbar myToolbar = (Toolbar) root.findViewById(R.id.my_toolbar);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 7));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), GRID));
        int offset = position - CURRENT_PAGE;
//        Month month = MainActivity.year.getMonth(offset);
        Month month = new Month(getActivity(), offset);
        if (month != null) {
            recyclerView.setAdapter(new CalendarRecyclerAdapter(month));
            myToolbar.setTitle(month.getMonthName() + " , " + month.getYearName());
        }
        ((AppCompatActivity)getActivity()).setSupportActionBar(myToolbar);
        return root;
    }

    public int getPosition() {
        return position;
    }
}
