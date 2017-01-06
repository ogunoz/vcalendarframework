package ogunoz.com.vcalendarframework;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import ogunoz.com.vcalendar.DayListener;
import ogunoz.com.vcalendar.MonthListener;
import ogunoz.com.vcalendar.VCalendar;
import ogunoz.com.vcalendar.models.Event;
import ogunoz.com.vcalendar.models.HolidayEvent;

public class MainActivity extends AppCompatActivity implements DayListener, MonthListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VCalendar.setDayListener(this);

        Event e1 = new Event("XXXXX", "Nullam quis risus eget urna mollis ornare vel eu leo.", Color.RED,
                "23-Jan-2016 16:40", "23-Jan-2016 19:20");
        Event e2 = new Event("XXXXX", "Donec sed odio dui.", Color.BLUE, "23-Jan-2016 16:40", "23-Jan-2016 19:20", "IST");

        ArrayList<Event> sampleEventList = new ArrayList<>();
        sampleEventList.add(e1);
        sampleEventList.add(e2);
        VCalendar.addEvents("14-Dec-2016", sampleEventList);
        VCalendar.addEvent("01-Nov", new Event("Title", "Content"));

        VCalendar.addEvent("01-Jan", new HolidayEvent("New Year", "Happy New Year"));
        VCalendar.addEvent("09-May", new HolidayEvent("Victory Day", "Victory Day for The Red Army"));
        VCalendar.addEvent("05-Jul-2016", new HolidayEvent("Feast", "Ramadan"));
        VCalendar.addEvent("06-Jul-2016", new HolidayEvent("Feast", "Ramadan"));
        VCalendar.addEvent("07-Jul-2016", new HolidayEvent("Feast", "Ramadan"));

        VCalendar.setEventListViewAdapter(new CustomEventAdapter(this));

    }


    @Override
    public void onDayClick(int day, int month, int year) {

    }

    @Override
    public void onDayLongClick(int day, int month, int year) {

    }

    @Override
    public void onMonthSelected(int month, int year) {

    }
}
