package il.co.yshahak.ivricalendar.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import il.co.yshahak.ivricalendar.R;

/**
 * Created by yshahak on 10/10/2016.
 */
public class DaysHeaderAdapter extends RecyclerView.Adapter<DaysHeaderAdapter.ViewHolder> {
    private final static int DAYS_IN_WEEK = 7;
    private final static String[] dayHeaders= {"ראשון" , "שני"  , "שלישי", "רביעי", "חמישי", "שישי", "שבת"};


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.day_header, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.label.setText(dayHeaders[position]);
    }

    @Override
    public int getItemCount() {
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
