package sleeping_vityaz.trackmycaffeine.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;
import com.daimajia.swipe.util.Attributes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.lang.Runnable;
import java.util.Timer;
import java.util.TimerTask;

import sleeping_vityaz.trackmycaffeine.databases.DBTools;
import sleeping_vityaz.trackmycaffeine.R;
import sleeping_vityaz.trackmycaffeine.adapters.RecyclerViewAdapter;
import sleeping_vityaz.trackmycaffeine.adapters.util.DividerItemDecoration;
import sleeping_vityaz.trackmycaffeine.util.Calculations;
import sleeping_vityaz.trackmycaffeine.util.CommonConstants;
import sleeping_vityaz.trackmycaffeine.util.Util;


public class TrackerFragment extends Fragment {


    public static final String TAG = "TRACK-FRAGMENT";

    private final static int UPDATE = 0;

    private TextView tv_total, tv_mg, tv_today,
            tv_effects_desc_text, tv_effects_desc_time,
            tv_rate_num, tv_rate_units,
            tv_left_num, tv_left_desc_text;

    private ImageView iv_rate_indicator;

    private Context mContext = this.getActivity();

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<HashMap<String, String>> mDataSet;

    private Calendar calendar;
    private DateFormat dateFormat;

    private double caffeineConsumedToday;


    DBTools dbTools = null;


    public static TrackerFragment newInstance(int position) {
        TrackerFragment f = new TrackerFragment();
        Bundle b = new Bundle();
        b.putInt("position", position);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_tracker, container, false);

        dbTools = DBTools.getInstance(this.getActivity());

        findViewsById(rootView);

        recyclyViewSetUp(rootView);

        calendar = Calendar.getInstance();
        dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());

        final int i_date = 21*3600*1000, i_start = 20*3600*1000;

        //final double concentration = Calculations.calculateConcentration(i_date, i_start, 0);
        //results.setText("Concentration of caffeine: " + concentration);
        //alert("" + concentration);

        //final double concentration = 0.0;
        ArrayList<HashMap<String, String>> allRecordsOnThisDate = dbTools.getAllRecordsOnThisDate(Util.convertDateForDB(dateFormat.format(calendar.getTime())));
        for (HashMap<String, String> hashMap : allRecordsOnThisDate){
            caffeineConsumedToday += Double.parseDouble(hashMap.get(CommonConstants.CAFFEINE_MASS));
        }
        alert("Caffeine Consumed Today "+caffeineConsumedToday);
        tv_total.setText("" + ((int) Calculations.round(caffeineConsumedToday, 0)));

        Timer timer = new Timer();
        MyTimerTask timerTask = new MyTimerTask();
        timer.schedule(timerTask, 0, 1000);

        return rootView;
    }

    private void recyclyViewSetUp(View rootView) {
        // Layout Managers:
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        // Item Decorator:
        recyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        recyclerView.setItemAnimator(new FadeInLeftAnimator());

        // Adapter:
        mDataSet = dbTools.getAllRecords();
        mAdapter = new RecyclerViewAdapter(this.getActivity(), mDataSet);
        ((RecyclerViewAdapter) mAdapter).setMode(Attributes.Mode.Single);
        recyclerView.setAdapter(mAdapter);

        /* Listeners */
        recyclerView.setOnScrollListener(onScrollListener);
    }

    /**
     * Substitute for our onScrollListener for RecyclerView
     */
    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            //Log.e("ListView", "onScrollStateChanged");
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            // Could hide open views here if you wanted. //
        }
    };

    private void findViewsById(View rootView) {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        tv_total = (TextView) rootView.findViewById(R.id.tv_total);
        tv_mg = (TextView) rootView.findViewById(R.id.tv_mg);
        tv_today = (TextView) rootView.findViewById(R.id.tv_today);
        tv_effects_desc_text = (TextView) rootView.findViewById(R.id.tv_effects_desc_text);
        tv_effects_desc_time = (TextView) rootView.findViewById(R.id.tv_effects_desc_time);
        tv_rate_num = (TextView) rootView.findViewById(R.id.tv_rate_num);
        tv_rate_units = (TextView) rootView.findViewById(R.id.tv_rate_units);
        tv_left_num = (TextView) rootView.findViewById(R.id.tv_left_num);
        tv_left_desc_text = (TextView) rootView.findViewById(R.id.tv_left_desc_text);
        iv_rate_indicator = (ImageView) rootView.findViewById(R.id.iv_rate_indicator);

    }

    @Override
    public void onResume(){
        super.onResume();
        tv_total.setText(""+((int) Calculations.round(caffeineConsumedToday, 0)));
    }

    @Override
    public void onPause(){
        super.onPause();
        //handler.removeCallbacks(r);
    }

    private void alert(String s) {
        Log.d(TAG, s);
    }

    private final Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            final int what = msg.what;
            switch(what) {
                case UPDATE: updateRate(); break;
            }
        }
    };

    private void updateRate() {
        double concentration = 0.0;
        caffeineConsumedToday = 0;
        calendar = Calendar.getInstance();
        ArrayList<HashMap<String, String>> allRecordsOnThisDate = dbTools.getAllRecordsOnThisDate(Util.convertDateForDB(dateFormat.format(calendar.getTime())));
        //for (HashMap<String, String> hashMap : allRecordsOnThisDate){
        for (int i = 0; i < allRecordsOnThisDate.size(); i++) {
            caffeineConsumedToday += Double.parseDouble(allRecordsOnThisDate.get(i).get(CommonConstants.CAFFEINE_MASS));
            // caffeineToStart, start, duration, timeOfInterest
            double caffeineToStart = Double.parseDouble(allRecordsOnThisDate.get(i).get(CommonConstants.CAFFEINE_MASS));
            int start = Util.timeToMilliseconds(allRecordsOnThisDate.get(i).get(CommonConstants.TIME_STARTED));
            int duration = 0;//Long.parseLong(allRecordsOnThisDate.get(i).get(CommonConstants.DURATION))*60*1000;
            int timeOfInterest = Util.stripeDateReturnMilliseconds(calendar.getTimeInMillis());

            //alert("start: "+start);
            //alert("duration: "+duration);
            //alert("current: "+calendar.getTimeInMillis());
            concentration += Calculations.calcConcentration(caffeineToStart, start, duration, timeOfInterest);
        }
        tv_total.setText("" + ((int) Calculations.round(caffeineConsumedToday, 0)));
        tv_rate_num.setText("" + (int) concentration);
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {

            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        myHandler.sendEmptyMessage(UPDATE);
                    }
                });
            }
        }

    }

}
