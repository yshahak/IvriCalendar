package il.co.yshahak.ivricalendar.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import il.co.yshahak.ivricalendar.R;
import il.co.yshahak.ivricalendar.calendar.jewish.Day;
import il.co.yshahak.ivricalendar.calendar.jewish.Month;
import il.co.yshahak.ivricalendar.views.HebrewPickerDialog;

/**
 * Created by yshahak on 07/10/2016.
 */
public class CalendarRecyclerAdapterPicker extends RecyclerView.Adapter<CalendarRecyclerAdapterPicker.ViewHolder> {

    private final static int VIEW_TYPE_DAY_CELL = 1;
    private final static int VIEW_TYPE_HEAD = 2;
    private static final int VIEW_TYPE_TAIL = 3;
    private Month month;


    public CalendarRecyclerAdapterPicker(Month monthes) {
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
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.day_cell_for_picker, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case VIEW_TYPE_DAY_CELL:
                Day day = month.getDays().get(position - month.getHeadOffsetMonth());
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
        return  month.getHeadOffsetMonth() + month.getMonthNumberOfDays() + month.getTrailOffsetMonth() ;
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
                HebrewPickerDialog.currentDay = month.getDays().get(getAdapterPosition() - month.getHeadOffsetMonth());
                notifyDataSetChanged();
            }
        }

    }
}
