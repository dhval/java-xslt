package com.dhval.utils;

import com.dhval.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    private static final Logger LOG = LoggerFactory.getLogger(DateUtils.class);
    static DateFormat formatYYYYMMMDD = new SimpleDateFormat( "yyyy-MM-dd" );

    static {
        formatYYYYMMMDD.setLenient(false);
    }

    public static String format(Date date) {
        if (date == null)
            return  "";
        return formatYYYYMMMDD.format(date);
    }

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

}
