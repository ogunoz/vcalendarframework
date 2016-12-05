package ogunoz.com.vcalendar.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

import ogunoz.com.vcalendar.DayListener;
import ogunoz.com.vcalendar.R;
import ogunoz.com.vcalendar.adapters.EventAdapter;
import ogunoz.com.vcalendar.adapters.MonthPagerAdapter;
import ogunoz.com.vcalendar.models.Event;
import ogunoz.com.vcalendar.models.Month;


/**
 * Created by Ogün Öz on 24/11/16.
 */

public class CalendarUtil {

    private static ArrayList<String> monthNames;
    private static ArrayList<Month> monthList;
    private static HashMap<Integer, HashMap<Integer, Month>> monthMap;

    private static MonthPagerAdapter monthPagerAdapter;

    private static int CURRENT_MONTH_INDEX;

    private static int calendarType = 2;
    private static boolean disableHolidaysAndWeekends = true;

    private static int extraViewID;

    private static ListView eventListView;
    private static ArrayList<Event> selectedDayEventList;
    private static Locale language;

    private static int daySelectionColor;

    private static DayListener dayListener;
    private static Drawable dayUnreadIcon;

    public static void init(String startDate, String endDate) {
        monthNames = new ArrayList<>();
        monthList = new ArrayList<>();
        monthMap = new HashMap<>();

        Calendar startCalendar = new GregorianCalendar();
        startCalendar.setTime(DateUtil.getDateFromString(startDate));
        Calendar todayCalendar = new GregorianCalendar();
        todayCalendar.setTime(new Date());
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(DateUtil.getDateFromString(endDate));

        int diffYearPast = todayCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
        int diffMonthPast = diffYearPast * 12 + todayCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
        int diffYearFuture = endCalendar.get(Calendar.YEAR) - todayCalendar.get(Calendar.YEAR);
        int diffMonthFuture = diffYearFuture * 12 + endCalendar.get(Calendar.MONTH) - todayCalendar.get(Calendar.MONTH);

        fillMonthLists(diffMonthPast, diffMonthFuture);
    }


    private static void fillMonthLists(int diffMonthPast, int diffMonthFuture) {
        CURRENT_MONTH_INDEX = diffMonthPast;
        int totalMonthNum = diffMonthPast + 1 + diffMonthFuture;
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - diffMonthPast);
        int year;
        int previousYear = -1;
        HashMap<Integer, Month> monthHashMap = new HashMap<>();

        for (int i = 0; i < totalMonthNum; i++) {
            int today = -1;
            if (i == diffMonthPast) {
                today = currentDay;
            }
            int lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            calendar.set(Calendar.DAY_OF_MONTH, 1);
            monthNames.add(calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, getLanguage()));

            int firstDayOfMonth = (calendar.get(Calendar.DAY_OF_WEEK) - CalendarUtil.getCalendarType()) % 7;
            if (firstDayOfMonth < 0) firstDayOfMonth += 7;
            int monthIndex = calendar.get(Calendar.MONTH);
            Month month = new Month(firstDayOfMonth, lastDayOfMonth, today, monthIndex, calendar.get(Calendar.YEAR));

