package sleeping_vityaz.trackmycaffeine.util;


import android.app.Activity;
import android.util.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;

public class Calculations extends Activity {




    public static double calculateConcentration(int t, int start, int duration) {
        //final int duration = 24 * 3600 * 1000; // 24 hours as milliseconds
        final int halfLife = (int) (4.5 * 3600 * 1000); // half-life of caffeine in the body - 5 hours as milliseconds
        final int absorpLife = (int) (3600 * 1000); // absorption rate - 45 minutes as milliseconds
        final double k1 = (Math.log(2.0))/halfLife;
        final double k2 = (Math.log(0.05))/absorpLife;
        final double bloodVolume = 0.007; // liters of blood per kg of mass in the typical human 0.007 L/kg

        double a = 120; // amount of caffeine consumed
        double mass = 75; // kg


        double A = a/duration;

        int t1 = start;
        int t2 = t1 + duration;

        double sum = 0;

        if (t > t1) {
            /*sum = (Math.pow(Math.E, (k2-k1)*(t - t2)))/(k2-k1) + (Math.pow(Math.E, -k1*(t-t2)))/k1;
            sum = sum - ((Math.pow(Math.E, ((k2-k1)*(t - t1))))/(k2-k1) + (Math.pow(Math.E, -k1*(t-t1)))/k1);*/
            sum += a*(Math.exp(-(t-start)*k1));
        }else{
            Log.e("CALCULATIONS", "Caffeine hasn't been consumed yet. Try again.");
        }
        return sum;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


}
