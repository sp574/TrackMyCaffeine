package sleeping_vityaz.trackmycaffeine.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.implments.SwipeItemMangerImpl;
import com.github.lzyzsd.circleprogress.ArcProgress;

import sleeping_vityaz.trackmycaffeine.R;
import sleeping_vityaz.trackmycaffeine.adapters.ListViewAdapter;
import sleeping_vityaz.trackmycaffeine.circularcounter.CircularCounter;
import sleeping_vityaz.trackmycaffeine.util.Calculations;


public class TrackerFragment extends Fragment {


    public static final String TAG = "TRACK-FRAGMENT";

    private TextView date, start, duration, results;

   // private CircularCounter meter;
    /*private CircularCounter meter;
    private String[] colors;
    private Handler handler;
    private Runnable r;
    private long startTime = 0;*/


    private ListView mListView;
    private ListViewAdapter mAdapter;
    private Context mContext = this.getActivity();
    private ArcProgress arcProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_tracker, container, false);

        mListView = (ListView) rootView.findViewById(R.id.listview);
        arcProgress = (ArcProgress) rootView.findViewById(R.id.arc_progress);

        mAdapter = new ListViewAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mAdapter.setMode(SwipeItemMangerImpl.Mode.Single);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((SwipeLayout)(mListView.getChildAt(position - mListView.getFirstVisiblePosition()))).open(true);
            }
        });

        mListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e("ListView", "onItemSelected:" + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.e("ListView", "onNothingSelected:");
            }
        });

        date = (TextView) rootView.findViewById(R.id.tv_date);
        start = (TextView) rootView.findViewById(R.id.tv_start);
        duration = (TextView) rootView.findViewById(R.id.tv_duration);
        results = (TextView) rootView.findViewById(R.id.tv_results);

        final int i_date = 21*3600*1000, i_start = 20*3600*1000;

        double concentration = Calculations.calculateConcentration(i_date, i_start, 0);
        results.setText("Concentration of caffeine: " + concentration);
        alert("" + concentration);
        arcProgress.setProgress((int) concentration);
       /* colors = getResources().getStringArray(R.array.colors);
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
               *//* else if (currV == -60 && !go)
                    go = true;*//*

                long millis = System.currentTimeMillis() - startTime;
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;

                if (go)
                    currV++;

                meter.setValues(seconds, 0, 0);
                handler.postDelayed(this, 500);
            }
        };*/


        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        //handler.postDelayed(r, 500);
    }

    @Override
    public void onPause(){
        super.onPause();
        //handler.removeCallbacks(r);
    }

    private void alert(String s) {
        Log.d(TAG, s);
    }

}