            year = calendar.get(Calendar.YEAR);
            if (year == previousYear + 1) {
                monthMap.put(previousYear, monthHashMap);
                monthHashMap = new HashMap<>();
            }
            monthList.add(month);
            monthHashMap.put(monthIndex, month);
            previousYear = year;
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
        }
        monthMap.put(previousYear, monthHashMap);
    }

    public static void createDaysBarLayout(Context context, LinearLayout layout) {
        String[] dayArray = {"S", "M", "T", "W", "T", "F", "S", "S",
                "S", "M", "D", "M", "D", "F", "S", "S",
                "D", "L", "M", "M", "J", "V", "S", "D",
                "D", "L", "M", "M", "G", "V", "S", "D",
                "В", "П", "В", "С", "Ч", "П", "С", "В",
                "P", "P", "S", "Ç", "P", "C", "C", "P"};

        int startIndex = 1;
        int endIndex = 8;
        if (getLanguage() == Locale.GERMANY) {
            startIndex += 8;
            endIndex += 8;
        } else if (getLanguage() == Locale.FRANCE) {
            startIndex += 16;
            endIndex += 16;
        } else if (getLanguage() == Locale.ITALY) {
            startIndex += 24;
            endIndex += 24;
        } else if (TextUtils.equals(getLanguage().getLanguage(), "ru")) {
            startIndex += 32;
            endIndex += 32;
        } else if (TextUtils.equals(getLanguage().getLanguage(), "tr")) {
            startIndex += 40;
            endIndex += 40;
        }

        if (CalendarUtil.calendarType == context.getResources().getInteger(R.integer.american)) {
            startIndex--;
            endIndex--;
        }

        for (int i = startIndex; i < endIndex; i++) {
            Button textView = new Button(context);
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

    public static void setCalendarPagerHeight(ViewPager pager, int heightParam) {
        switch (heightParam) {
            case ViewGroup.LayoutParams.MATCH_PARENT:
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                pager.setLayoutParams(params);
                break;
            case ViewGroup.LayoutParams.WRAP_CONTENT:
                View view = pager.findViewWithTag(pager.getCurrentItem() + "");
                view.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                pager.getLayoutParams().height = view.getMeasuredHeight() + 50;
                break;
        }
    }

    public static void setCurrentViewSelected(ViewPager pager, int position) {
        TextView textView = getCurrentTextView(pager, position);
        if (textView != null) {
            textView.setTextSize(32);
            textView.setAlpha(1.0f);
        }
    }

    public static void setNeighborViewUnSelected(ViewPager pager, int position) {
        TextView textView = getCurrentTextView(pager, position);
        if (textView != null) {
            textView.setTextSize(14);
            textView.setAlpha(0.5f);
        }
    }

    private static TextView getCurrentTextView(ViewPager pager, int position) {
        View view = pager.findViewWithTag(position + "");
        if (view == null) {
            return null;
        }
        return (TextView) view.findViewById(R.id.month_name_text_view);
    }

    public static ArrayList<String> getMonthNames() {
        return monthNames;
    }

    public static ArrayList<Month> getMonthList() {
        return monthList;
    }

    public static int getCurrentMonthIndex() {
        return CURRENT_MONTH_INDEX;
    }

    public static void addEvent(String day, Event event) {
        int[] date = getDateItems(day);
        int year = date[0];
        int month = date[1];
        int dayIndex = date[2];

        if (year == 1) {
            for (int yearItem : CalendarUtil.getMonthMap().keySet()) {
                addEvent(event, yearItem, month, dayIndex);
            }
        } else if (year != -1) {
            addEvent(event, year, month, dayIndex);
        }
    }

    public static void addEvents(String day, ArrayList<Event> eventList) {
        int[] date = getDateItems(day);
        int year = date[0];
        int month = date[1];
        int dayIndex = date[2];

        if (year == 1) {
            for (int yearItem : CalendarUtil.getMonthMap().keySet()) {
                for (Event event : eventList) {
                    addEvent(event, yearItem, month, dayIndex);
                }
            }
        } else if (year != -1) {
            for (Event event : eventList) {
                addEvent(event, year, month, dayIndex);
            }
        }
    }

    public static void setMonthPagerAdapter(MonthPagerAdapter monthPagerAdapter) {
        CalendarUtil.monthPagerAdapter = monthPagerAdapter;
    }

    public static HashMap<Integer, HashMap<Integer, Month>> getMonthMap() {
        return monthMap;
    }

    public static int getCalendarType() {
        return calendarType;
    }

    public static void setCalendarType(int calendarType) {
        CalendarUtil.calendarType = calendarType;
    }

    public static boolean isDisableHolidaysAndWeekends() {
        return disableHolidaysAndWeekends;
    }

    public static void setDisableHolidaysAndWeekends(boolean disableHolidaysAndWeekends) {
        CalendarUtil.disableHolidaysAndWeekends = disableHolidaysAndWeekends;
    }

    public static int getExtraViewID() {
        return extraViewID;
    }

    public static void setExtraViewID(int extraViewID) {
        CalendarUtil.extraViewID = extraViewID;
    }

    public static void setEventListViewAdapter(EventAdapter adapter) {
        CalendarUtil.eventListView.setAdapter(adapter);
    }

    public static void setEventListView(ListView eventListView) {
        CalendarUtil.eventListView = eventListView;
    }

    public static ArrayList<Event> getSelectedDayEventList() {
        return selectedDayEventList;
    }

    public static void setSelectedDayEventList(ArrayList<Event> selectedDayEventList) {
        if (CalendarUtil.getSelectedDayEventList() == null) {
            CalendarUtil.selectedDayEventList = new ArrayList<>();
        }
        CalendarUtil.selectedDayEventList.clear();
        if (selectedDayEventList != null) {
            CalendarUtil.selectedDayEventList.addAll(selectedDayEventList);
        }
    }

    public static Locale getLanguage() {
        return language;
    }

    public static void setLanguage(Locale language) {
        CalendarUtil.language = language;
    }

    public static int getDaySelectionColor() {
        return daySelectionColor;
    }

    public static void setDaySelectionColor(int daySelectionColor) {
        CalendarUtil.daySelectionColor = daySelectionColor;
    }

    public static DayListener getDayListener() {
        return dayListener;
    }

    public static void setDayListener(DayListener dayListener) {
        CalendarUtil.dayListener = dayListener;
    }

    public static Drawable getDayUnreadIcon() {
        return dayUnreadIcon;
    }

    public static void setDayUnreadIcon(Drawable dayUnreadIcon) {
        CalendarUtil.dayUnreadIcon = dayUnreadIcon;
    }

    private static void addEvent(Event event, int year, int month, int day) {
        HashMap<Integer, Month> monthHashMap = CalendarUtil.monthMap.get(year);

        if (monthHashMap != null) {
            Month m = monthHashMap.get(month);
            if (m != null) {
                m.addEventToDay(day, event);
                if (CalendarUtil.monthPagerAdapter != null) {
                    CalendarUtil.monthPagerAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private static int[] getDateItems(String day) {
        int[] output = {-1, -1, -1};
        String[] splitter = day.split("-");
        if (splitter.length == 2) {
            day = day.concat("-0000");
        }

        Calendar calendar = Calendar.getInstance();
        Date date = DateUtil.getDateFromString(day);
        if (date != null) {
            calendar.setTime(date);
            output[0] = calendar.get(Calendar.YEAR);
            output[1] = calendar.get(Calendar.MONTH);
            output[2] = calendar.get(Calendar.DAY_OF_MONTH);
            calendar.setTime(new Date());
        }
        return output;
    }
}
