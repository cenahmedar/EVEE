package com.eventapp.helpers;

import android.annotation.SuppressLint;
import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@SuppressLint("SimpleDateFormat")
public class Functions {
    public static boolean isNullOrEmpty(final String s) {
        return s == null || s.trim().isEmpty();
    }


    public static String getDay(String string_date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        try {
            return (String) DateFormat.format("dd", dateFormat.parse(string_date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;

    }

    public static String getMonthName(String string_date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        try {
            return new SimpleDateFormat("MMM").format(Objects.requireNonNull(dateFormat.parse(string_date)).getTime()).toUpperCase();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String dateTimeToInt(Date date) {
        return new SimpleDateFormat("yyyyMMddHHmm").format(date).toUpperCase();
    }
}
