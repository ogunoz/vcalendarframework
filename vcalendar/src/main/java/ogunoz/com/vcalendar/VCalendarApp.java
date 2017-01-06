package ogunoz.com.vcalendar;

import android.app.Application;
import android.content.Context;

/**
 * Created by ogun on 06/01/2017.
 */

public class VCalendarApp extends Application {

    private static VCalendarApp instance;


    public VCalendarApp() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }
}
