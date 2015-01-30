package sleeping_vityaz.trackmycaffeine.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;

import sleeping_vityaz.trackmycaffeine.R;
import sleeping_vityaz.trackmycaffeine.circularcounter.CircularCounter;


public class TrackerFragment extends Fragment {

   // private CircularCounter meter;
    private CircularCounter meter;
    private String[] colors;
    private Handler handler;
    private Runnable r;
    private long startTime = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_tracker, container, false);

        colors = getResources().getStringArray(R.array.colors);

        meter = (CircularCounter) rootView.findViewById(R.id.meter);

        meter.setFirstWidth(getResources().getDimension(R.dimen.first))
                .setFirstColor(getResources().getColor(R.color.primary_dark))

                .setSecondWidth(getResources().getDimension(R.dimen.second))
                .setSecondColor(getResources().getColor(R.color.accent))


                .setBackgroundColor(getResources().getColor(R.color.white));



        handler = new Handler();
        r = new Runnable() {
            int currV = 0;
            boolean go = true;

            public void run() {
                if (currV == 60 && go)
                    go = false;
               /* else if (currV == -60 && !go)
                    go = true;*/

                long millis = System.currentTimeMillis() - startTime;
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;

                if (go)
                    currV++;

                meter.setValues(seconds, seconds, 0);
                handler.postDelayed(this, 500);
            }
        };


        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        handler.postDelayed(r, 500);
    }

    @Override
    public void onPause(){
        super.onPause();
        handler.removeCallbacks(r);
    }

}
