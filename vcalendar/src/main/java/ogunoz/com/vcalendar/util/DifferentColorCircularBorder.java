package ogunoz.com.vcalendar.util;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;

import ogunoz.com.vcalendar.R;

/**
 * Created by ogun on 13/11/16.
 */

public class DifferentColorCircularBorder {

    private Context context;
    private HashMap<RelativeLayout, ArrayList<ArrayList<ProgressBar>>> progressListMap;

    public DifferentColorCircularBorder(Context context) {
        this.context = context;
        progressListMap = new HashMap<>();
    }

    public void addBorderPortion(RelativeLayout parentLayout, int color, int startDegree) {
        final ProgressBar portion = getBorderPortion(parentLayout, color, startDegree);
        if (progressListMap.get(parentLayout) == null) {
            ArrayList<ArrayList<ProgressBar>> progressListList = new ArrayList<>();
            progressListList.add(new ArrayList<ProgressBar>());
            progressListList.add(new ArrayList<ProgressBar>());
            progressListMap.put(parentLayout, progressListList);
        }
        progressListMap.get(parentLayout).get(0).add(portion);
        progressListMap.get(parentLayout).get(1).add(portion);
        parentLayout.addView(portion);
        portion.setProgress(0);
    }

    public void showBorderPortions(final int eventNum, final RelativeLayout parentLayout, final int duration) {

        if (progressListMap.get(parentLayout).get(1).size() > 0) {
            final ProgressBar portion = progressListMap.get(parentLayout).get(1).get(0);
            ValueAnimator anim = ValueAnimator.ofInt(0, 360 / eventNum);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    portion.setProgress(val);
                }
            });
            anim.setDuration(duration / eventNum);
            anim.start();
            anim.setInterpolator(new LinearInterpolator());
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {

                    if (progressListMap.get(parentLayout).get(1).size() > 0) {
                        progressListMap.get(parentLayout).get(1).remove(0);
                        showBorderPortions(eventNum, parentLayout, duration);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
        }
    }


    public void removeBorders(RelativeLayout parentLayout) {

        if (progressListMap.get(parentLayout) != null && progressListMap.get(parentLayout).get(0) != null) {
            for (int i = 0; i < progressListMap.get(parentLayout).get(0).size(); i++) {
                parentLayout.removeView(progressListMap.get(parentLayout).get(0).get(i));
            }
            progressListMap.get(parentLayout).get(0).clear();
            progressListMap.get(parentLayout).get(1).clear();
        }
    }

    private ProgressBar getBorderPortion(RelativeLayout parentLayout, int color, int startDegree) {
        LayoutInflater inflater = LayoutInflater.from(context);

        ProgressBar portion = (ProgressBar) inflater.inflate(R.layout.border_portion, parentLayout, false);
        portion.setRotation(startDegree - 90);

        portion.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) portion.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        portion.setLayoutParams(params);

        return portion;
    }

}