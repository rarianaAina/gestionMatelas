package itu.station.utils;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeUtils {
    public static Date convertToSqlDate(String dateString,String lang) throws ParseException {
        String datePattern = "yyyy-MM-dd";
        if (lang.equals("fr")){
            datePattern = "yyyy-MM-dd";
        }
        SimpleDateFormat format = new SimpleDateFormat(datePattern);
        java.util.Date parsedDate = format.parse(dateString);
        return new Date(parsedDate.getTime());
    }
    public static Date getYesterday(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -1);
        return new Date(calendar.getTimeInMillis());
    }
}
