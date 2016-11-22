package il.co.yshahak.ivricalendar.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import il.co.yshahak.ivricalendar.R;
import il.co.yshahak.ivricalendar.calendar.jewish.Week;

/**
 * Created by yshahak on 10/10/2016.
 */
public class DaysHeaderAdapter extends RecyclerView.Adapter<DaysHeaderAdapter.ViewHolder> {
    private final static int DAYS_IN_WEEK = 7;
    private final static String[] dayHeaders= {"ראשון" , "שני"  , "שלישי", "רביעי", "חמישי", "שישי", "שבת"};
    private Week week;

    public DaysHeaderAdapter(Week week) {
        this.week = week;
    }

    public DaysHeaderAdapter() {

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.day_header, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (week != null) {
            if (position == 0){
                return;
            }
            holder.label.setText(dayHeaders[position - 1] + "\n" + week.getDays()[position -1].getLabel());
        } else {
            holder.label.setText(dayHeaders[position]);
        }
    }

    @Override
    public int getItemCount() {
        if (week != null) {
            return DAYS_IN_WEEK + 1;
        }
        return DAYS_IN_WEEK;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView label;

        ViewHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.cell_label);
        }
    }
}
