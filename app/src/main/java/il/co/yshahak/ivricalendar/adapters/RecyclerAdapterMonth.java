package il.co.yshahak.ivricalendar.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import il.co.yshahak.ivricalendar.R;
import il.co.yshahak.ivricalendar.calendar.google.EventInstance;
import il.co.yshahak.ivricalendar.calendar.jewish.JewCalendar;

import static il.co.yshahak.ivricalendar.calendar.jewish.JewCalendar.hebrewDateFormatter;

/**
 * Created by yshahak on 07/10/2016.
 */
public class RecyclerAdapterMonth extends RecyclerView.Adapter<RecyclerAdapterMonth.ViewHolder> {

    private final int itemCount;
    private final static int VIEW_TYPE_DAY_CELL = 1;
    private final static int VIEW_TYPE_HEAD = 2;
    private static final int VIEW_TYPE_TAIL = 3;
    private final JewCalendar jewCalendar;
    private final int transparentColor, primaryColor;
    private final int cellHeight;
    private HashMap<Integer, List<EventInstance>> eventsMap;


    public RecyclerAdapterMonth(JewCalendar jewCalendar, int transparentColor, int primaryColor, int height) {
        this.jewCalendar = jewCalendar;
        this.transparentColor = transparentColor;
        this.primaryColor = primaryColor;
        this.itemCount = jewCalendar.getHeadOffset() + jewCalendar.getDaysInJewishMonth() + jewCalendar.getTrailOffset();
        cellHeight = height / (itemCount / 7);
    }

    public void setEventsMap(HashMap<Integer, List<EventInstance>> eventsMap) {
        this.eventsMap = eventsMap;
        for (int i: eventsMap.keySet()){
            notifyItemChanged(i);
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (position < jewCalendar.getHeadOffset()) {
            return VIEW_TYPE_HEAD;
        }
        if (position < jewCalendar.getHeadOffset() + jewCalendar.getDaysInJewishMonth()) {
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
        Object tagPosition = holder.itemView.getTag(R.string.tag_month_position);
        if (holder.itemView.getTag(R.string.tag_month_position) != null && (int) tagPosition == position + 1) {
            return;
        } else {
            holder.itemView.setTag(R.string.tag_month_position, position + 1);
        }
        holder.cellContainer.removeAllViews();
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_DAY_CELL:
                holder.label.setText(hebrewDateFormatter.formatHebrewNumber(position + 1));
                setDay(holder, position - jewCalendar.getHeadOffset());
                break;
//            default:
//                holder.label.setBackgroundColor(transparentColor);

        }
    }


    private void setDay(ViewHolder holder, int position) {
        holder.label.setText(hebrewDateFormatter.formatHebrewNumber(position + 1));
        if (jewCalendar.flagCurrentMonth) {
            if (position + 1 == jewCalendar.getJewishDayOfMonth()){
                holder.label.setBackgroundColor(primaryColor);
            } else {
                holder.label.setBackgroundColor(transparentColor);
            }
        }
        if (eventsMap == null) {
            return;
        }
        List<EventInstance> eventInstances = eventsMap.get(position + 1);
        if (eventInstances == null || eventInstances.size() == 0) {
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());
        for (EventInstance eventInstance : eventInstances){
            TextView textView = (TextView) inflater.inflate(R.layout.text_view_event_for_month, holder.cellContainer, false);
            textView.setText(eventInstance.getEventTitle());
            textView.setBackgroundColor(eventInstance.getDisplayColor());
            holder.cellContainer.addView(textView);
            textView.setTag(eventInstance);
//            textView.setOnClickListener(holder);
        }
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout cellContainer;
        private TextView label;

        ViewHolder(View itemView) {
            super(itemView);
            ViewGroup.LayoutParams params = itemView.getLayoutParams();
            params.height = cellHeight;
            itemView.setLayoutParams(params);
            cellContainer = (LinearLayout) itemView.findViewById(R.id.event_container);
            label = (TextView) itemView.findViewById(R.id.cell_label);
        }

    }

}
