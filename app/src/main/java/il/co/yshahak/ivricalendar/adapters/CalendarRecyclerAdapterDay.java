package il.co.yshahak.ivricalendar.adapters;

import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import il.co.yshahak.ivricalendar.R;
import il.co.yshahak.ivricalendar.calendar.google.Event;
import il.co.yshahak.ivricalendar.calendar.jewish.JewCalendar;

/**
 * Created by yshahak on 07/10/2016.
 */
public class CalendarRecyclerAdapterDay extends RecyclerView.Adapter<CalendarRecyclerAdapterDay.ViewHolder> {
    private static final int REQUEST_CODE_EDIT_EVENT = 100;
    //    private JewCalendar jewishCalendar;
    private ArrayList<Event> dayEvents;
    private SparseIntArray eventToHourMap;
    private final static long Hour = 1000 * 60 * 60;


    public CalendarRecyclerAdapterDay(JewCalendar jewishCalendar, ArrayList<Event> dayEvents, SparseIntArray eventToHourMap) {
//        this.jewishCalendar = jewishCalendar;
        this.dayEvents = dayEvents;
        this.eventToHourMap = eventToHourMap;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.day_display_row, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.labelHour.setText(position + 1 + ":00");
        setDayEvents(holder, position);
    }

    @SuppressWarnings("deprecation")
    private void setDayEvents(ViewHolder holder, int hour) {
        holder.eventContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());

        holder.separator.setVisibility(View.VISIBLE);
        int count = 0;
        for (Event event : dayEvents) {
            if ((hour >= event.getBeginDate().getHours())
                    && (hour <= event.getEndDate().getHours())) {
                TextView textView = (TextView) inflater.inflate(R.layout.text_view_event_for_day, holder.eventContainer, false);
                if (hour <= event.getBeginDate().getHours() && (event.getBeginDate().getHours() < hour + 1)) {
                    textView.setText(event.getEventTitle());
                }
                if (event.getEndDate().getHours() >= hour + 1) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.eventContainer.getLayoutParams();
                    params.addRule(RelativeLayout.ABOVE, 0);
                    holder.separator.setVisibility(View.INVISIBLE);
                    holder.eventContainer.setLayoutParams(params);
                }
                textView.setBackgroundColor(event.getDisplayColor());
                holder.eventContainer.addView(textView);
                textView.setTag(event);
                textView.setOnClickListener(holder);
                count++;
            }
        }
        int hourCount = eventToHourMap.get(hour);
        for (; count < hourCount; count++) {
            TextView textView = (TextView) inflater.inflate(R.layout.text_view_event_for_day, holder.eventContainer, false);
            holder.eventContainer.addView(textView);

        }
    }

    @Override
    public int getItemCount() {
        return 24;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private LinearLayout eventContainer;
        private TextView labelHour;
        private View separator;

        ViewHolder(View itemView) {
            super(itemView);
            eventContainer = (LinearLayout) itemView.findViewById(R.id.event_container);
            labelHour = (TextView) itemView.findViewById(R.id.text_view_hour);
            separator = itemView.findViewById(R.id.saparator);
        }

        @Override
        public void onClick(View view) {
            Event event = (Event) view.getTag();
            if (event != null) {

            }
        }
    }

    private void processDay() {
        List<Section> sections = new ArrayList<>();
        Section section = new Section();
        boolean activeRange = false;
        section.range.x = 0;
        for (int hour = 0; hour < 23; hour++) {
            int hourCount = eventToHourMap.get(hour);
            if (hourCount == 0) {
                if (activeRange) {
                    activeRange = false;
                    section.range.y = hour - 1;
                    sections.add(section);
                    section = new Section();
                    section.range.x = hour;
                }
            } else {
                if (!activeRange) {
                    activeRange = true;
                    section.range.y = hour - 1;
                    sections.add(section);
                    section = new Section();
                    section.range.x = hour;
                }
                section.count++;
            }
        }
    }

    class Section {
        int count;
        Point range = new Point();
    }
}
