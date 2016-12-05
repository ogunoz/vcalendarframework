package ogunoz.com.vcalendar.models;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ogun on 12/11/16.
 */

public class Month {

    private int firstDayOfMonth;
    private int lastDayOfMonth;
    private int today;
    private int year;
    private int monthIndex;
    private HashMap<Integer, Day> eventMap;

    public Month(int firstDayOfMonth, int lastDayOfMonth, int today, int monthIndex, int year) {
        setFirstDayOfMonth(firstDayOfMonth);
        setLastDayOfMonth(lastDayOfMonth);
        setToday(today);
        setMonthIndex(monthIndex);
        setYear(year);
        eventMap = new HashMap<>();
        for (int i = 0; i < lastDayOfMonth; i++) {
            ArrayList<Event> eventList = new ArrayList<>();
            eventMap.put(i, new Day(eventList, true));
        }
    }

    public void addEventToDay(int day, Event event) {
        ArrayList<Event> currentList = this.eventMap.get(day - 1).getEventList();
        if (currentList == null) {
            currentList = new ArrayList<>();
        }
        currentList.add(event);
        boolean isRead = false;
        if (event instanceof HolidayEvent) {
            isRead = true;
        }
        this.eventMap.put(day - 1, new Day(currentList, isRead));
    }

    public Day getDay(int day) {
        return this.eventMap.get(day);
    }

    public void setDay(int dayIndex, Day day) {
        this.eventMap.put(dayIndex, day);
    }

    public int getFirstDayOfMonth() {
        return firstDayOfMonth;
    }

    public void setFirstDayOfMonth(int firstDayOfMonth) {
        this.firstDayOfMonth = firstDayOfMonth;
    }

    public int getLastDayOfMonth() {
        return lastDayOfMonth;
    }

    public void setLastDayOfMonth(int lastDayOfMonth) {
        this.lastDayOfMonth = lastDayOfMonth;
    }

    public int getToday() {
        return today;
    }

    public void setToday(int today) {
        this.today = today;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonthIndex() {
        return monthIndex;
    }

    public void setMonthIndex(int monthIndex) {
        this.monthIndex = monthIndex;
    }

    public HashMap<Integer, Day> getEventMap() {
        return eventMap;
    }
}
