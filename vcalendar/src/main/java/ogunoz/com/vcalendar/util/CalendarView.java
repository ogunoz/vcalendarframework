package ogunoz.com.vcalendar.util;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import ogunoz.com.vcalendar.R;
import ogunoz.com.vcalendar.VCalendarApp;
import ogunoz.com.vcalendar.adapters.EventAdapter;
import ogunoz.com.vcalendar.customview.ExpandableLayout;
import ogunoz.com.vcalendar.models.Event;
import ogunoz.com.vcalendar.models.Month;

public class CalendarView {

    private SparseIntArray calendarHeightList;
    private int daySelectionColor;
    private int extraViewID;
    private Drawable dayUnreadIcon;
    // TODO ViewPager ve ListView tutmak sıkıntı yaratıyor.
    private ViewPager calendarPager;
    private ListView eventListView;


    private static CalendarView calendarView;

    private CalendarView() {
        calendarHeightList = new SparseIntArray();
    }

    public static CalendarView getCalendarViewInstance() {
        if (calendarView == null) {
            calendarView = new CalendarView();
        }
        return calendarView;
    }

    public RelativeLayout getDayLayout(View view, int day, Month month) {
        if (view == null)
            return null;
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

    public void fillDayWithEvents(int position, int day) {
        View view = getCalendarPager().findViewWithTag(position + "");
        if (view != null) {

            CalendarLogic calendarLogic = CalendarLogic.getCalendarLogicInstance();

            Month month = calendarLogic.getMonthList().get(position);
            RelativeLayout dayLayout = getDayLayout(view, day, month);
            if (dayLayout != null) {
                ImageView unread = (ImageView) dayLayout.findViewById(R.id.unread_image_id);
                unread.setVisibility(View.VISIBLE);
                LinearLayout eventLayout = (LinearLayout) dayLayout.findViewById(R.id.event_layout_id);

                ArrayList<Event> eventList = calendarLogic.getEventListInDay(day, month.getMonthIndex(), month.getYear());

                for (Event event : eventList) {
                    ImageView eventView = new ImageView(VCalendarApp.getContext());
                    eventView.setBackgroundResource(R.drawable.today_button_selector);
                    eventView.getBackground().setColorFilter(event.getColor(), PorterDuff.Mode.SRC);

                    int size = DeviceScreenUtil.dpToPx(VCalendarApp.getContext(), 5);
                    LinearLayout.LayoutParams eventPointLayoutParam = new LinearLayout.LayoutParams(size, size);
                    if (eventLayout.getChildCount() != 0) {
                        eventPointLayoutParam.setMargins(size / 2, 0, 0, 0);
                    }
                    eventView.setLayoutParams(eventPointLayoutParam);
                    eventLayout.addView(eventView);
                }

            }
        }
    }

    public void setCurrentViewSelected(ViewPager pager, int position) {
        TextView textView = getCurrentTextView(pager, position);
        if (textView != null) {
            textView.setTextSize(32);
            textView.setAlpha(1.0f);
        }
    }

    public void setNeighborViewUnSelected(ViewPager pager, int position) {
        TextView textView = getCurrentTextView(pager, position);
        if (textView != null) {
            textView.setTextSize(14);
            textView.setAlpha(0.5f);
        }
    }

    private TextView getCurrentTextView(ViewPager pager, int position) {
        View view = pager.findViewWithTag(position + "");
        if (view == null) {
            return null;
        }
        return (TextView) view.findViewById(R.id.month_name_text_view);
    }

    public void createDaysBarLayout(Context context, LinearLayout layout) {
        String[] dayArray = {"S", "M", "T", "W", "T", "F", "S", "S",
                "S", "M", "D", "M", "D", "F", "S", "S",
                "D", "L", "M", "M", "J", "V", "S", "D",
                "D", "L", "M", "M", "G", "V", "S", "D",
                "В", "П", "В", "С", "Ч", "П", "С", "В",
                "P", "P", "S", "Ç", "P", "C", "C", "P"};

        CalendarLogic calendarLogic = CalendarLogic.getCalendarLogicInstance();

        int startIndex = 1;
        int endIndex = 8;
        Locale calendarLanguage = calendarLogic.getLanguage();
        if (calendarLanguage == Locale.GERMANY) {
            startIndex += 8;
            endIndex += 8;
        } else if (calendarLanguage == Locale.FRANCE) {
            startIndex += 16;
            endIndex += 16;
        } else if (calendarLanguage == Locale.ITALY) {
            startIndex += 24;
            endIndex += 24;
        } else if (TextUtils.equals(calendarLanguage.getLanguage(), "ru")) {
            startIndex += 32;
            endIndex += 32;
        } else if (TextUtils.equals(calendarLanguage.getLanguage(), "tr")) {
            startIndex += 40;
            endIndex += 40;
        }

        if (calendarLogic.getCalendarType() == context.getResources().getInteger(R.integer.american)) {
            startIndex--;
            endIndex--;
        }

        for (int i = startIndex; i < endIndex; i++) {
            Button textView = new Button(context);
            textView.setTypeface(null, Typeface.NORMAL);
            LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            textView.setText(dayArray[i]);
            textView.setTextSize(14);
            textView.setBackgroundResource(0);
            textView.setLayoutParams(barParams);
            textView.setMinimumHeight(0);
            textView.setMinHeight(0);
            layout.addView(textView);
        }
    }

    public void clearEventsInDay(int position, int day, Month month) {
        View view = getCalendarPager().findViewWithTag(position + "");
        RelativeLayout dayLayout = getDayLayout(view, day, month);
        if (dayLayout != null) {
            ImageView unread = (ImageView) dayLayout.findViewById(R.id.unread_image_id);
            unread.setVisibility(View.GONE);
            LinearLayout eventLayout = (LinearLayout) dayLayout.findViewById(R.id.event_layout_id);
            eventLayout.removeAllViews();
        }
    }

    private ViewPager getCalendarPager() {
        return calendarPager;
    }

    public void setCalendarPager(ViewPager calendarPager) {
        this.calendarPager = calendarPager;
    }


    public Drawable getDayUnreadIcon() {
        return this.dayUnreadIcon;
    }

    public void setDayUnreadIcon(Drawable dayUnreadIcon) {
        this.dayUnreadIcon = dayUnreadIcon;
    }

    public int getDaySelectionColor() {
        return this.daySelectionColor;
    }

    public void setDaySelectionColor(int daySelectionColor) {
        this.daySelectionColor = daySelectionColor;
    }

    public void setEventListView(ListView eventListView) {
        this.eventListView = eventListView;
    }

    public void setEventListViewAdapter(EventAdapter adapter) {
        this.eventListView.setAdapter(adapter);
    }

    public int getExtraViewID() {
        return this.extraViewID;
    }

    public void setExtraViewID(int extraViewID) {
        this.extraViewID = extraViewID;
    }

    public SparseIntArray getCalendarHeightList() {
        return calendarHeightList;
    }
}
