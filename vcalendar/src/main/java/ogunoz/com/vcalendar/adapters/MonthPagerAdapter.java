package ogunoz.com.vcalendar.adapters;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import ogunoz.com.vcalendar.DayListener;
import ogunoz.com.vcalendar.R;
import ogunoz.com.vcalendar.customview.ExpandableLayout;
import ogunoz.com.vcalendar.customview.MinHeightButton;
import ogunoz.com.vcalendar.models.Day;
import ogunoz.com.vcalendar.models.Event;
import ogunoz.com.vcalendar.models.HolidayEvent;
import ogunoz.com.vcalendar.models.Month;
import ogunoz.com.vcalendar.CalendarUtil;
import ogunoz.com.vcalendar.util.DeviceScreenUtil;
import ogunoz.com.vcalendar.util.DifferentColorCircularBorder;

public class MonthPagerAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<Month> monthList;
    private MonthPagerAdapterListener listener;
    private DayListener dayListener;
    private RelativeLayout previousDayLayout;
    private ExpandableLayout previousWeekLayout;
    private LinearLayout previousEventLayout;
    private DifferentColorCircularBorder border;
    private boolean dayExpandable;
    private int extraContentColor;
    private boolean isTodaySelected = false;
    private ViewPager calendarPager;
    private int maximumHeight = 0;
    private int extraContentHeight = 0;


    public MonthPagerAdapter(Context context, ArrayList<Month> monthList, boolean dayExpandable,
                             MonthPagerAdapterListener listener, ViewPager calendarPager) {
        this.context = context;
        this.monthList = monthList;
        this.dayExpandable = dayExpandable;
        this.listener = listener;
        this.dayListener = CalendarUtil.getDayListener();
        border = new DifferentColorCircularBorder(context);
        this.calendarPager = calendarPager;

        View extraContentView = View.inflate(context, CalendarUtil.getExtraViewID(), null);
        extraContentView.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        extraContentHeight = extraContentView.getMeasuredHeight();

        int color = Color.TRANSPARENT;
        Drawable background = extraContentView.getBackground();
        if (background instanceof ColorDrawable)
            color = ((ColorDrawable) background).getColor();

        extraContentColor = color;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View v = inflater.inflate(R.layout.calendar_pager_item, container, false);
        v.setTag(position + "");
        LinearLayout calendarLayout = (LinearLayout) v.findViewById(R.id.calendar_layout);
        calendarLayout.setTag("CalendarLayout");
        createCalendar(calendarLayout, position);

        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if (v.getMeasuredHeight() > maximumHeight) {
            maximumHeight = v.getMeasuredHeight();
            calendarPager.getLayoutParams().height = maximumHeight;
        }

        container.addView(v);

        return v;
    }

    @Override
    public int getCount() {
        return monthList.size();
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    private void createCalendar(LinearLayout calendarLayout, final int position) {
        final Month month = monthList.get(position);

        int i = 0;
        while (7 * i + 1 - month.getFirstDayOfMonth() <= month.getLastDayOfMonth()) {
            View extraContentView = View.inflate(context, CalendarUtil.getExtraViewID(), null);

            final LinearLayout weekContentLayout = ((LinearLayout) extraContentView);
            weekContentLayout.setTag("WeekLayoutContent " + i);

            final ExpandableLayout weekLayout = new ExpandableLayout(context);
            weekLayout.setTag("WeekLayout " + i);
            weekLayout.setOrientation(LinearLayout.VERTICAL);

            final LinearLayout weekLayoutHeader = new LinearLayout(context);
            weekLayoutHeader.setTag("WeekLayoutHeader " + i);
            weekLayoutHeader.setOrientation(LinearLayout.HORIZONTAL);
            weekLayoutHeader.setWeightSum(7);

            for (int j = 0; j < 7; j++) {
                final int day = 7 * i + j + 1 - month.getFirstDayOfMonth();
                final RelativeLayout dayLayout = new RelativeLayout(context);
                dayLayout.setTag("DayLayout " + day);
                final LinearLayout eventLayout = new LinearLayout(context);

                final ImageView unreadView = new ImageView(context);
                unreadView.setId(R.id.unread_image_id);

                Drawable unreadIcon = CalendarUtil.getDayUnreadIcon();
                if (unreadIcon != null) {
                    unreadView.setImageDrawable(unreadIcon);
                    int unreadIconSize = DeviceScreenUtil.dpToPx(context, 10);
                    RelativeLayout.LayoutParams unreadIconParam = new RelativeLayout.LayoutParams(unreadIconSize, unreadIconSize);
                    unreadView.setLayoutParams(unreadIconParam);
                    unreadIconParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    unreadIconParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    unreadIconParam.setMargins(0, unreadIconSize / 2, 0, 0);
                }
                unreadView.setVisibility(View.GONE);
                dayLayout.addView(unreadView);

                eventLayout.setId(R.id.event_layout_id);
                eventLayout.setOrientation(LinearLayout.HORIZONTAL);

                final MinHeightButton dayButton = new MinHeightButton(context);
                dayButton.setId(R.id.day_button_id);

                ArrayList<Event> eventList = new ArrayList<>();
                if (day >= 1 && day <= month.getLastDayOfMonth()) {
                    if (!month.getDay(day - 1).isRead()) {
                        unreadView.setVisibility(View.VISIBLE);
                    }

                    Day dayObject = CalendarUtil.getMonthMap().get(month.getYear()).
                            get(month.getMonthIndex()).getEventMap().get(day - 1);
                    eventList = dayObject.getEventList();
                    if (eventList != null) {
                        for (int k = 0; k < eventList.size(); k++) {
                            Event event = eventList.get(k);
                            if (event instanceof HolidayEvent) {
                                dayButton.setAlpha(0.5f);
                            } else {
                                ImageView eventView = new ImageView(context);
                                eventView.setBackgroundResource(R.drawable.today_button_selector);
                                eventView.getBackground().setColorFilter(event.getColor(), PorterDuff.Mode.SRC);
                                int size = DeviceScreenUtil.dpToPx(context, 5);
                                LinearLayout.LayoutParams eventPointLayoutParam = new LinearLayout.LayoutParams(size, size);
                                if (eventLayout.getChildCount() != 0) {
                                    eventPointLayoutParam.setMargins(size / 2, 0, 0, 0);
                                }
                                eventView.setLayoutParams(eventPointLayoutParam);
                                eventLayout.addView(eventView);
                            }
                        }
                    }


                    RelativeLayout.LayoutParams eventsLayoutParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    // eventsLayoutParam.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.day_button_id);
                    eventsLayoutParam.addRule(RelativeLayout.CENTER_IN_PARENT);
                    eventLayout.setLayoutParams(eventsLayoutParam);
                    int a = DeviceScreenUtil.dpToPx(context, 14);
                    eventLayout.setPadding(0, 2 * a, 0, 0);

                    RelativeLayout.LayoutParams dayButtonParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    dayButton.setLayoutParams(dayButtonParam);
                    dayButton.setGravity(Gravity.CENTER);

                    dayButton.measure(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    int heightButton = dayButton.getMeasuredHeight();
                    int weightButton = dayButton.getMeasuredWidth();

                    final int min = heightButton < weightButton ? heightButton : weightButton;
                    ImageView selectionView = new ImageView(context);
                    selectionView.setId(R.id.selection_id);
                    selectionView.setImageResource(R.drawable.today_button_selector);
                    selectionView.setColorFilter(CalendarUtil.getDaySelectionColor(), PorterDuff.Mode.SRC);

                    RelativeLayout.LayoutParams selectionViewParams = new RelativeLayout.LayoutParams(min * 2 / 3, min * 2 / 3);
                    selectionView.setLayoutParams(selectionViewParams);
                    selectionViewParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                    dayLayout.addView(selectionView);
                    selectionView.setVisibility(View.GONE);

                    if (day == month.getToday()) {
                        dayButton.setTypeface(null, Typeface.BOLD);
                    }


                    final ArrayList<Event> finalEventList = eventList;
                    dayButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CalendarUtil.setLastClickedMonthPosition(position);
                            CalendarUtil.setLastClickedDayIndex(day);
                            onDayClick(dayLayout, finalEventList, day, month);
                        }
                    });

                    if (dayExpandable) {
                        dayButton.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                if (dayListener != null) {
                                    dayListener.onDayLongClick();
                                }
                                unreadView.setVisibility(View.GONE);

                                boolean previousExpanded = false;
                                if (previousDayLayout != null && previousDayLayout != dayLayout) {
                                    ImageView previousArrow = (ImageView) previousDayLayout.findViewById(R.id.selection_arrow_id);
                                    previousDayLayout.removeView(previousArrow);
                                }
                                if (previousWeekLayout != null && previousWeekLayout != weekLayout && previousWeekLayout.isExpanded()) {
                                    previousWeekLayout.collapse();
                                    previousExpanded = true;
                                }
                                int extraHeight = 0;
                                if (!previousExpanded) {
                                    extraHeight = extraContentHeight;
                                }
                                if (!weekLayout.isExpanded()) {
                                    weekLayout.expand();
/*
                                    int initial = calendarPager.getLayoutParams().height;
                                    int target = initial + extraHeight;

                                    ValueAnimator va = ValueAnimator.ofFloat(initial, target);
                                    va.setDuration(400);
                                    va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                        public void onAnimationUpdate(ValueAnimator animation) {
                                            System.out.println((float) animation.getAnimatedValue());
                                         //   calendarPager.getLayoutParams().height = (int) animation.getAnimatedValue();
                                         //   System.out.println((int) animation.getAnimatedValue());
                                          //  calendarPager.requestLayout();
                                        }
                                    });
                                    va.setInterpolator(new LinearInterpolator());

                                    va.start();
*/


                                 //   calendarPager.getLayoutParams().height = calendarPager.getLayoutParams().height + extraHeight;
                                }


                                if (dayLayout != previousDayLayout) {
                                    ImageView selectionArrow = new ImageView(context);
                                    selectionArrow.setId(R.id.selection_arrow_id);
                                    selectionArrow.setBackgroundResource(R.drawable.arrow_up);
                                    selectionArrow.getBackground().setColorFilter(extraContentColor, PorterDuff.Mode.SRC);

                                    RelativeLayout.LayoutParams selectionArrowParam = new RelativeLayout.LayoutParams(min / 2, min / 2);
                                    selectionArrowParam.addRule(RelativeLayout.ALIGN_BOTTOM, dayButton.getId());
                                    selectionArrowParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
                                    selectionArrow.setLayoutParams(selectionArrowParam);

                                    dayLayout.addView(selectionArrow);
                                }

                                clickDayLayout(dayLayout, finalEventList, extraHeight, day, month);

                                previousWeekLayout = weekLayout;
                                return true;
                            }
                        });
                    }

                }
                if (day > month.getLastDayOfMonth() || day < 1) {
                    dayLayout.setVisibility(View.INVISIBLE);
                }
                dayButton.setBackgroundResource(0);
                String dayLabel = day + "";
                dayButton.setText(dayLabel);
                dayButton.setTextSize(14);

                if (CalendarUtil.isDisableHolidaysAndWeekends()) {
                    int weekEndOne = 5;
                    int weekEndTwo = 6;
                    if (CalendarUtil.getCalendarType() == context.getResources().getInteger(R.integer.american)) {
                        weekEndOne = 0;
                    }
                    if (j == weekEndOne || j == weekEndTwo) { // weekends
                        dayButton.setAlpha(0.5f);
                    }
                }


                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 1);

                dayLayout.setLayoutParams(params);
                dayLayout.addView(dayButton);
                dayLayout.addView(eventLayout);
                weekLayoutHeader.addView(dayLayout);

                if (day == month.getToday()) {
                    if (!isTodaySelected) {
                        onDayClick(dayLayout, eventList, day, month);
                        isTodaySelected = true;
                    }
                }

            }

            weekLayout.addHeaderView(weekLayoutHeader);
            weekLayout.addContentView(weekContentLayout);
            calendarLayout.addView(weekLayout);
            i++;
        }
    }

    public void clickDayLayout(RelativeLayout dayLayout, ArrayList<Event> eventList, int extraHeight,
                               int day, Month month) {
        LinearLayout eventLayout = null;
        if (dayLayout != null) {

            if (dayListener != null) {
                dayListener.onDayClick();
            }

            ImageView unreadView = (ImageView) dayLayout.findViewById(R.id.unread_image_id);
            unreadView.setVisibility(View.GONE);
            eventLayout = (LinearLayout) dayLayout.findViewById(R.id.event_layout_id);
        }

        if (previousDayLayout != null && previousDayLayout != dayLayout) {
            MinHeightButton b = (MinHeightButton) previousDayLayout.findViewById(R.id.day_button_id);

            ImageView previousSelectionView = (ImageView) previousDayLayout.findViewById(R.id.selection_id);
            previousSelectionView.setVisibility(View.GONE);

            b.setTextColor(ContextCompat.getColor(context, R.color.black));
            LinearLayout l = (LinearLayout) previousDayLayout.findViewById(R.id.event_layout_id);
            l.setVisibility(View.VISIBLE);

            if (previousEventLayout != null) {

                border.removeBorders(previousDayLayout);
            }
        }
        if (dayLayout != null && dayLayout != previousDayLayout) {
            if (eventList != null) {
                int eventNumber = eventLayout.getChildCount();
                if (eventNumber > 0) {
                    int counter = 0;
                    for (Event e : eventList) {
                        if (!(e instanceof HolidayEvent)) {
                            border.addBorderPortion(dayLayout, e.getColor(), counter * 360 / eventNumber);
                            counter++;
                        }
                    }
                    border.showBorderPortions(counter, dayLayout);
                } else {
                    selectButton(dayLayout);
                }
            } else {
                selectButton(dayLayout);
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            dayLayout.setLayoutParams(params);
        }


        listener.onDayClick(extraHeight, day - 1, month.getMonthIndex(), month.getYear());
        if (eventLayout != null) {
            eventLayout.setVisibility(View.INVISIBLE);
        }
        previousDayLayout = dayLayout;
        previousEventLayout = eventLayout;
    }

    private void selectButton(RelativeLayout dayLayout) {
        ImageView selectionView = (ImageView) dayLayout.findViewById(R.id.selection_id);
        MinHeightButton dayButton = (MinHeightButton) dayLayout.findViewById(R.id.day_button_id);
        selectionView.setVisibility(View.VISIBLE);
        dayButton.setTextColor(ContextCompat.getColor(context, R.color.white));
    }

    private void onDayClick(RelativeLayout dayLayout, ArrayList<Event> eventList, int day, Month month) {

        int extraHeight = 0;
        if (dayExpandable) {
            if (previousDayLayout != null && previousDayLayout != dayLayout && previousWeekLayout != null &&
                    previousWeekLayout.isExpanded()) {
                ImageView previousArrow = (ImageView) previousDayLayout.findViewById(R.id.selection_arrow_id);
                previousDayLayout.removeView(previousArrow);

                previousWeekLayout.collapse();

                extraHeight = extraContentHeight;
              //  calendarPager.getLayoutParams().height = calendarPager.getLayoutParams().height - extraHeight;

            }
        }
        clickDayLayout(dayLayout, eventList, -extraHeight, day, month);
    }

    public void onDayClick(View view, ArrayList<Event> eventList,
                           int day, Month month) {

        RelativeLayout dayLayout = getDayLayout(view, day, month);

        int extraHeight = 0;
        if (dayExpandable) {
            if (previousDayLayout != null && previousDayLayout != dayLayout && previousWeekLayout != null &&
                    previousWeekLayout.isExpanded()) {
                ImageView previousArrow = (ImageView) previousDayLayout.findViewById(R.id.selection_arrow_id);
                previousDayLayout.removeView(previousArrow);

                previousWeekLayout.collapse();
                extraHeight = extraContentHeight;
              //  calendarPager.getLayoutParams().height = calendarPager.getLayoutParams().height - extraHeight;
            }
        }
        clickDayLayout(dayLayout, eventList, -extraHeight, day, month);


    }

    public RelativeLayout getDayLayout(View view, int day, Month month) {
        int weekIndex = 0;
        if (day != 1) {
            int indexOfDay = month.getFirstDayOfMonth() + day;
            weekIndex = indexOfDay / 7;
        }
        RelativeLayout dayLayout = null;
        if (day > 0) {
            LinearLayout calendarLayout = (LinearLayout) view.findViewWithTag("CalendarLayout");
            ExpandableLayout weekLayout = (ExpandableLayout) calendarLayout.findViewWithTag("WeekLayout " + weekIndex);
            LinearLayout weekLayoutHeader = (LinearLayout) weekLayout.findViewWithTag("WeekLayoutHeader " + weekIndex);
            dayLayout = (RelativeLayout) weekLayoutHeader.findViewWithTag("DayLayout " + day);
        }
        return dayLayout;
    }

    public interface MonthPagerAdapterListener {
        void onDayClick(int extraHeight, int dayIndex, int monthIndex, int yearIndex);
    }

}
