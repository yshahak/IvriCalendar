package il.co.yshahak.ivricalendar;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import il.co.yshahak.ivricalendar.calendar.jewish.Month;

/**
 * Created by yshahak on 07/10/2016.
 */
public class CalendarRecyclerAdapter extends RecyclerView.Adapter<CalendarRecyclerAdapter.ViewHolder> {
    private final static String[] dayHeaders= {"ראשון" , "שני"  , "שלישי", "רביעי", "חמישי", "שישי", "שבת"};
    private final static int DAYS_IN_WEEK = 7;
    private final static int VIEW_TYPE_HEADER_WEEK = 0;
    private final static int VIEW_TYPE_DAY_CELL = 1;
    private final static int VIEW_TYPE_DAY_ENPTY = 2;
    private Month month;

    public CalendarRecyclerAdapter(Month month) {
        this.month = month;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < DAYS_IN_WEEK){
            return VIEW_TYPE_HEADER_WEEK;
        }
        if (position - DAYS_IN_WEEK < month.getHeadOffsetMonth()){
            return VIEW_TYPE_DAY_ENPTY;
        }
        return VIEW_TYPE_DAY_CELL;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.day_cell, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case VIEW_TYPE_HEADER_WEEK:
                holder.label.setText(dayHeaders[position]);
                break;
            case VIEW_TYPE_DAY_ENPTY:
                break;
            case VIEW_TYPE_DAY_CELL:
                holder.label.setText(Month.hebrewDateFormatter.formatHebrewNumber(++position - DAYS_IN_WEEK - month.getHeadOffsetMonth()));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return month.getMonthNumberOfDays() + month.getHeadOffsetMonth() + DAYS_IN_WEEK;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView label;

        public ViewHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.cell_label);
        }
    }
}
