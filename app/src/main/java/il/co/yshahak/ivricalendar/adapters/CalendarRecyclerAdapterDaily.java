package il.co.yshahak.ivricalendar.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import il.co.yshahak.ivricalendar.R;
import il.co.yshahak.ivricalendar.calendar.google.EventInstance;
import il.co.yshahak.ivricalendar.calendar.google.GoogleManager;
import il.co.yshahak.ivricalendar.fragments.FragmentDay;

/**
 * Created by yshahak on 07/10/2016.
 */
public class CalendarRecyclerAdapterDaily extends RecyclerView.Adapter<CalendarRecyclerAdapterDaily.ViewHolder> {
    private static final int VIEW_TYPE_EMPTY_RANGE = 0;
    private static final int VIEW_TYPE_EVENTS_RANGE = 1;
    private List<FragmentDay.Section> sections = new ArrayList<>();


    public CalendarRecyclerAdapterDaily(List<FragmentDay.Section> sections) {
        this.sections = sections;
    }

    @Override
    public int getItemViewType(int position) {
        if (sections.get(position).sectionEventInstances.size() == 0){
            return VIEW_TYPE_EMPTY_RANGE;
        } else {
            return VIEW_TYPE_EVENTS_RANGE;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup root = null;
        switch (viewType){
            case VIEW_TYPE_EMPTY_RANGE:
                root = new LinearLayout(parent.getContext());
                ((LinearLayout)root).setOrientation(LinearLayout.VERTICAL);
                break;
            case VIEW_TYPE_EVENTS_RANGE:
                root = new LinearLayout(parent.getContext());
                root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                ((LinearLayout)root).setOrientation(LinearLayout.HORIZONTAL);
                break;
        }
        return new ViewHolder(root);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FragmentDay.Section section = sections.get(position);
        holder.eventContainer.removeAllViews();
        int hourHeight = holder.itemView.getResources().getDimensionPixelSize(R.dimen.hour_height);
        ViewGroup.LayoutParams rootParams =  holder.eventContainer.getLayoutParams();
        switch (holder.getItemViewType()){
            case VIEW_TYPE_EMPTY_RANGE:
                int margin;
                View view = null;
                if (section.range.x % 60 > 0){
                    margin = (int) (((60 - section.range.x % 60) / 60f) * hourHeight);
                    view = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.day_display_empty, holder.eventContainer, false);
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
                    params.height = margin;
                    holder.eventContainer.addView(view);
                }
                for (int i = 0; i < (section.range.y - section.range.x) / 60 ; i++){
                    view = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.day_display_empty, holder.eventContainer, false);
                    holder.eventContainer.addView(view);
                }

                if (section.range.y % 60 > 0){
                    margin = (int) (((section.range.y % 60) / 60f) * hourHeight);
                    LinearLayout.LayoutParams params;
                    if (view == null){
                        view = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.day_display_empty, holder.eventContainer, false);
                        params = (LinearLayout.LayoutParams) view.getLayoutParams();
                        params.height = margin;
                        holder.eventContainer.addView(view, params);
                    } else {
                        params = (LinearLayout.LayoutParams) view.getLayoutParams();
                        params.bottomMargin = margin;
                        view.setLayoutParams(params);
                    }

                }
                break;
            case VIEW_TYPE_EVENTS_RANGE:
                int range = section.range.y - section.range.x;
                rootParams.height =(int) ((range / 60f) * hourHeight);
                LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());
                holder.eventContainer.setOrientation(LinearLayout.HORIZONTAL);
                for (EventInstance eventInstance : section.sectionEventInstances){
                    int start = eventInstance.getBeginDate().getHours() * 60 + eventInstance.getBeginDate().getMinutes();
                    int length = eventInstance.getEndDate().getHours() * 60 + eventInstance.getEndDate().getMinutes() - start;
                    CardView eventContainer = (CardView) inflater.inflate(R.layout.text_view_event_for_day, holder.eventContainer, false);
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) eventContainer.getLayoutParams();
                    params.height = (int)((length / 60f) * hourHeight);
                    params.topMargin = (int) (((start - section.range.x) / 60f) * hourHeight);
                    eventContainer.setCardBackgroundColor(eventInstance.getDisplayColor());
                    TextView textView = (TextView) eventContainer.findViewById(R.id.event_title);
                    textView.setText(eventInstance.getEventTitle());
                    holder.eventContainer.addView(eventContainer);
                    textView.setTag(eventInstance);
                    textView.setOnClickListener(holder);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return sections.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private LinearLayout eventContainer;

        ViewHolder(View itemView) {
            super(itemView);
            eventContainer = (LinearLayout) itemView;
        }

        @Override
        public void onClick(View view) {
            EventInstance eventInstance = (EventInstance) view.getTag();
            if (eventInstance != null) {
                GoogleManager.openEvent(itemView.getContext(), eventInstance);
            }
        }
    }

}
