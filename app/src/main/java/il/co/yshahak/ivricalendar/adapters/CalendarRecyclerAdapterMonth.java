package il.co.yshahak.ivricalendar.adapters;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
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
import il.co.yshahak.ivricalendar.calendar.jewish.JewCalendar;
import il.co.yshahak.ivricalendar.fragments.FragmentMonth;

/**
 * Created by yshahak on 07/10/2016.
 */
public class CalendarRecyclerAdapterMonth extends RecyclerView.Adapter<CalendarRecyclerAdapterMonth.ViewHolder> {
    private static final int REQUEST_CODE_EDIT_EVENT = 100;

    private final static int VIEW_TYPE_DAY_CELL = 1;
    private final static int VIEW_TYPE_HEAD = 2;
    private static final int VIEW_TYPE_TAIL = 3;
    private final View.OnClickListener clickListener;
//    private Month month;
    private SparseArray<List<Event>> eventSparseArray;
    private JewCalendar jewCalendar;


    public CalendarRecyclerAdapterMonth(JewCalendar jewCalendar, SparseArray<List<Event>> eventSparseArray, View.OnClickListener listener) {
        this.jewCalendar = jewCalendar;
        this.eventSparseArray = eventSparseArray;
        this.clickListener = listener;
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
//                Day day = days.get(position - jewCalendar.getHeadOffset());
//                setDay(holder, day);
                setDay(holder, position- jewCalendar.getHeadOffset());
                break;
            default:
                holder.label.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.transparent));

        }
    }


    private void setDay(ViewHolder holder, int position){
        LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());
        jewCalendar.setJewishDayOfMonth(position + 1);
        holder.label.setText(jewCalendar.getDayLabel() + "    ");
        if (position + 1 ==  FragmentMonth.currentDayOfMonth){
            holder.label.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.colorPrimary));
        }else {
            holder.label.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.transparent));
        }
        List<Event> events = eventSparseArray.get(position + 1);
        if (events == null) {
            return;
        }
        for (Event event : events){
            TextView textView = (TextView) inflater.inflate(R.layout.text_view_event_for_month, holder.cellContainer, false);
            textView.setText(event.getEventTitle());
            textView.setBackgroundColor(event.getDisplayColor());
            holder.cellContainer.addView(textView);
            textView.setTag(event);
            textView.setOnClickListener(holder);
            textView.setOnLongClickListener(holder);
        }

    }

    @Override
    public int getItemCount() {
        return  jewCalendar.getTrailOffset() + jewCalendar.getDaysInJewishMonth() + jewCalendar.getTrailOffset() ;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private LinearLayout cellContainer;
        private TextView label;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            cellContainer = (LinearLayout)itemView.findViewById(R.id.event_container);
            label = (TextView) itemView.findViewById(R.id.cell_label);
        }

        @Override
        public void onClick(View view) {
            if (view.equals(itemView)){
                if (getItemViewType() == VIEW_TYPE_DAY_CELL) {
//                    FragmentMonth.currentDay = days.get(getAdapterPosition() - jewCalendar.getHeadOffset());
                    clickListener.onClick(view);
//                    notifyDataSetChanged();
                }
            } else {
                Event event = (Event) view.getTag();
                if (event != null) {
                    Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, event.getEventId());
                    Intent intent = new Intent(Intent.ACTION_VIEW)
                            .setData(uri);
                    itemView.getContext().startActivity(intent);
                    MainActivity.recreateFlag = true;
                }
            }
        }

        @Override
        public boolean onLongClick(View view) {
            Event event = (Event) view.getTag();
            if (event != null) {
                Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, event.getEventId());
                Intent intent = new Intent(Intent.ACTION_EDIT)
                        .setData(uri)
                        .putExtra(CalendarContract.Events.TITLE, event.getEventTitle());
                ((Activity)itemView.getContext()).startActivityForResult(intent, REQUEST_CODE_EDIT_EVENT);
            }
            return true;
        }
    }
}
