package ru.tcgeo.application.utils;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by artem on 12.04.18.
 */

public class CommonUtils {
    public static String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        int mounth = calendar.get(Calendar.MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        return String.format(Locale.ENGLISH, "%02d_%02d_%02d_%02d_%02d", mounth + 1, day, hour, minute, second);
    }

    public static String getCurrentTimeShort() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        int mounth = calendar.get(Calendar.MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        return /*m_Map.ps.m_name +*/ String.format(Locale.ENGLISH, "%02d_%02d_%02d_%02d", mounth + 1, day, hour, minute);
//                                 String.format(Locale.ENGLISH, "%02d_%02d_%02d_%02d_%02d", mounth+1, day, hour, minute, second);
    }


    public static String getTime(String currentTimeShort) {
        String result = "";
        if (currentTimeShort != null && !currentTimeShort.isEmpty()) {
            String[] parts = currentTimeShort.split("_");
            if (parts.length == 5) {
                result = parts[2] + "h" + parts[3] + "m";
            }
        }
        return result;
    }
}
