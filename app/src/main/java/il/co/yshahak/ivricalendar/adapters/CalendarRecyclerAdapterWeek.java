package il.co.yshahak.ivricalendar.adapters;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Color;
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
import il.co.yshahak.ivricalendar.calendar.google.EventInstance;
import il.co.yshahak.ivricalendar.calendar.jewish.Day;
import il.co.yshahak.ivricalendar.calendar.jewish.Week;

/**
 * Created by yshahak on 07/10/2016.
 */
public class CalendarRecyclerAdapterWeek extends RecyclerView.Adapter<CalendarRecyclerAdapterWeek.ViewHolder> {
    private static final int REQUEST_CODE_EDIT_EVENT = 100;

    private Week week;
    private final static long Hour = 1000 * 60 * 60;


    public CalendarRecyclerAdapterWeek(Week week) {
        this.week = week;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.day_cell_for_week, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int index = (position / 8) ;
        if (position % 8 == 0){
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            holder.label.setText("" + index + "");
        } else {
            int hour = position / 8;
            int day = position % 8;
            setDay(holder, week.getDays()[day - 1], hour);
            if (day == 4){
                holder.itemView.setBackgroundColor(Color.GRAY);
            } else {
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            }
            holder.label.setText("");
        }
    }

    private void setDay(ViewHolder holder, Day day, int hour){
//        LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());
//        Calendar cal = day.getCalendarForWeekDisplay();
//        cal.set(Calendar.HOUR, hour);
//        long time = cal.getTimeInMillis();
//        for (EventInstance event : day.getGoogleEventInstances()){
//            if (event.getBegin() >= time && event.getBegin()  < (time + Hour)) {
//                int emptyWeight =(int)((event.getBegin() - time) / 1000 / 60 / 15);
//                if (emptyWeight > 0){
//                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.label.getLayoutParams();
//                    params.weight = emptyWeight;
//                    holder.label.setLayoutParams(params);
//                }
//                Log.d("TAG", "hour: " + hour);
//                Log.d("TAG", "event: " + event.getEventTitle() + " , dif:" + (event.getBegin() - time) / 1000 / 60 / 15);
//                TextView textView = (TextView) inflater.inflate(R.layout.text_view_event, holder.cellContainer, false);
//                textView.setText(event.getEventTitle());
//                textView.setBackgroundColor(event.getDisplayColor());
//                holder.cellContainer.addView(textView);
//                textView.setTag(event);
//                textView.setOnClickListener(holder);
//                textView.setOnLongClickListener(holder);
//            }
//        }
    }

    @Override
    public int getItemCount() {
        return  8 * 24 ;
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
            EventInstance eventInstance = (EventInstance) view.getTag();
            if (eventInstance != null) {
                Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventInstance.getEventId());
                Intent intent = new Intent(Intent.ACTION_VIEW)
                        .setData(uri);
                itemView.getContext().startActivity(intent);
                MainActivity.recreateFlag = true;
            }
        }

        @Override
        public boolean onLongClick(View view) {
            EventInstance eventInstance = (EventInstance) view.getTag();
            if (eventInstance != null) {
                Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventInstance.getEventId());
                Intent intent = new Intent(Intent.ACTION_EDIT)
                        .setData(uri)
                        .putExtra(CalendarContract.Events.TITLE, eventInstance.getEventTitle());
                ((Activity)itemView.getContext()).startActivityForResult(intent, REQUEST_CODE_EDIT_EVENT);
            }
            return true;
        }
    }
}
