package ogunoz.com.vcalendar.customview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

/**
 * Created by Ogün Öz on 21/11/16.
 */

public class ExpandableLayout extends LinearLayout {

    private boolean isExpanded = false;
    private int targetHeight = 0;

    private LinearLayout contentLayout;

    public ExpandableLayout(Context context) {
        super(context);
    }

    public ExpandableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void addHeaderView(LinearLayout layout) {
        addView(layout);
    }

    public void addContentView(LinearLayout layout) {
        this.contentLayout = layout;
        layout.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        targetHeight = layout.getMeasuredHeight();
        addView(layout);
        layout.getLayoutParams().height = 0;
    }

    public boolean isExpanded() {
        return this.isExpanded;
    }


    public void expand() {
        isExpanded = true;

        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, targetHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                contentLayout.getLayoutParams().height = (int) animation.getAnimatedValue();
                contentLayout.requestLayout();
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(500);
        valueAnimator.start();
    }

    public void collapse() {
        isExpanded = false;
        ValueAnimator valueAnimator = ValueAnimator.ofInt(targetHeight, 0);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                contentLayout.getLayoutParams().height = (int) animation.getAnimatedValue();
                contentLayout.requestLayout();
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(500);
        valueAnimator.start();
    }
}
