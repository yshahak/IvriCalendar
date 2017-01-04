package il.co.yshahak.ivricalendar.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import il.co.yshahak.ivricalendar.R;
import il.co.yshahak.ivricalendar.activities.CreteIvriEventActivity;
import il.co.yshahak.ivricalendar.calendar.jewish.JewCalendar;

/**
 * Created by yshahak on 07/10/2016.
 */
public class CalendarRecyclerAdapterPicker extends RecyclerView.Adapter<CalendarRecyclerAdapterPicker.ViewHolder> {

    private final static int VIEW_TYPE_DAY_CELL = 1;
    private final static int VIEW_TYPE_HEAD = 2;
    private static final int VIEW_TYPE_TAIL = 3;
//    private Month month;
    private JewCalendar jewCalendar;

    public CalendarRecyclerAdapterPicker(JewCalendar jewCalendar) {
        this.jewCalendar = jewCalendar;
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
                setDay(holder, position - jewCalendar.getHeadOffset());
                break;
            default:
                holder.label.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.transparent));
        }

    }


    private void setDay(ViewHolder holder, int position) {
        jewCalendar.setJewishDayOfMonth(position + 1);
        holder.label.setText(jewCalendar.getDayLabel());
        if (jewCalendar.equals(CreteIvriEventActivity.currentCalendar)) {
            if (position + 1 == jewCalendar.getJewishDayOfMonth()) {
                holder.label.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.colorPrimary));
            } else {
                holder.label.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.transparent));
            }
        }
        holder.itemView.setTag(position + 1);
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
                CreteIvriEventActivity.currentCalendar.setJewishDayOfMonth((Integer) itemView.getTag());
                notifyDataSetChanged();
            }
        }

    }
}
