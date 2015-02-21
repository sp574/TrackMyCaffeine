package sleeping_vityaz.trackmycaffeine.util;

import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by naja-ox on 2/3/15.
 */
public class Util {

    public static String convertDateForDB(String oldDateFormatString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyy");
            Date d = sdf.parse(oldDateFormatString);
            sdf.applyPattern("yyyy-MM-dd");
            return sdf.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String convertTimeForDB(String oldTimeFormatString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            Date d = sdf.parse(oldTimeFormatString);
            sdf.applyPattern("HH:mm");
            return sdf.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String convertDateFromDB(String oldDateFormatString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date d = sdf.parse(oldDateFormatString);
            sdf.applyPattern("MMM dd, yyy");
            return sdf.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String convertTimeFromDB(String oldTimeFormatString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date d = sdf.parse(oldTimeFormatString);
            sdf.applyPattern("hh:mm a");
            return sdf.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int timeToMilliseconds(String oldTimeFormatString) {
        try {
            DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm");
            DateTime d = dtf.parseDateTime(oldTimeFormatString);
            return d.getHourOfDay() * 3600 * 1000 + d.getMinuteOfHour() * 60 * 1000;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int stripeDateReturnMilliseconds(long oldTimeFormatString) {
        try {
            DateTime d = new DateTime(oldTimeFormatString);
            return d.getHourOfDay() * 3600 * 1000 + d.getMinuteOfHour() * 60 * 1000 + d.getSecondOfMinute() * 1000;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String convertTimeForDisplay(int timeInts) {

        if (timeInts > 24*3600*1000){
            timeInts = timeInts - 24*3600*1000;
        }
        int h = timeInts / (3600 * 1000);
        int m = (timeInts%3600000)/(1000*60);
        //Log.d("Util","timeInts="+timeInts+" h="+h+" m="+m);
        if (m < 30 && h < 12) return h + "am";
        else if (m < 30 && h >= 12){ if (h==12) return h+"pm"; else return (h-12) + "pm";}
        else if (m > 30 && h < 12){ if (h==11) return (h+1)+"pm"; else return (h + 1) + "am";}
        else if (m > 30 && h >= 12){ if (h==23) return (h+1-12)+"am"; else return (h + 1-12) + "pm";}
        else return ""; // never gets here
    }

    public static String adjustCaffeineMass(String density, String volume) {
        return "" + Calculations.round((Double.parseDouble(density) * Double.parseDouble(volume)), 1);
    }


}
