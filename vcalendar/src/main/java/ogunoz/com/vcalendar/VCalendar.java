package ogunoz.com.vcalendar;

import android.util.SparseArray;

import java.util.ArrayList;

import ogunoz.com.vcalendar.adapters.EventAdapter;
import ogunoz.com.vcalendar.models.Event;
import ogunoz.com.vcalendar.models.Month;
import ogunoz.com.vcalendar.util.CalendarLogic;
import ogunoz.com.vcalendar.util.CalendarView;

/**
 * Created by Ogün Öz on 24/11/16.
 */

public class VCalendar {

    private static DayListener dayListener;
    private static MonthListener monthListener;


    public static void addEvent(int day, int month, int year, Event event) {
        addEvent(day, month, year, event, false);
    }

    public static void addEvent(int day, int month, int year, Event event, boolean changingMode) {
        CalendarLogic calendarLogic = CalendarLogic.getCalendarLogicInstance();
        CalendarView calendarView = CalendarView.getCalendarViewInstance();
        if (year == 1) {
            for (int i = 0; i < calendarLogic.getMonthMap().size(); i++) {
                int yearItem = calendarLogic.getMonthMap().keyAt(i);
                int index = calendarLogic.getMonthPagerIndex(yearItem, month);
                if (changingMode) {
                    clearEventsInDay(index, day);
                }
                addEvent(event, yearItem, month, day);
                calendarView.fillDayWithEvents(index, day);
            }
        } else if (year != -1) {
            int index = calendarLogic.getMonthPagerIndex(year, month);
            if (changingMode) {
                clearEventsInDay(index, day);
            }
            addEvent(event, year, month, day);
            calendarView.fillDayWithEvents(index, day);
        }
    }

    public static void addEvent(String day, Event event) {
        addEvent(day, event, false);
    }

    public static void addEvent(String day, Event event, boolean updateMode) {
        CalendarLogic calendarLogic = CalendarLogic.getCalendarLogicInstance();
        int[] date = calendarLogic.getDateItems(day);
        int year = date[0];
        int month = date[1];
        int dayIndex = date[2];

        addEvent(dayIndex, month, year, event, updateMode);
    }

    public static void addEvents(int day, int month, int year, ArrayList<Event> eventList) {
        addEvents(day, month, year, eventList, false);
    }

    public static void addEvents(int day, int month, int year, ArrayList<Event> eventList, boolean clearingDay) {
        CalendarLogic calendarLogic = CalendarLogic.getCalendarLogicInstance();
        CalendarView calendarView = CalendarView.getCalendarViewInstance();
        if (year == 1) {

            for (int i = 0; i < calendarLogic.getMonthMap().size(); i++) {
                int yearItem = calendarLogic.getMonthMap().keyAt(i);
                for (Event event : eventList) {
                    int index = calendarLogic.getMonthPagerIndex(yearItem, month);
                    if (clearingDay) {
                        clearEventsInDay(index, day);
                    }
                    addEvent(event, yearItem, month, day);
                    calendarView.fillDayWithEvents(index, day);
                }
            }
        } else if (year != -1) {
            int index = calendarLogic.getMonthPagerIndex(year, month);
            if (clearingDay) {
                clearEventsInDay(index, day);
            }
            for (Event event : eventList) {
                addEvent(event, year, month, day);
            }
            calendarView.fillDayWithEvents(index, day);
        }
    }

    public static void addEvents(String day, ArrayList<Event> eventList) {
        addEvents(day, eventList, false);
    }

    public static void addEvents(String day, ArrayList<Event> eventList, boolean updateMode) {
        CalendarLogic calendarLogic = CalendarLogic.getCalendarLogicInstance();
        int[] date = calendarLogic.getDateItems(day);
        int year = date[0];
        int month = date[1];
        int dayIndex = date[2];
        addEvents(dayIndex, month, year, eventList, updateMode);
    }

    private static void addEvent(Event event, int year, int month, int day) {
        CalendarLogic calendarLogic = CalendarLogic.getCalendarLogicInstance();
        SparseArray<Month> monthSparseArray = calendarLogic.getMonthMap().get(year);

        if (monthSparseArray != null) {
            Month m = monthSparseArray.get(month);
            if (m != null) {
                m.addEventToDay(day, event);
            }
        }
    }

    public static void clearEventsInDay(int position, int day) {
        CalendarLogic calendarLogic = CalendarLogic.getCalendarLogicInstance();
        CalendarView calendarView = CalendarView.getCalendarViewInstance();
        Month month = calendarLogic.getMonthList().get(position);
        calendarView.clearEventsInDay(position, day, month);
        calendarLogic.clearEventsInDay(day, month.getMonthIndex(), month.getYear());
    }

    public static void clearEventsInMonth(int month, int year) {
        CalendarLogic calendarLogic = CalendarLogic.getCalendarLogicInstance();
        int position = calendarLogic.getMonthPagerIndex(year, month);
        clearEventsInMonth(position);
    }

    private static void clearEventsInMonth(int position) {
        CalendarLogic calendarLogic = CalendarLogic.getCalendarLogicInstance();
        CalendarView calendarView = CalendarView.getCalendarViewInstance();
        Month month = calendarLogic.getMonthList().get(position);

        for (int i = 1; i <= month.getLastDayOfMonth(); i++) {
            calendarView.clearEventsInDay(position, i, month);
            calendarLogic.clearEventsInDay(i, month.getMonthIndex(), month.getYear());
        }
    }

    public static String getCurrentPositionMonthName() {
        CalendarLogic calendarLogic = CalendarLogic.getCalendarLogicInstance();
        return calendarLogic.getMonthNames().get(calendarLogic.getCurrentPosition());
    }

    public static int getCurrentPositionYear() {
        CalendarLogic calendarLogic = CalendarLogic.getCalendarLogicInstance();
        return calendarLogic.getMonthList().get(calendarLogic.getCurrentPosition()).getYear();
    }

    public static DayListener getDayListener() {
        return dayListener;
    }

    public static void setDayListener(DayListener dayListener) {
        VCalendar.dayListener = dayListener;
    }

    public static MonthListener getMonthListener() {
        return monthListener;
    }

    public static void setMonthListener(MonthListener monthListener) {
        VCalendar.monthListener = monthListener;
    }

    public static void setEventListViewAdapter(EventAdapter customEventAdapter) {
        CalendarView calendarView = CalendarView.getCalendarViewInstance();
        calendarView.setEventListViewAdapter(customEventAdapter);
    }
}
