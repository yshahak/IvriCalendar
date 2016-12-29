package il.co.yshahak.ivricalendar.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import il.co.yshahak.ivricalendar.R;
import il.co.yshahak.ivricalendar.calendar.jewish.Day;
import il.co.yshahak.ivricalendar.calendar.jewish.JewCalendar;
import il.co.yshahak.ivricalendar.views.HebrewPickerDialog;

/**
 * Created by yshahak on 07/10/2016.
 */
public class CalendarRecyclerAdapterPicker extends RecyclerView.Adapter<CalendarRecyclerAdapterPicker.ViewHolder> {

    private final static int VIEW_TYPE_DAY_CELL = 1;
    private final static int VIEW_TYPE_HEAD = 2;
    private static final int VIEW_TYPE_TAIL = 3;
//    private Month month;
    private JewCalendar jewCalendar;
    private List<Day> days;

    public CalendarRecyclerAdapterPicker(JewCalendar jewCalendar, List<Day> days) {
        this.jewCalendar = jewCalendar;
        this.days = days;
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
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.day_cell_for_picker, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case VIEW_TYPE_DAY_CELL:
                Day day = days.get(position - jewCalendar.getHeadOffset());
                setDay(holder, day);
                break;
            default:
                holder.label.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.transparent));
        }

    }

    private void setDay(ViewHolder holder, Day day){
        holder.label.setText(day.getLabel() + "    " + day.getJewishCalendar().getTime().getDate());
        if (day.equals(HebrewPickerDialog.currentDay)){
            holder.label.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.colorPrimary));
        } else {
            holder.label.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.transparent));
        }
    }

    @Override
    public int getItemCount() {
        return  jewCalendar.getHeadOffset() + jewCalendar.getDaysInJewishMonth() + jewCalendar.getTrailOffset() ;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView label;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            label = (TextView) itemView.findViewById(R.id.cell_label);
        }

        @Override
        public void onClick(View view) {
            if (getItemViewType() == VIEW_TYPE_DAY_CELL) {
                HebrewPickerDialog.currentDay = days.get(getAdapterPosition() - jewCalendar.getHeadOffset());
                notifyDataSetChanged();
            }
        }

    }
}
