package ogunoz.com.vcalendar.customview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by ogun on 12/11/16.
 */

public class UnscrollableViewPager extends ViewPager {

    public UnscrollableViewPager(Context context) {
        super(context);
    }

    public UnscrollableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        if(event == null) {
            return false;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        if(event == null) {
            return false;
        }
        return super.onTouchEvent(event);
    }

}
