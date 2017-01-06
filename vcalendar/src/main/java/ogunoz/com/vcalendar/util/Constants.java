package ogunoz.com.vcalendar.util;

import java.util.HashMap;

/**
 * Created by ogun on 02/01/2017.
 */

public class Constants {

    private static HashMap<Integer, String> monthNameMap;


    public static String getMonthName(int monthEnum) {
        if (monthNameMap == null) {
            createMonthNameMap();
        }
        return monthNameMap.get(monthEnum);
    }

    private static void createMonthNameMap() {
        monthNameMap = new HashMap<>();
        monthNameMap.put(100, "Jan");
        monthNameMap.put(101, "Feb");
        monthNameMap.put(102, "Mar");
        monthNameMap.put(103, "Apr");
        monthNameMap.put(104, "May");
        monthNameMap.put(105, "Jun");
        monthNameMap.put(106, "Jul");
        monthNameMap.put(107, "Aug");
        monthNameMap.put(108, "Sep");
        monthNameMap.put(109, "Oct");
        monthNameMap.put(110, "Nov");
        monthNameMap.put(111, "Dec");
    }
}
