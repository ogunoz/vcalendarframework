package ogunoz.com.vcalendar.customview;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by Ogün Öz on 29/11/16.
 */

public class DraggableListView extends ListView {

    private float diffX = 0, diffY = 0;
    private float lastX = 0, lastY = 0;

    public DraggableListView(Context context) {
        super(context, null);
    }

    public DraggableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DraggableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (MotionEventCompat.getActionMasked(event)) {
            case MotionEvent.ACTION_DOWN:
                diffX = 0;
                diffY = 0;
                lastX = event.getX();
                lastY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                diffX += Math.abs(event.getX() - lastX);
                diffY += Math.abs(event.getY() - lastY);
                lastX = event.getX();
                lastY = event.getY();
                if (diffX > diffY)
                    return false;
        }
        return super.onInterceptTouchEvent(event);
    }
}
