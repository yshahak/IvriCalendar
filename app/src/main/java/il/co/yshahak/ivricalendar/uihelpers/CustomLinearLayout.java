package il.co.yshahak.ivricalendar.uihelpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import il.co.yshahak.ivricalendar.R;
import il.co.yshahak.ivricalendar.calendar.google.Event;
import il.co.yshahak.ivricalendar.calendar.jewish.Day;

/**
 * Created by B.E.L on 23/11/2016.
 */

public class CustomLinearLayout extends LinearLayout {
    private final static long Hour = 1000 * 60 * 60;

    BitmapDrawable bm;

    public CustomLinearLayout(Context context) {
        super(context);
        init();
    }

    public CustomLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public CustomLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init(){
        Drawable d = getResources().getDrawable(R.drawable.week_cell_rect);
        if (d != null) {
            Bitmap b = drawableToBitmap(d);
            bm = new BitmapDrawable(getResources(), b);
            bm.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        }
    }

    public void setDay(Day day){
//        Calendar cal = day.getCalendarForWeekDisplay();
//        List<List<Event>> dayEvents = new ArrayList<>();
//        for (int i =0 ; i < 24 ; i++) {
//            cal.set(Calendar.HOUR, i);
//            long time = cal.getTimeInMillis();
//            List<Event> hourEvents = new ArrayList<>();
//            for (Event event : day.getGoogleEvents()) {
//                if (event.getBegin() >= time && event.getBegin() < (time + Hour)) {
//                    hourEvents.add(event);
//                }
//            }
//            dayEvents.add(hourEvents);
//        }
//        for (List<Event> list : dayEvents){
//            for (Event event : list) {
//
//            }
//
//        }
        LayoutInflater inflater = LayoutInflater.from(getContext());
        List<Point> intervals = new ArrayList<>();
        for (Event event : day.getGoogleEvents()){
            Point point = new Point();
            point.x = (int)event.getBegin();
            point.y = (int)event.getEnd();
            intervals.add(point);
            TextView textView = (TextView) inflater.inflate(R.layout.text_view_event, this, false);
            textView.setText(event.getEventTitle());
            textView.setBackgroundColor(event.getDisplayColor());
            addView(textView);
            textView.setTag(event);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bm != null) {
            bm.setBounds(canvas.getClipBounds());
            bm.draw(canvas);
        }

    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
