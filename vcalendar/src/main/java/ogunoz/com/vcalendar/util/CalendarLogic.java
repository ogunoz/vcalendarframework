package ogunoz.com.vcalendar.util;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import ogunoz.com.vcalendar.models.Event;
import ogunoz.com.vcalendar.models.Month;

public class CalendarLogic {

    private SparseArray<SparseArray<Month>> monthMap;
    private ArrayList<String> monthNames;
    private ArrayList<Month> monthList;
    private ArrayList<Event> selectedDayEventList;
    private Locale language;

    private String startDate;
    private int calendarType = 2;
    private int lastClickedDayIndex = -1;
    private int lastClickedMonthPosition = -1;
    private int currentMonthIndex;
    private int currentPosition = 0;
    private boolean disableHolidaysAndWeekends = true;

    private static CalendarLogic calendarLogic;

    private CalendarLogic() {
        this.monthMap = new SparseArray<>();
        this.monthNames = new ArrayList<>();
        this.monthList = new ArrayList<>();
    }

    public static CalendarLogic getCalendarLogicInstance() {
        if (calendarLogic == null) {
            calendarLogic = new CalendarLogic();
        }
        return calendarLogic;
    }

    public void fillMonthLists(String startDate, String endDate) {
        setStartDate(startDate);

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

        setCurrentMonthIndex(diffMonthPast);
        currentPosition = diffMonthPast;
        int totalMonthNum = diffMonthPast + 1 + diffMonthFuture;
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - diffMonthPast);
        int year;
        int previousYear = -1;
        SparseArray<Month> monthSparseArray = new SparseArray<>();

        for (int i = 0; i < totalMonthNum; i++) {
            int today = -1;
            if (i == diffMonthPast) {
                today = currentDay;
            }
            int lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            calendar.set(Calendar.DAY_OF_MONTH, 1);
            monthNames.add(calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, getLanguage()));

            int firstDayOfMonth = (calendar.get(Calendar.DAY_OF_WEEK) - getCalendarType()) % 7;
            if (firstDayOfMonth < 0) firstDayOfMonth += 7;
            int monthIndex = calendar.get(Calendar.MONTH);
            Month month = new Month(firstDayOfMonth, lastDayOfMonth, today, monthIndex, calendar.get(Calendar.YEAR));

            year = calendar.get(Calendar.YEAR);
            if (year == previousYear + 1) {
                monthMap.put(previousYear, monthSparseArray);
                monthSparseArray = new SparseArray<>();
            }
            monthList.add(month);
            monthSparseArray.put(monthIndex, month);
            previousYear = year;
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
        }
        monthMap.put(previousYear, monthSparseArray);
    }

    public void clearEventsInDay(int day, int month, int year) {
        getMonthMap().get(year).get(month).getDay(day - 1).getEventList().clear();
    }

    public int getMonthPagerIndex(int year, int month) {
        int[] output = getDateItems(getStartDate());
        int startYear = output[0];
        int startMonth = output[1];
        int diffYear = year - startYear;
        return 12 * diffYear + month - startMonth;
    }

    public int[] getDateItems(String day) {
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

    ArrayList<Event> getEventListInDay(int day, int month, int year) {
        return getMonthMap().get(year).get(month).getDay(day - 1).getEventList();
    }

    public ArrayList<Event> getSelectedDayEventList() {
        return selectedDayEventList;
    }

    public void setSelectedDayEventList(ArrayList<Event> selectedDayEventList) {
        if (this.getSelectedDayEventList() == null) {
            this.selectedDayEventList = new ArrayList<>();
        }
        this.selectedDayEventList.clear();
        if (selectedDayEventList != null) {
            this.selectedDayEventList.addAll(selectedDayEventList);
        }
    }

    public ArrayList<Month> getMonthList() {
        return this.monthList;
    }

    public ArrayList<String> getMonthNames() {
        return this.monthNames;
    }

    public SparseArray<SparseArray<Month>> getMonthMap() {
        return this.monthMap;
    }

    public Locale getLanguage() {
        return language;
    }

    public void setLanguage(Locale language) {
        this.language = language;
    }

    public int getCalendarType() {
        return calendarType;
    }

    public void setCalendarType(int calendarType) {
        this.calendarType = calendarType;
    }

    public int getLastClickedMonthPosition() {
        return this.lastClickedMonthPosition;
    }

    public void setLastClickedMonthPosition(int lastClickedMonthPosition) {
        this.lastClickedMonthPosition = lastClickedMonthPosition;
    }

    public int getLastClickedDayIndex() {
        return this.lastClickedDayIndex;
    }

    public void setLastClickedDayIndex(int lastClickedDayIndex) {
        this.lastClickedDayIndex = lastClickedDayIndex;
    }

    public boolean isDisableHolidaysAndWeekends() {
        return this.disableHolidaysAndWeekends;
    }

    public void setDisableHolidaysAndWeekends(boolean disableHolidaysAndWeekends) {
        this.disableHolidaysAndWeekends = disableHolidaysAndWeekends;
    }

    public int getCurrentMonthIndex() {
        return currentMonthIndex;
    }

    private void setCurrentMonthIndex(int currentMonthIndex) {
        this.currentMonthIndex = currentMonthIndex;
    }

    public int getCurrentPosition() {
        return this.currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    private String getStartDate() {
        return this.startDate;
    }

    private void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}
