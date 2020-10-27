package com.saveblue.saveblueapp;

import android.icu.util.Calendar;
import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimestampHandler {

    // Parse mongo timestamp to dd-MMM-yyyy format
    public static String parseMongoTimestamp(String timestamp) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        SimpleDateFormat df2 = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        try {
            Date mongoDate = df.parse(timestamp);

            assert mongoDate != null;
            return df2.format(mongoDate);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "No Date";
    }

    // Parse selected date to mongo timestamp
    public static String parse2Mongo(String dateStr) {

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        SimpleDateFormat dfMongo = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());

        try {
            Calendar cal = Calendar.getInstance();
            Date date = df.parse(dateStr);

            // Parse values from selected date
            String day = (String) DateFormat.format("dd", date);
            String month = (String) DateFormat.format("MM", date);
            String year = (String) DateFormat.format("yyyy", date);

            // Set values in calendar, subtract 1 because January is 0
            cal.set(Calendar.DATE, Integer.parseInt(day));
            cal.set(Calendar.MONTH, Integer.parseInt(month) - 1);
            cal.set(Calendar.YEAR, Integer.parseInt(year));

            System.out.println(dfMongo.format(cal.getTime()));
            assert date != null;
            return dfMongo.format(cal.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dfMongo.format(new Date());
    }
}
