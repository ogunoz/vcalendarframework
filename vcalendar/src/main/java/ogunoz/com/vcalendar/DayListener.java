package ogunoz.com.vcalendar;

/**
 * Created by Ogün Öz on 01/12/16.
 */

public interface DayListener {

    void onDayClick(int day, int month, int year);
    void onDayLongClick(int day, int month, int year);
}
