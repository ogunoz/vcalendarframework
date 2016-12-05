package ogunoz.com.vcalendar.models;

import java.util.ArrayList;

/**
 * Created by Ogün Öz on 01/12/16.
 */

public class Day {

    private ArrayList<Event> eventList;
    private boolean isRead = true;
    private boolean isClicked = false;

    public Day(ArrayList<Event> eventList, boolean isRead) {
        setEventList(eventList);
        setRead(isRead);
    }

    public ArrayList<Event> getEventList() {
        return eventList;
    }

    public void setEventList(ArrayList<Event> eventList) {
        this.eventList = eventList;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }
}
