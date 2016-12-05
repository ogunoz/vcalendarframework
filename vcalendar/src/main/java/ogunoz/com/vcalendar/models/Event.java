package ogunoz.com.vcalendar.models;

import android.graphics.Color;

/**
 * Created by Ogün Öz on 25/11/16.
 */

public class Event {

    private int color = Color.GRAY;
    private String title;
    private String text;
    private String eventStartTime = "00:00";
    private String eventEndTime = "23:59";
    private String eventPlace = "-";

    public Event(String title, String text, int color, String eventStartTime, String eventEndTime,
                 String eventPlace) {
        setTitle(title);
        setText(text);
        setColor(color);
        setEventStartTime(eventStartTime);
        setEventEndTime(eventEndTime);
        setEventPlace(eventPlace);
    }

    public Event(String title, String text, int color, String eventStartTime, String eventEndTime) {
        setTitle(title);
        setText(text);
        setColor(color);
        setEventStartTime(eventStartTime);
        setEventEndTime(eventEndTime);
    }

    public Event(String title, String text, int color, String eventPlace) {
        setTitle(title);
        setText(text);
        setColor(color);
        setEventPlace(eventPlace);
    }

    public Event(String title, String text, int color) {
        setTitle(title);
        setText(text);
        setColor(color);
    }

    public Event(String title, String text, String eventStartTime, String eventEndTime,
                 String eventPlace) {
        setTitle(title);
        setText(text);
        setEventStartTime(eventStartTime);
        setEventEndTime(eventEndTime);
        setEventPlace(eventPlace);
    }

    public Event(String title, String text, String eventStartTime, String eventEndTime) {
        setTitle(title);
        setText(text);
        setEventStartTime(eventStartTime);
        setEventEndTime(eventEndTime);
    }

    public Event(String title, String text, String eventPlace) {
        setTitle(title);
        setText(text);
        setEventPlace(eventPlace);
    }

    public Event(String title, String text) {
        setTitle(title);
        setText(text);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getEventStartTime() {
        return eventStartTime;
    }

    public void setEventStartTime(String eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    public String getEventEndTime() {
        return eventEndTime;
    }

    public void setEventEndTime(String eventEndTime) {
        this.eventEndTime = eventEndTime;
    }

    public String getEventPlace() {
        return eventPlace;
    }

    public void setEventPlace(String eventPlace) {
        this.eventPlace = eventPlace;
    }
}
