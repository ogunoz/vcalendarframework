package ogunoz.com.vcalendar.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by Ogün Öz on 21/11/16.
 */

public class MinHeightButton extends Button {

    public MinHeightButton(Context context) {
        super(context);
    }

    public MinHeightButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MinHeightButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMinHeight(0);
        setMinimumHeight(0);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
    }
}
