package ogunoz.com.vcalendar.util;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by Ogün OZ on 10/08/16.
 * 10:30:30
 */
public class DeviceScreenUtil {
    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

}
