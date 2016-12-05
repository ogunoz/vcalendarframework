package ogunoz.com.vcalendar;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import ogunoz.com.vcalendar.adapters.EventAdapter;
import ogunoz.com.vcalendar.adapters.HeaderPagerAdapter;
import ogunoz.com.vcalendar.adapters.MonthPagerAdapter;
import ogunoz.com.vcalendar.customview.DraggableListView;
import ogunoz.com.vcalendar.customview.ExpandableLayout;
import ogunoz.com.vcalendar.models.Day;
import ogunoz.com.vcalendar.models.Event;
import ogunoz.com.vcalendar.models.Month;
import ogunoz.com.vcalendar.util.CalendarUtil;


/**
 * Created by Ogün Öz on 21/11/16.
 */

public class CalendarLayout extends LinearLayout implements MonthPagerAdapter.MonthPagerAdapterListener {

    private ViewPager calendarPager;
    private ViewPager monthNamePager;
    private DraggableListView eventListView;
    private ArrayList<Event> events;

    private boolean disableEventList = false;
    private boolean isCalendarPagerMaster = true;
    private int previousPosition = CalendarUtil.getCurrentMonthIndex();


    public CalendarLayout(Context context) {
        super(context);
    }

    public CalendarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CalendarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(final Context context, AttributeSet attrs) {
        View v = View.inflate(context, R.layout.calendar_layout, this);

        if (isInEditMode())
            return;

        final Calendar calendar = Calendar.getInstance();

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CalendarLayout);

        int startYear = a.getInteger(R.styleable.CalendarLayout_startYear, calendar.get(Calendar.YEAR));
        String startMonth = a.getString(R.styleable.CalendarLayout_startMonth);
        int endYear = a.getInteger(R.styleable.CalendarLayout_endYear, calendar.get(Calendar.YEAR));
        String endMonth = a.getString(R.styleable.CalendarLayout_endMonth);
        boolean disableHolidaysAndWeekends = a.getBoolean(R.styleable.CalendarLayout_disableHolidaysAndWeekends, true);
        int calendarType = a.getInt(R.styleable.CalendarLayout_calendarType, getResources().getInteger(R.integer.european));
        int calendarLanguage = a.getInt(R.styleable.CalendarLayout_calendarLanguage, getResources().getInteger(R.integer.english));
        final int extraContentID = a.getResourceId(R.styleable.CalendarLayout_extraLayout, -1);
        disableEventList = a.getBoolean(R.styleable.CalendarLayout_disableEventList, false);
        Drawable dayUnreadIcon = a.getDrawable(R.styleable.CalendarLayout_dayUnreadIcon);
        final boolean rememberLastSelectedDay = a.getBoolean(R.styleable.CalendarLayout_rememberLastSelectedDay, false);

        // TODO get list divider default color as default value
        int listDividerColor = a.getColor(R.styleable.CalendarLayout_eventListDividerColor, Color.GRAY);
        int dividerHeight = a.getDimensionPixelSize(R.styleable.CalendarLayout_eventListDividerHeight, -1);
        int daySelectionColor = a.getColor(R.styleable.CalendarLayout_daySelectionColor, ContextCompat.getColor(context,
                R.color.day_selection_color));

        monthNamePager = (ViewPager) v.findViewById(R.id.month_name_pager);
        calendarPager = (ViewPager) v.findViewById(R.id.calendar_pager);
        eventListView = (DraggableListView) v.findViewById(R.id.event_list_view);

