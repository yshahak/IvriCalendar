package il.co.yshahak.ivricalendar.uihelpers;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by yshahak on 07/10/2016.
 */

public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };

//    public static final int GRID = 2;

    private Drawable mDivider;


    public DividerItemDecoration(Context context) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = setTint(a.getDrawable(0), Color.BLACK);
        a.recycle();
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawVertical(c, parent);
        drawHorizontal(c, parent);
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        if (parent.getChildCount() == 0) return;

        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final View child = parent.getChildAt(0);
        if (child.getHeight() == 0) return;

        final RecyclerView.LayoutParams params =
                (RecyclerView.LayoutParams) child.getLayoutParams();
        int top = child.getBottom() + params.bottomMargin + mDivider.getIntrinsicHeight();
        int bottom = top + mDivider.getIntrinsicHeight();
        final int parentBottom = parent.getHeight() - parent.getPaddingBottom();

        mDivider.setBounds(left, parent.getPaddingTop(), right, parent.getPaddingTop() + mDivider.getIntrinsicHeight());
        mDivider.draw(c);
        do {
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);

            top += mDivider.getIntrinsicHeight() + params.topMargin + child.getHeight() + params.bottomMargin + mDivider.getIntrinsicHeight();
            bottom = top + mDivider.getIntrinsicHeight();
        }while (bottom < parentBottom);
        mDivider.setBounds(left, parentBottom - mDivider.getIntrinsicHeight(), right, parentBottom);
        mDivider.draw(c);
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();
//        mDivider.setBounds(0, top, parent.getWidth(), bottom);
//        MyLog.d("height=" + (bottom - top));
//        MyLog.d("width=" + (parent.getWidth()));
//        mDivider.draw(c);
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params =
                    (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + params.rightMargin + mDivider.getIntrinsicHeight();
            final int right = left + mDivider.getIntrinsicWidth();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, mDivider.getIntrinsicWidth(), mDivider.getIntrinsicHeight());
    }

    public Drawable setTint(Drawable d, int color) {
        Drawable wrappedDrawable = DrawableCompat.wrap(d);
        DrawableCompat.setTint(wrappedDrawable, color);
        return wrappedDrawable;
    }
}
