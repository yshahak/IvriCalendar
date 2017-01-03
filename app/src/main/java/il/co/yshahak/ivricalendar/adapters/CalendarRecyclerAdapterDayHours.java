package il.co.yshahak.ivricalendar.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import il.co.yshahak.ivricalendar.R;

/**
 * Created by yshahak on 07/10/2016.
 */
public class CalendarRecyclerAdapterDayHours extends RecyclerView.Adapter<CalendarRecyclerAdapterDayHours.ViewHolder> {


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.day_display_row, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.labelHour.setText(position + 1 + ":00");
    }

    @Override
    public int getItemCount() {
        return 24;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView labelHour;

        ViewHolder(View itemView) {
            super(itemView);
            labelHour = (TextView) itemView.findViewById(R.id.text_view_hour);
        }
    }
}
