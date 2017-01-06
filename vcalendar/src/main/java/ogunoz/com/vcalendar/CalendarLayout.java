package ogunoz.com.vcalendar;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HeaderViewListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ogunoz.com.vcalendar.adapters.EventAdapter;
import ogunoz.com.vcalendar.adapters.HeaderPagerAdapter;
import ogunoz.com.vcalendar.adapters.MonthPagerAdapter;
import ogunoz.com.vcalendar.customview.DraggableListView;
import ogunoz.com.vcalendar.models.Event;
import ogunoz.com.vcalendar.models.Month;
import ogunoz.com.vcalendar.util.CalendarLogic;
import ogunoz.com.vcalendar.util.CalendarView;
import ogunoz.com.vcalendar.util.Constants;
import ogunoz.com.vcalendar.util.DeviceScreenUtil;


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
    private int previousPosition;
    private int screenHeight;
    private int lastEventLayoutY;

    private int heightBeforeCalendarLayout;

    private CardView eventLayout;


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
        if (isInEditMode())
            return;

        final TypedArray styledAttributes = getContext().getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize});
        int mActionBarSize = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        heightBeforeCalendarLayout = mActionBarSize;
        heightBeforeCalendarLayout += DeviceScreenUtil.dpToPx(context, 42);

        View v = View.inflate(context, R.layout.calendar_layout, this);

        eventLayout = (CardView) v.findViewById(R.id.event_layout_in_calendar_layout);
        eventLayout.setContentPadding(-eventLayout.getPaddingLeft(), 0,
                -eventLayout.getPaddingRight(), -eventLayout.getPaddingBottom());
        screenHeight = DeviceScreenUtil.getScreenHeight(context);
        eventLayout.setY(screenHeight);
        lastEventLayoutY = screenHeight;

        final Calendar calendar = Calendar.getInstance();

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CalendarLayout);

        int startYear = a.getInteger(R.styleable.CalendarLayout_startYear, calendar.get(Calendar.YEAR));
        int startMonthNum = a.getInt(R.styleable.CalendarLayout_startMonth, getResources().getInteger(R.integer.firstMonth));
        int endYear = a.getInteger(R.styleable.CalendarLayout_endYear, calendar.get(Calendar.YEAR));
        int endMonthNum = a.getInt(R.styleable.CalendarLayout_endMonth, getResources().getInteger(R.integer.lastMonth));
        boolean disableHolidaysAndWeekends = a.getBoolean(R.styleable.CalendarLayout_disableHolidaysAndWeekends, true);
        int calendarType = a.getInt(R.styleable.CalendarLayout_calendarType, getResources().getInteger(R.integer.european));
        int calendarLanguage = a.getInt(R.styleable.CalendarLayout_calendarLanguage, getResources().getInteger(R.integer.english));
        final int extraContentID = a.getResourceId(R.styleable.CalendarLayout_extraLayout, -1);
        disableEventList = a.getBoolean(R.styleable.CalendarLayout_disableEventList, false);
        Drawable dayUnreadIcon = a.getDrawable(R.styleable.CalendarLayout_dayUnreadIcon);
        final boolean rememberLastSelectedDay = a.getBoolean(R.styleable.CalendarLayout_rememberLastSelectedDay, false);

        int previousMonthNumber = a.getInteger(R.styleable.CalendarLayout_previousMonthNumber, -1);
        int nextMonthNumber = a.getInteger(R.styleable.CalendarLayout_nextMonthNumber, -1);

        // TODO get list divider default color as default value
        int listDividerColor = a.getColor(R.styleable.CalendarLayout_eventListDividerColor, Color.GRAY);
        int dividerHeight = a.getDimensionPixelSize(R.styleable.CalendarLayout_eventListDividerHeight, -1);
        int daySelectionColor = a.getColor(R.styleable.CalendarLayout_daySelectionColor, ContextCompat.getColor(context,
                R.color.day_selection_color));
        int pageOffset = a.getInteger(R.styleable.CalendarLayout_calendarPageOffset, 1);
        if (pageOffset < 0) {
            pageOffset = 1;
        }

        final CalendarView calendarView = CalendarView.getCalendarViewInstance();
        final CalendarLogic calendarLogic = CalendarLogic.getCalendarLogicInstance();

        monthNamePager = (ViewPager) v.findViewById(R.id.month_name_pager);
        calendarPager = (ViewPager) v.findViewById(R.id.calendar_pager);
        eventListView = (DraggableListView) v.findViewById(R.id.event_list_view);

        calendarView.setCalendarPager(calendarPager);

        final LinearLayout wholeLayout = (LinearLayout) v.findViewById(R.id.whole_calendar_layout);
        wholeLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                screenHeight = wholeLayout.getHeight();
                eventLayout.setY(screenHeight);
                lastEventLayoutY = screenHeight;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    wholeLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    wholeLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });

        LayoutInflater inflater = LayoutInflater.from(context);
        View listHeaderView = inflater.inflate(R.layout.event_header, eventListView, false);
        eventListView.addHeaderView(listHeaderView);

        monthNamePager.setOffscreenPageLimit(pageOffset);
        calendarPager.setOffscreenPageLimit(pageOffset);

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


        calendarView.setEventListView(eventListView);
        final TextView yearText = (TextView) v.findViewById(R.id.year_text);

        yearText.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        heightBeforeCalendarLayout += yearText.getMeasuredHeight();


        if (listDividerColor != Color.GRAY && eventListView.getDivider() != null) {
            eventListView.getDivider().setColorFilter(listDividerColor, PorterDuff.Mode.SRC);
        }
        if (dividerHeight != -1) {
            eventListView.setDividerHeight(dividerHeight);
        }


        boolean dayExpandable = false;
        if (extraContentID != -1) {
            dayExpandable = true;
        }
        calendarView.setDayUnreadIcon(dayUnreadIcon);
        calendarLogic.setLanguage(setCalendarLanguage(calendarLanguage));
        calendarView.setDaySelectionColor(daySelectionColor);
        calendarView.setExtraViewID(extraContentID);
        calendarLogic.setDisableHolidaysAndWeekends(disableHolidaysAndWeekends);
        calendarLogic.setCalendarType(calendarType);

        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);
        String startDate;
        String endDate;

        String startMonth = Constants.getMonthName(startMonthNum);
        if (previousMonthNumber < 0) {

            if (startYear < calendar.getMinimum(Calendar.YEAR)) {
                startYear = calendar.getMinimum(Calendar.YEAR);
            } else if (startYear > calendar.get(Calendar.YEAR)) {
                startYear = calendar.get(Calendar.YEAR);
            }

            int calendarYear = calendar.get(Calendar.YEAR);
            if (startYear == calendarYear) {
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
            startDate = "01-" + startMonth + "-" + startYear;
        } else {
            calendar.set(Calendar.YEAR, currentYear);
            calendar.set(Calendar.MONTH, currentMonth - previousMonthNumber);
            startMonth = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US);
            startDate = "01-" + startMonth + "-" + calendar.get(Calendar.YEAR);
            calendar.set(Calendar.MONTH, currentMonth);
            calendar.set(Calendar.YEAR, currentYear);
        }
        String endMonth = Constants.getMonthName(endMonthNum);

        if (nextMonthNumber < 0) {

            if (endYear > calendar.getMaximum(Calendar.YEAR)) {
                endYear = calendar.getMaximum(Calendar.YEAR);
            } else if (endYear < calendar.get(Calendar.YEAR)) {
                endYear = calendar.get(Calendar.YEAR);
            }

            int calendarYear = calendar.get(Calendar.YEAR);
            if (endYear == calendarYear) {
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
            endDate = "01-" + endMonth + "-" + endYear;
        } else {
            calendar.set(Calendar.YEAR, currentYear);
            calendar.set(Calendar.MONTH, currentMonth + nextMonthNumber);
            endMonth = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US);
            endDate = "01-" + endMonth + "-" + calendar.get(Calendar.YEAR);
            calendar.set(Calendar.MONTH, currentMonth);
            calendar.set(Calendar.YEAR, currentYear);
        }

        calendarLogic.fillMonthLists(startDate, endDate);
        a.recycle();

        previousPosition = calendarLogic.getCurrentMonthIndex();

        final HeaderPagerAdapter headerPagerAdapter = new HeaderPagerAdapter(context, calendarLogic.getMonthNames(), calendarPager);
        monthNamePager.setAdapter(headerPagerAdapter);

        final MonthPagerAdapter monthPagerAdapter = new MonthPagerAdapter(context, calendarLogic.getMonthList(),
                dayExpandable, this, calendarLogic.getMonthMap());
        calendarPager.setAdapter(monthPagerAdapter);

        events = new ArrayList<>();
        calendarLogic.setSelectedDayEventList(events);
        final EventAdapter eventAdapter = new EventAdapter(context, calendarLogic.getSelectedDayEventList());
        eventListView.setAdapter(eventAdapter);

        LinearLayout daysBarLayout = (LinearLayout) v.findViewById(R.id.days_bar_layout);

        daysBarLayout.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        heightBeforeCalendarLayout += daysBarLayout.getMeasuredHeight();

        calendarView.createDaysBarLayout(context, daysBarLayout);

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
                    calendarView.setNeighborViewUnSelected(monthNamePager, previousPosition);
                    calendarView.setCurrentViewSelected(monthNamePager, position);
                    calendarLogic.setCurrentPosition(position);
                    setYearText(yearText, position, previousPosition);
                    previousPosition = position;
                    selectDay(rememberLastSelectedDay, position, monthPagerAdapter);

                } else {
                    MonthListener monthListener = VCalendar.getMonthListener();
                    if (monthListener != null) {
                        Month month = calendarLogic.getMonthList().get(position);
                        monthListener.onMonthSelected(month.getMonthIndex(), month.getYear());
                    }
                }
                //else {
                //   selectDay(rememberLastSelectedDay, position, monthPagerAdapter);
                // }
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

        calendarPager.setCurrentItem(calendarLogic.getCurrentMonthIndex(), false);
        MonthListener monthListener = VCalendar.getMonthListener();
        if (monthListener != null) {
            // TODO burada henuz set edilmemis oluyor monthListener
            Month month = calendarLogic.getMonthList().get(calendarLogic.getCurrentMonthIndex());
            monthListener.onMonthSelected(month.getMonthIndex(), month.getYear());
        }
        calendarPager.addOnPageChangeListener(listener);

        String year = calendar.get(Calendar.YEAR) + "";
        yearText.setText(year);


        monthNamePager.setCurrentItem(calendarLogic.getCurrentMonthIndex(), false);
        calendarLogic.setCurrentPosition(calendarLogic.getCurrentMonthIndex());

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
                    calendarView.setNeighborViewUnSelected(monthNamePager, previousPosition);
                    calendarView.setCurrentViewSelected(monthNamePager, position);
                    calendarLogic.setCurrentPosition(position);
                    previousPosition = position;
                    selectDay(rememberLastSelectedDay, position, monthPagerAdapter);


                } else {
                    MonthListener monthListener = VCalendar.getMonthListener();
                    if (monthListener != null) {
                        Month month = calendarLogic.getMonthList().get(position);
                        monthListener.onMonthSelected(month.getMonthIndex(), month.getYear());
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
    public void onDayClick(int day, int monthIndex, int year) {
        ArrayList<Event> eventList;
        final CalendarLogic calendarLogic = CalendarLogic.getCalendarLogicInstance();
        CalendarView calendarView = CalendarView.getCalendarViewInstance();
        if (day >= 0) {
            eventList = calendarLogic.getMonthMap().get(year).get(monthIndex).getEventMap().
                    get(day).getEventList();
        } else {
            eventList = null;
        }

        int eventNumber = 0;
        if (eventList != null) {
            eventNumber = eventList.size();
        }

        int calendarHeight = calendarView.getCalendarHeightList().get(calendarLogic.getCurrentPosition());
        int extraHeight = calendarView.getCalendarHeightList().get(-1);
        calendarHeight += extraHeight;

        final LinearLayout.LayoutParams eventListParam = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        eventListParam.height = screenHeight - heightBeforeCalendarLayout - calendarHeight -
                DeviceScreenUtil.dpToPx(getContext(), 2) - eventLayout.getPaddingTop();


        if (eventNumber > 0) {
            events.clear();
            for (int i = 0; i < eventNumber; i++) {
                Event e = eventList.get(i);
                events.add(e);
            }
            calendarLogic.setSelectedDayEventList(events);
            getEventListAdapter().notifyDataSetChanged();
            if (heightBeforeCalendarLayout + calendarHeight != lastEventLayoutY) {
                eventLayout.animate().y(heightBeforeCalendarLayout + calendarHeight).setDuration(getContext().
                        getResources().getInteger(R.integer.animationDuration)).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        if (eventListView.getHeight() < eventListParam.height) {
                            eventListView.setLayoutParams(eventListParam);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (eventListView.getHeight() > eventListParam.height) {
                            eventListView.setLayoutParams(eventListParam);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                lastEventLayoutY = heightBeforeCalendarLayout + calendarHeight;
            }
        } else {
            if (screenHeight != lastEventLayoutY) {
                eventLayout.animate().y(screenHeight).setDuration(getContext().getResources().
                        getInteger(R.integer.animationDuration)).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        events.clear();
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        calendarLogic.setSelectedDayEventList(events);
                        getEventListAdapter().notifyDataSetChanged();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                lastEventLayoutY = screenHeight;
            }
        }
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
        CalendarLogic calendarLogic = CalendarLogic.getCalendarLogicInstance();
        int newYear = calendarLogic.getMonthList().get(position).getYear();
        int previousYear = calendarLogic.getMonthList().get(previousPosition).getYear();
        if (newYear != previousYear) {
            String year = newYear + "";
            yearText.setText(year);
            blinkYear(yearText);
        }
    }

    private void selectDay(boolean remembering, int position, MonthPagerAdapter monthPagerAdapter) {
        CalendarLogic calendarLogic = CalendarLogic.getCalendarLogicInstance();
        Month month = calendarLogic.getMonthList().get(position);

        View view = calendarPager.findViewWithTag(position + "");
        ArrayList<Event> eventArrayList;

        int targetDay = 1;
        if (month.getToday() > 0) {
            targetDay = month.getToday();
        }
        if (remembering) {
            targetDay = -10;
            if (position == calendarLogic.getLastClickedMonthPosition()) {
                targetDay = calendarLogic.getLastClickedDayIndex();
            }
        }

        if (targetDay > 0) {
            eventArrayList = calendarLogic.getMonthMap().get(month.getYear()).
                    get(month.getMonthIndex()).getDay(targetDay - 1).getEventList();
        } else {
            eventArrayList = new ArrayList<>();
        }
        monthPagerAdapter.onDayClick(view, eventArrayList, targetDay, month);
    }

    private EventAdapter getEventListAdapter() {
        EventAdapter adapter;
        if (eventListView.getAdapter().getClass().equals(HeaderViewListAdapter.class)) {
            HeaderViewListAdapter wrapperAdapter = (HeaderViewListAdapter) eventListView.getAdapter();
            adapter = (EventAdapter) wrapperAdapter.getWrappedAdapter();
        } else {
            adapter = (EventAdapter) eventListView.getAdapter();
        }
        return adapter;
    }

}
