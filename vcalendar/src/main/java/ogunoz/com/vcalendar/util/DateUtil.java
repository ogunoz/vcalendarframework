package ogunoz.com.vcalendar.util;

import android.support.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ogün OZ on 12/08/16.
 * 15:42:42
 */
public class DateUtil {

    private static final String SIMPLE_DATE_PATTERN = "dd-MMM-yyyy";

    @Nullable
    public static Date getDateFromString(String date) {
        SimpleDateFormat format = new SimpleDateFormat(SIMPLE_DATE_PATTERN, Locale.US);
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(format.parse(date));
            return cal.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