        monthNamePager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                isCalendarPagerMaster = false;
                return false;
            }
        });
        calendarPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                isCalendarPagerMaster = true;
                return false;
            }
        });


        CalendarUtil.setEventListView(eventListView);
        final TextView yearText = (TextView) v.findViewById(R.id.year_text);


        if (listDividerColor != Color.GRAY) {
            eventListView.getDivider().setColorFilter(listDividerColor, PorterDuff.Mode.SRC);
        }
        if (dividerHeight != -1) {
            eventListView.setDividerHeight(dividerHeight);
        }


        boolean dayExpandable = false;
        if (extraContentID != -1) {
            dayExpandable = true;
        }
        CalendarUtil.setDayUnreadIcon(dayUnreadIcon);
        CalendarUtil.setLanguage(setCalendarLanguage(calendarLanguage));
        CalendarUtil.setDaySelectionColor(daySelectionColor);
        CalendarUtil.setExtraViewID(extraContentID);
        CalendarUtil.setDisableHolidaysAndWeekends(disableHolidaysAndWeekends);
        CalendarUtil.setCalendarType(calendarType);

        if (TextUtils.isEmpty(startMonth)) {
            startMonth = "Jan";
        }
        if (TextUtils.isEmpty(endMonth)) {
            endMonth = "Dec";
        }

        if (startYear < calendar.getMinimum(Calendar.YEAR)) {
            startYear = calendar.getMinimum(Calendar.YEAR);
        } else if (startYear > calendar.get(Calendar.YEAR)) {
            startYear = calendar.get(Calendar.YEAR);
        }
        if (endYear > calendar.getMaximum(Calendar.YEAR)) {
            endYear = calendar.getMaximum(Calendar.YEAR);
        } else if (endYear < calendar.get(Calendar.YEAR)) {
            endYear = calendar.get(Calendar.YEAR);
        }

        SimpleDateFormat format = new SimpleDateFormat("MMM", Locale.US);
        try {
            format.parse(startMonth);
        } catch (ParseException e) {
            startMonth = "Jan";
        }
        try {
            format.parse(endMonth);
        } catch (ParseException e) {
            endMonth = "Dec";
        }

        int currentMonth = calendar.get(Calendar.MONTH);

        if (startYear == calendar.get(Calendar.YEAR)) {
            try {
                calendar.setTime(new SimpleDateFormat("MMM", Locale.US).parse(startMonth));
                int startMonthInt = calendar.get(Calendar.MONTH);
                calendar.setTime(new Date());
                if (startMonthInt > currentMonth) {
                    startMonth = "Jan";
                }
            } catch (ParseException e) {
                startMonth = "Jan";
            }
        }
        if (endYear == calendar.get(Calendar.YEAR)) {
            try {
                calendar.setTime(new SimpleDateFormat("MMM", Locale.US).parse(endMonth));
                int endMonthInt = calendar.get(Calendar.MONTH);

                calendar.setTime(new Date());
                if (endMonthInt < currentMonth) {
                    endMonth = "Dec";
                }
            } catch (ParseException e) {
                endMonth = "Dec";
            }
        }

        String startDate = "01-" + startMonth + "-" + startYear;
        String endDate = "01-" + endMonth + "-" + endYear;
        CalendarUtil.init(startDate, endDate);
        a.recycle();

        monthNamePager.setOffscreenPageLimit(CalendarUtil.getMonthList().size());
        calendarPager.setOffscreenPageLimit(8);

        final HeaderPagerAdapter headerPagerAdapter = new HeaderPagerAdapter(context, CalendarUtil.getMonthNames(), calendarPager);
        monthNamePager.setAdapter(headerPagerAdapter);
        final MonthPagerAdapter monthPagerAdapter = new MonthPagerAdapter(context, CalendarUtil.getMonthList(), dayExpandable, this);
        calendarPager.setAdapter(monthPagerAdapter);
        CalendarUtil.setMonthPagerAdapter(monthPagerAdapter);

        events = new ArrayList<>();
        CalendarUtil.setSelectedDayEventList(events);
        final EventAdapter eventAdapter = new EventAdapter(context, CalendarUtil.getSelectedDayEventList());
        eventListView.setAdapter(eventAdapter);

        LinearLayout daysBarLayout = (LinearLayout) v.findViewById(R.id.days_bar_layout);
        CalendarUtil.createDaysBarLayout(context, daysBarLayout);

        ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
            private int scrollState = ViewPager.SCROLL_STATE_IDLE;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (scrollState == ViewPager.SCROLL_STATE_IDLE) {
                    return;
                }
                if (isCalendarPagerMaster) {
                    monthNamePager.scrollTo(calendarPager.getScrollX() * monthNamePager.getWidth() /
                            calendarPager.getWidth() / 3, 0);
                }
            }

            @Override
            public void onPageSelected(int position) {

                if (isCalendarPagerMaster) {
                    CalendarUtil.setNeighborViewUnSelected(monthNamePager, previousPosition);
                    CalendarUtil.setCurrentViewSelected(monthNamePager, position);
                    setYearText(yearText, position, previousPosition);
                    previousPosition = position;

                } else {

                    if (!rememberLastSelectedDay) {
                        events.clear();
                        CalendarUtil.setSelectedDayEventList(null);
                        eventAdapter.notifyDataSetChanged();


                        View view = calendarPager.findViewWithTag(position + "");

                        Month month = CalendarUtil.getMonthList().get(position);

                        if(month.getToday() > 0){
                            monthPagerAdapter.clickDayLayout(monthPagerAdapter.getDayLayout(view, month.getToday(), month),
                                    events, 0, month.getToday(), month);
                        }
                        else{
                            monthPagerAdapter.clickDayLayout(monthPagerAdapter.getDayLayout(view, 1, month), events, 0, 1, month);
                        }

                        CalendarUtil.setCalendarPagerHeight(calendarPager, ViewGroup.LayoutParams.MATCH_PARENT);
                    } else {
                        // TODO do nothing
                    }
                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {
                scrollState = state;
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    if (isCalendarPagerMaster) {
                        monthNamePager.setCurrentItem(calendarPager.getCurrentItem(), false);
                    }

                }
            }
        };

        calendarPager.addOnPageChangeListener(listener);
        calendarPager.setCurrentItem(CalendarUtil.getCurrentMonthIndex(), false);
        String year = calendar.get(Calendar.YEAR) + "";
        yearText.setText(year);

        monthNamePager.setCurrentItem(CalendarUtil.getCurrentMonthIndex(), false);

        ViewPager.OnPageChangeListener headerListener = new ViewPager.OnPageChangeListener() {
            private int scrollState = ViewPager.SCROLL_STATE_IDLE;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (scrollState == ViewPager.SCROLL_STATE_IDLE) {
                    return;
                }
                if (!isCalendarPagerMaster) {
                    calendarPager.scrollTo(monthNamePager.getScrollX() * calendarPager.getWidth() /
                            monthNamePager.getWidth() * 3, 0);
                }
            }

            @Override
            public void onPageSelected(int position) {


                if (!isCalendarPagerMaster) {
                    setYearText(yearText, position, previousPosition);
                    CalendarUtil.setNeighborViewUnSelected(monthNamePager, previousPosition);
                    CalendarUtil.setCurrentViewSelected(monthNamePager, position);
                    previousPosition = position;
                } else {
                    if (!rememberLastSelectedDay) {
                        events.clear();

                        Month month = CalendarUtil.getMonthList().get(position);
                        events = CalendarUtil.getMonthMap().get(month.getYear()).get(month.getMonthIndex()).getDay(0).getEventList();
                        CalendarUtil.setSelectedDayEventList(events);



                        eventAdapter.notifyDataSetChanged();

                        View view = calendarPager.findViewWithTag(position + "");



                        if(month.getToday() > 0){
                            monthPagerAdapter.clickDayLayout(monthPagerAdapter.getDayLayout(view, month.getToday(), month),
                                    events, 0, month.getToday(), month);
                        }
                        else{
                            monthPagerAdapter.clickDayLayout(monthPagerAdapter.getDayLayout(view, 1, month), events, 0, 1, month);
                        }

                        CalendarUtil.setCalendarPagerHeight(calendarPager, ViewGroup.LayoutParams.MATCH_PARENT);
                    } else {
                        // TODO do nothing
                    }
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                scrollState = state;
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    if (!isCalendarPagerMaster) {
                        calendarPager.setCurrentItem(monthNamePager.getCurrentItem(), false);
                    }
                }
            }
        };

        monthNamePager.addOnPageChangeListener(headerListener);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onDayClick(int extraHeight, int day, int monthIndex, int year) {
        ArrayList<Event> eventList = CalendarUtil.getMonthMap().get(year).get(monthIndex).getEventMap().
                get(day).getEventList();
        int eventNumber = 0;
        if (eventList != null) {
            eventNumber = eventList.size();
        }
        if (eventNumber > 0 && !disableEventList) {
            CalendarUtil.setCalendarPagerHeight(calendarPager, ViewGroup.LayoutParams.WRAP_CONTENT);
            calendarPager.getLayoutParams().height = calendarPager.getLayoutParams().height + extraHeight;
        } else {
            CalendarUtil.setCalendarPagerHeight(calendarPager, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        events.clear();
        for (int i = 0; i < eventList.size(); i++) {
            Event e = eventList.get(i);
            events.add(e);
        }

        CalendarUtil.setSelectedDayEventList(events);
        ((EventAdapter) eventListView.getAdapter()).notifyDataSetChanged();

    }

    public void blinkYear(final TextView textView) {
        ObjectAnimator colorAnim = ObjectAnimator.ofInt(textView, "textColor",
                Color.BLACK, Color.RED);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.start();
        colorAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator colorAnim = ObjectAnimator.ofInt(textView, "textColor",
                        Color.RED, Color.BLACK);
                colorAnim.setEvaluator(new ArgbEvaluator());
                colorAnim.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private Locale setCalendarLanguage(int calendarLanguage) {
        Locale output = Locale.US;
        if (calendarLanguage == getResources().getInteger(R.integer.french)) {
            output = Locale.FRANCE;
        } else if (calendarLanguage == getResources().getInteger(R.integer.german)) {
            output = Locale.GERMANY;
        } else if (calendarLanguage == getResources().getInteger(R.integer.italian)) {
            output = Locale.ITALY;
        } else if (calendarLanguage == getResources().getInteger(R.integer.russian)) {
            output = new Locale("ru", "RU");
        } else if (calendarLanguage == getResources().getInteger(R.integer.turkish)) {
            output = new Locale("tr", "TR");
        }
        return output;
    }

    private void setYearText(TextView yearText, int position, int previousPosition) {
        int newYear = CalendarUtil.getMonthList().get(position).getYear();
        int previousYear = CalendarUtil.getMonthList().get(previousPosition).getYear();
        if (newYear != previousYear) {
            String year = newYear + "";
            yearText.setText(year);
            blinkYear(yearText);
        }
    }

}
