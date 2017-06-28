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


    public RecyclerAdapterMonth(JewCalendar jewCalendar, int transparentColor, int primaryColor) {
        this.jewCalendar = jewCalendar;
        this.transparentColor = transparentColor;
        this.primaryColor = primaryColor;
        this.itemCount = jewCalendar.getHeadOffset() + jewCalendar.getDaysInJewishMonth() + jewCalendar.getTrailOffset();

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
        if (position + 1 == jewCalendar.getJewishDayOfMonth()) {
            holder.label.setBackgroundColor(primaryColor);
        } else {
            holder.label.setBackgroundColor(transparentColor);
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
            cellContainer = (LinearLayout) itemView.findViewById(R.id.event_container);
            label = (TextView) itemView.findViewById(R.id.cell_label);
        }

    }

}
