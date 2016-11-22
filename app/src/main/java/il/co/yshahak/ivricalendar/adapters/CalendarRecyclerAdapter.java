package il.co.yshahak.ivricalendar.adapters;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import il.co.yshahak.ivricalendar.R;
import il.co.yshahak.ivricalendar.activities.MainActivity;
import il.co.yshahak.ivricalendar.calendar.google.Event;
import il.co.yshahak.ivricalendar.calendar.jewish.Day;
import il.co.yshahak.ivricalendar.calendar.jewish.Month;

/**
 * Created by yshahak on 07/10/2016.
 */
public class CalendarRecyclerAdapter extends RecyclerView.Adapter<CalendarRecyclerAdapter.ViewHolder> {
    private static final int REQUEST_CODE_EDIT_EVENT = 100;

    private final static int VIEW_TYPE_DAY_CELL = 1;
    private final static int VIEW_TYPE_HEAD = 2;
    private static final int VIEW_TYPE_TAIL = 3;
    private Month month;


    public CalendarRecyclerAdapter(Month monthes) {
        this.month = monthes;
    }

    @Override
    public int getItemViewType(int position) {

        if (position  <  month.getHeadOffsetMonth()){
            return VIEW_TYPE_HEAD;
        }
        if (position <  month.getHeadOffsetMonth() + month.getMonthNumberOfDays()) {
            return VIEW_TYPE_DAY_CELL;
        }
        return VIEW_TYPE_TAIL;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.day_cell, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case VIEW_TYPE_DAY_CELL:
                Day day = month.getDays()[position - month.getHeadOffsetMonth()];
                setDay(holder, day);
                break;

        }

    }

    private void setDay(ViewHolder holder, Day day){
        LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());
        holder.label.setText(day.getLabel());
        for (Event event : day.getGoogleEvents()){
            TextView textView = (TextView) inflater.inflate(R.layout.text_view_event, holder.cellContainer, false);
            textView.setText(event.getEventTitle());
            textView.setBackgroundColor(event.getDisplayColor());
            holder.cellContainer.addView(textView);
            textView.setTag(event);
            textView.setOnClickListener(holder);
            textView.setOnLongClickListener(holder);
        }
        if (month.isCurrentMonth() && month.getJewishCalendar().getJewishDayOfMonth() == day.getDayInMonth()){
            holder.label.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.colorPrimary));
        }
    }

    @Override
    public int getItemCount() {
        return  month.getHeadOffsetMonth() + month.getMonthNumberOfDays() + month.getTrailOffsetMonth() ;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private LinearLayout cellContainer;
        private TextView label;

        ViewHolder(View itemView) {
            super(itemView);
            cellContainer = (LinearLayout)itemView.findViewById(R.id.cell_root);
            label = (TextView) itemView.findViewById(R.id.cell_label);
        }

        @Override
        public void onClick(View view) {
            Event event = (Event) view.getTag();
            if (event != null) {
                Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, event.getEventId());
                Intent intent = new Intent(Intent.ACTION_VIEW)
                        .setData(uri);
                itemView.getContext().startActivity(intent);
                MainActivity.recreateFlag = true;
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
