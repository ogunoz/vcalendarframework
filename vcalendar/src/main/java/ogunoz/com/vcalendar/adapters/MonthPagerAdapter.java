package ogunoz.com.vcalendar.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;

import ogunoz.com.vcalendar.DayListener;
import ogunoz.com.vcalendar.R;
import ogunoz.com.vcalendar.VCalendar;
import ogunoz.com.vcalendar.customview.ExpandableLayout;
import ogunoz.com.vcalendar.customview.MinHeightButton;
import ogunoz.com.vcalendar.models.Day;
import ogunoz.com.vcalendar.models.Event;
import ogunoz.com.vcalendar.models.HolidayEvent;
import ogunoz.com.vcalendar.models.Month;
import ogunoz.com.vcalendar.util.CalendarLogic;
import ogunoz.com.vcalendar.util.CalendarView;
import ogunoz.com.vcalendar.util.DeviceScreenUtil;
import ogunoz.com.vcalendar.util.DifferentColorCircularBorder;

public class MonthPagerAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<Month> monthList;
    private MonthPagerAdapterListener listener;
    private RelativeLayout previousDayLayout;
    private ExpandableLayout previousWeekLayout;
    private LinearLayout previousEventLayout;
    private DifferentColorCircularBorder border;
    private boolean dayExpandable;
    private int extraContentColor;
    private boolean isTodaySelected = false;
    private int extraContentHeight = 0;
    private SparseArray<SparseArray<Month>> monthMap;


    public MonthPagerAdapter(Context context, ArrayList<Month> monthList, boolean dayExpandable,
                             MonthPagerAdapterListener listener, SparseArray<SparseArray<Month>> monthMap) {
        this.context = context;
        this.monthList = monthList;
        this.dayExpandable = dayExpandable;
        this.listener = listener;
        border = new DifferentColorCircularBorder(context);
        this.monthMap = monthMap;

        CalendarView calendarView = CalendarView.getCalendarViewInstance();
        View extraContentView = View.inflate(context, calendarView.getExtraViewID(), null);
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
        final View v = inflater.inflate(R.layout.calendar_pager_item, container, false);
        v.setTag(position + "");
        LinearLayout calendarLayout = (LinearLayout) v.findViewById(R.id.calendar_layout);
        calendarLayout.setTag("CalendarLayout");
        createCalendar(calendarLayout, position);

        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        CalendarView calendarView = CalendarView.getCalendarViewInstance();
        calendarView.getCalendarHeightList().put(position, v.getMeasuredHeight());

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

    private void createCalendar(final LinearLayout calendarLayout, final int position) {
        final Month month = monthList.get(position);
        final CalendarLogic calendarLogic = CalendarLogic.getCalendarLogicInstance();
        final CalendarView calendarView = CalendarView.getCalendarViewInstance();

        int i = 0;
        while (7 * i + 1 - month.getFirstDayOfMonth() <= month.getLastDayOfMonth()) {
            final View extraContentView = View.inflate(context, calendarView.getExtraViewID(), null);

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

                Drawable unreadIcon = calendarView.getDayUnreadIcon();
                if (unreadIcon != null) {
                    unreadView.setImageDrawable(unreadIcon);
                    int unreadIconSize = DeviceScreenUtil.dpToPx(context, 12);
                    RelativeLayout.LayoutParams unreadIconParam = new RelativeLayout.LayoutParams(unreadIconSize, unreadIconSize);
                    unreadView.setLayoutParams(unreadIconParam);
                    unreadIconParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    unreadIconParam.addRule(RelativeLayout.CENTER_HORIZONTAL);

                    //  unreadIconParam.setMargins(0, unreadIconSize / 3, 0, 0);
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

                    Day dayObject = monthMap.get(month.getYear()).
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
                    eventsLayoutParam.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.day_button_id);
                    eventsLayoutParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    // eventsLayoutParam.addRule(RelativeLayout.CENTER_IN_PARENT);
                    eventLayout.setLayoutParams(eventsLayoutParam);
                    int a = DeviceScreenUtil.dpToPx(context, 5);
                    eventLayout.setPadding(0, 0, 0, a);

                    RelativeLayout.LayoutParams dayButtonParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    dayButtonParam.addRule(RelativeLayout.CENTER_IN_PARENT);
                    dayButton.setLayoutParams(dayButtonParam);
                    dayButton.setGravity(Gravity.CENTER);


                    dayButton.measure(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    int heightButton = dayButton.getMeasuredHeight();
                    int widthButton = dayButton.getMeasuredWidth();

                    final int min = heightButton < widthButton ? heightButton : widthButton;
                    ImageView selectionView = new ImageView(context);
                    selectionView.setId(R.id.selection_id);
                    selectionView.setImageResource(R.drawable.today_button_selector);
                    selectionView.setColorFilter(calendarView.getDaySelectionColor(), PorterDuff.Mode.SRC);

                    RelativeLayout.LayoutParams selectionViewParams = new RelativeLayout.LayoutParams(min * 2 / 3, min * 2 / 3);
                    selectionView.setLayoutParams(selectionViewParams);
                    selectionViewParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                    dayLayout.addView(selectionView);
                    selectionView.setVisibility(View.GONE);

                    if (day == month.getToday()) {
                        dayButton.setTypeface(null, Typeface.BOLD);
                    } else {
                        dayButton.setTypeface(null, Typeface.NORMAL);
                    }


                    final ArrayList<Event> finalEventList = eventList;
                    dayButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            calendarLogic.setLastClickedMonthPosition(position);
                            calendarLogic.setLastClickedDayIndex(day);
                            onDayClick(dayLayout, finalEventList, day, month);
                        }
                    });

                    if (dayExpandable) {
                        dayButton.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                DayListener dayListener = VCalendar.getDayListener();
                                if (dayListener != null) {
                                    dayListener.onDayLongClick(day, month.getMonthIndex(), month.getYear());
                                }
                                unreadView.setVisibility(View.GONE);

                                if (previousDayLayout != null && previousDayLayout != dayLayout) {
                                    ImageView previousArrow = (ImageView) previousDayLayout.findViewById(R.id.selection_arrow_id);
                                    previousDayLayout.removeView(previousArrow);
                                }
                                if (previousWeekLayout != null && previousWeekLayout != weekLayout && previousWeekLayout.isExpanded()) {
                                    previousWeekLayout.collapse(context.getResources().getInteger(R.integer.animationDuration));
                                }

                                if (!weekLayout.isExpanded()) {
                                    weekLayout.expand(context.getResources().getInteger(R.integer.animationDuration));
                                    calendarView.getCalendarHeightList().put(-1, extraContentHeight);
                                }
                                clickDayLayout(dayLayout, finalEventList, day, month, false);
                                ImageView currentSelectionArrow = (ImageView) dayLayout.findViewById(R.id.selection_arrow_id);
                                if (currentSelectionArrow == null) {

                                    ImageView selectionArrow = new ImageView(context);
                                    selectionArrow.setId(R.id.selection_arrow_id);
                                    selectionArrow.setBackgroundResource(R.drawable.arrow_up_week_layout);
                                    selectionArrow.getBackground().setColorFilter(extraContentColor, PorterDuff.Mode.SRC);

                                    RelativeLayout.LayoutParams selectionArrowParam = new RelativeLayout.LayoutParams(min / 2, min / 2);
                                    selectionArrowParam.addRule(RelativeLayout.ALIGN_BOTTOM, dayButton.getId());
                                    selectionArrowParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
                                    selectionArrow.setLayoutParams(selectionArrowParam);

                                    dayLayout.addView(selectionArrow);
                                }

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
                dayButton.setTextSize(13);

                if (calendarLogic.isDisableHolidaysAndWeekends()) {
                    int weekEndOne = 5;
                    int weekEndTwo = 6;
                    if (calendarLogic.getCalendarType() == context.getResources().getInteger(R.integer.american)) {
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

    private void clickDayLayout(RelativeLayout dayLayout, ArrayList<Event> eventList,
                                int day, Month month, boolean onDayClicked) {
        LinearLayout eventLayout = null;
        if (dayLayout != null) {
            DayListener dayListener = VCalendar.getDayListener();
            if (onDayClicked && dayListener != null) {
                dayListener.onDayClick(day, month.getMonthIndex(), month.getYear());
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
                    border.showBorderPortions(counter, dayLayout, context.getResources().
                            getInteger(R.integer.fastAnimationDuration));
                } else {
                    selectButton(dayLayout);
                }
            } else {
                selectButton(dayLayout);
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            dayLayout.setLayoutParams(params);
        }


        listener.onDayClick(day - 1, month.getMonthIndex(), month.getYear());
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
        if (dayExpandable) {
            if (previousDayLayout != null && previousDayLayout != dayLayout && previousWeekLayout != null &&
                    previousWeekLayout.isExpanded()) {
                CalendarView calendarView = CalendarView.getCalendarViewInstance();
                ImageView previousArrow = (ImageView) previousDayLayout.findViewById(R.id.selection_arrow_id);
                previousDayLayout.removeView(previousArrow);

                previousWeekLayout.collapse(context.getResources().getInteger(R.integer.animationDuration));
                calendarView.getCalendarHeightList().put(-1, 0);
            }
        }
        clickDayLayout(dayLayout, eventList, day, month, true);
    }

    public void onDayClick(View view, ArrayList<Event> eventList,
                           int day, Month month) {
        CalendarView calendarView = CalendarView.getCalendarViewInstance();
        RelativeLayout dayLayout = calendarView.getDayLayout(view, day, month);
        onDayClick(dayLayout, eventList, day, month);
    }

    public interface MonthPagerAdapterListener {
        void onDayClick(int dayIndex, int monthIndex, int yearIndex);
    }

}
