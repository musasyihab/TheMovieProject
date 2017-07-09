package com.musasyihab.themovieproject.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by musasyihab on 7/9/17.
 */

public class Helper {

    public static String printDate(String date) {
        return formatDate("yyyy-MM-dd", date, "MMMM, d yyyy", null, null);
    }

    public static String getYear(String date) {
        return formatDate("yyyy-MM-dd", date, "yyyy", null, null);
    }

    public static String formatDate(String dateFormat, String date, String toFormat, Locale fromLocale, Locale toLocale) {
        String formatted = "";
        DateFormat formatter = fromLocale == null ? new SimpleDateFormat(dateFormat) : new SimpleDateFormat(dateFormat, fromLocale);
        try {
            Date dateStr = formatter.parse(date);
            formatted = formatter.format(dateStr);
            Date formatDate = formatter.parse(formatted);
            formatter = toLocale == null ? new SimpleDateFormat(toFormat) : new SimpleDateFormat(toFormat, toLocale);
            formatted = formatter.format(formatDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formatted;
    }
}
