package il.co.yshahak.ivricalendar.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import il.co.yshahak.ivricalendar.R;
import il.co.yshahak.ivricalendar.activities.MainActivity;
import il.co.yshahak.ivricalendar.calendar.google.Event;
import il.co.yshahak.ivricalendar.calendar.google.GoogleManager;
import il.co.yshahak.ivricalendar.calendar.jewish.JewCalendar;

/**
 * Created by yshahak on 07/10/2016.
 */
public class CalendarRecyclerAdapterMonth extends RecyclerView.Adapter<CalendarRecyclerAdapterMonth.ViewHolder> {

    private final static int VIEW_TYPE_DAY_CELL = 1;
    private final static int VIEW_TYPE_HEAD = 2;
    private static final int VIEW_TYPE_TAIL = 3;
    private final View.OnClickListener clickListener;
//    private Month month;
    private SparseArray<List<Event>> eventSparseArray;
    private JewCalendar jewCalendar;
    private int transparentColor, primaryColor;


    public CalendarRecyclerAdapterMonth(JewCalendar jewCalendar, SparseArray<List<Event>> eventSparseArray, View.OnClickListener listener) {
        this.jewCalendar = jewCalendar;
        this.eventSparseArray = eventSparseArray;
        this.clickListener = listener;
        this.transparentColor = ((Context)listener).getResources().getColor(android.R.color.transparent);
        this.primaryColor = ((Context)listener).getResources().getColor(R.color.colorPrimary);
    }

    @Override
    public int getItemViewType(int position) {

        if (position  <  jewCalendar.getHeadOffset()){
            return VIEW_TYPE_HEAD;
        }
        if (position <  jewCalendar.getHeadOffset() + jewCalendar.getDaysInJewishMonth()) {
            return VIEW_TYPE_DAY_CELL;
        }
        return VIEW_TYPE_TAIL;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.day_cell_for_month, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.cellContainer.removeAllViews();
        switch (holder.getItemViewType()){
            case VIEW_TYPE_DAY_CELL:
                setDay(holder, position - jewCalendar.getHeadOffset());
                break;
            default:
                holder.label.setBackgroundColor(transparentColor);

        }
    }


    private void setDay(ViewHolder holder, int position){
        jewCalendar.setJewishDayOfMonth(position + 1);
        holder.itemView.setTag(R.string.tag_month_position, position + 1);
        holder.label.setText(jewCalendar.getDayLabel());
        if (jewCalendar.getJewishMonth() == MainActivity.currentJewCalendar.getJewishMonth()) { //todo compare also year...
            if (position + 1 == MainActivity.currentJewCalendar.getJewishDayOfMonth()) {
                holder.label.setBackgroundColor(primaryColor);
            } else {
                holder.label.setBackgroundColor(transparentColor);
            }
        }
        List<Event> events = eventSparseArray.get(position + 1);
        if (events == null) {
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());
        for (Event event : events){
            TextView textView = (TextView) inflater.inflate(R.layout.text_view_event_for_month, holder.cellContainer, false);
            textView.setText(event.getEventTitle());
            textView.setBackgroundColor(event.getDisplayColor());
            holder.cellContainer.addView(textView);
            textView.setTag(event);
            textView.setOnClickListener(holder);
        }

    }

    @Override
    public int getItemCount() {
        return  jewCalendar.getHeadOffset() + jewCalendar.getDaysInJewishMonth() + jewCalendar.getTrailOffset() ;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private LinearLayout cellContainer;
        private TextView label;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            cellContainer = (LinearLayout) itemView.findViewById(R.id.event_container);
            label = (TextView) itemView.findViewById(R.id.cell_label);
        }

        @Override
        public void onClick(View view) {
            if (view.equals(itemView)) {
                if (getItemViewType() == VIEW_TYPE_DAY_CELL) {
                    int monthDay = (int) itemView.getTag(R.string.tag_month_position);
                    jewCalendar.setJewishDayOfMonth(monthDay);
                    view.setTag(R.string.tag_month_position, JewCalendar.getDaysDifference(jewCalendar, new JewCalendar()));
                    clickListener.onClick(view);
                }
            } else {
                Event event = (Event) view.getTag();
                if (event != null) {
                    GoogleManager.openEvent(itemView.getContext(), event);
                }
            }
        }
    }

}
