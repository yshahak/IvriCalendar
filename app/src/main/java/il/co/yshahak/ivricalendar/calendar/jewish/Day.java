package il.co.yshahak.ivricalendar.calendar.jewish;

import java.util.ArrayList;

/**
 * Created by yshahak on 09/10/2016.
 */

public class Day {

    private ArrayList<String> googleEvents;
    private final String label;

    public Day(ArrayList<String> googleEvents, String label) {
        this.googleEvents = googleEvents;
        this.label = label;
    }

    public ArrayList<String> getGoogleEvents() {
        return googleEvents;
    }

    public String getLabel() {
        return label;
    }
}
