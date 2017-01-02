package il.co.yshahak.ivricalendar.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import il.co.yshahak.ivricalendar.R;
import il.co.yshahak.ivricalendar.calendar.google.Event;
import il.co.yshahak.ivricalendar.fragments.FragmentDay;

/**
 * Created by yshahak on 07/10/2016.
 */
public class CalendarRecyclerAdapterDaily extends RecyclerView.Adapter<CalendarRecyclerAdapterDaily.ViewHolder> {
    private static final int REQUEST_CODE_EDIT_EVENT = 100;
    private static final int VIEW_TYPE_EMPTY_RANGE = 0;
    private static final int VIEW_TYPE_EVENTS_RANGE = 1;
    private SparseArray<List<Event>> eventToHourMap;
    private final static long Hour = 1000 * 60 * 60;
    private List<FragmentDay.Section> sections = new ArrayList<>();


    public CalendarRecyclerAdapterDaily(List<FragmentDay.Section> sections) {
        this.sections = sections;
    }

    @Override
    public int getItemViewType(int position) {
        if (sections.get(position).sectionEvents.size() == 0){
            return VIEW_TYPE_EMPTY_RANGE;
        } else {
            return VIEW_TYPE_EVENTS_RANGE;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup root = null;
        switch (viewType){
            case VIEW_TYPE_EMPTY_RANGE:
                root = new LinearLayout(parent.getContext());
                ((LinearLayout)root).setOrientation(LinearLayout.VERTICAL);
                break;
            case VIEW_TYPE_EVENTS_RANGE:
                root = new LinearLayout(parent.getContext());
                ((LinearLayout)root).setOrientation(LinearLayout.HORIZONTAL);
                break;
        }
        return new ViewHolder(root);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FragmentDay.Section section = sections.get(position);
        holder.eventContainer.removeAllViews();
        switch (holder.getItemViewType()){
            case VIEW_TYPE_EMPTY_RANGE:
                for (int i = 0; i < (section.range.y - section.range.x) ; i++){
                    View view = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.day_display_empty, holder.eventContainer, false);
                    holder.eventContainer.addView(view);
                }
                break;
            case VIEW_TYPE_EVENTS_RANGE:
                int range = section.range.y - section.range.x;
                LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());
                for (Event event : section.sectionEvents){
                    TextView textView = (TextView) inflater.inflate(R.layout.text_view_event_for_day, holder.eventContainer, false);
                    textView.setText(event.getEventTitle());
                    textView.setBackgroundColor(event.getDisplayColor());
                    holder.eventContainer.addView(textView);
                    textView.setTag(event);
                    textView.setOnClickListener(holder);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return sections.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private LinearLayout eventContainer;

        ViewHolder(View itemView) {
            super(itemView);
            eventContainer = (LinearLayout) itemView;
        }

        @Override
        public void onClick(View view) {
            Event event = (Event) view.getTag();
            if (event != null) {

            }
        }
    }

}
