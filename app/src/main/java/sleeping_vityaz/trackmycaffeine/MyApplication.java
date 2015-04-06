package sleeping_vityaz.trackmycaffeine;

import android.app.Application;

/**
 * Created by naja-ox on 2/21/15.
 */
public class MyApplication extends Application {

    public static boolean adsDisabled = true;

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    private static boolean activityVisible;
}