package sleeping_vityaz.trackmycaffeine.util;


import android.app.Activity;
import android.util.Log;

import java.sql.Date;

public class Calculations extends Activity {

    final private int duration = 24 * 3600 * 1000; // 24 hours as milliseconds
    final private int halfLife = 5 * 3600 * 1000; // half-life of caffeine in the body - 5 hours as milliseconds
    final private int absorpLife = (int) (3600 * 1000 * 0.75); // absorption rate - 45 minutes as milliseconds
    final private double k1 = Math.log(2) / halfLife;
    final private double k2 = Math.log(0.05) / absorpLife;
    final double bloodVolume = 70e-3; // liters of blood per kg of mass in the typical human 0.007 L/kg

    double a = 120; // amount of caffeine consumed
    double mass = 75; // kg
    int start = (int) System.currentTimeMillis();
    int end = start - duration;

    private double A = a/duration;

    int t1 = (int) System.currentTimeMillis();
    int t2 = t1 + 100000;


    public double calculateConcentration(int t, int start, int end) {
        double sum = 0;

        if (t > t1) {
            sum = Math.pow(Math.E, (k2-k1)*(t - t2))/(k2-k1) - Math.pow(Math.E, -k1*(t-t2))/k1;
            sum = sum - (Math.pow(Math.E, (k2-k1)*(t - t1))/(k2-k1) - Math.pow(Math.E, -k1*(t-t1))/k1);
        }else{
            Log.e("CALCULATIONS", "Caffeine hasn't been consumed yet. Try again.");
        }
        return sum*A/(mass * bloodVolume);
    }


}
