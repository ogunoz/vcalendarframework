package ogunoz.com.vcalendar.models;

import android.graphics.Color;

/**
 * Created by Ogün Öz on 01/12/16.
 */

public class HolidayEvent extends Event {
    public HolidayEvent(String title, String text, int color, String eventStartTime, String eventEndTime, String eventPlace) {
        super(title, text, color, eventStartTime, eventEndTime, eventPlace);
    }

    public HolidayEvent(String title, String text, int color, String eventStartTime, String eventEndTime) {
        super(title, text, color, eventStartTime, eventEndTime);
    }

    public HolidayEvent(String title, String text, int color, String eventPlace) {
        super(title, text, color, eventPlace);
    }

    public HolidayEvent(String title, String text, int color) {
        super(title, text, color);
    }

    public HolidayEvent(String title, String text, String eventStartTime, String eventEndTime, String eventPlace) {
        super(title, text, Color.GRAY, eventStartTime, eventEndTime, eventPlace);
    }

    public HolidayEvent(String title, String text, String eventStartTime, String eventEndTime) {
        super(title, text, Color.GRAY, eventStartTime, eventEndTime);
    }

    public HolidayEvent(String title, String text, String eventPlace) {
        super(title, text, Color.GRAY, eventPlace);
    }

    public HolidayEvent(String title, String text) {
        super(title, text, Color.GRAY);
    }
}
