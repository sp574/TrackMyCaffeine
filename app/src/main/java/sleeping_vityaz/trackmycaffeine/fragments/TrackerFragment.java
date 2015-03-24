package sleeping_vityaz.trackmycaffeine.fragments;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
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
import java.util.prefs.Preferences;

import sleeping_vityaz.trackmycaffeine.MainActivity;
import sleeping_vityaz.trackmycaffeine.MyApplication;
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
    private ArrayList<HashMap<String, String>> allRecordsOnThisDate;

    private Calendar calendar;
    private Calendar calPrev;
    private DateFormat dateFormat;

    private double caffeineConsumedToday;
    private int effectsBy;
    private double prevConcentration;

    private SharedPreferences settings;
    private boolean notifications;
    private String threshold;
    private String units;

    NotificationManager notificationManager;
    private int LOW_ID = 0;
    private int ABOVE_ID = 1;
    private boolean showNotification = true;


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



        // Restore preferences
        if (getActivity()!=null) { settings = PreferenceManager.getDefaultSharedPreferences(getActivity());}
        notifications = settings.getBoolean("notif_checkbox_pref", true);
        threshold = settings.getString("threshold_pref", "400");
        units = settings.getString("units_drinks_pref", "");

        alert("NOTIFICATIONS: "+notifications+" UNITS: "+units);

        if (getActivity()!=null) {
           notificationManager = (NotificationManager) getActivity().getSystemService(mContext.NOTIFICATION_SERVICE);
        }
        findViewsById(rootView);

        if (mContext!=null) {
            // Store our shared preference
            SharedPreferences sp = mContext.getSharedPreferences("OURINFO", mContext.MODE_PRIVATE);
            SharedPreferences.Editor ed = sp.edit();
            ed.putBoolean("active", true);
            ed.commit();
        }

        calendar = Calendar.getInstance();
        calPrev = Calendar.getInstance();
        calPrev.add(Calendar.DATE, -1);
        dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());

        prevConcentration = 0.0;

        allRecordsOnThisDate = dbTools.getAllRecordsOnThisDate(Util.convertDateForDB(dateFormat.format(calendar.getTime())),
                Util.convertDateForDB(dateFormat.format(calPrev.getTime())));


        recyclyViewSetUp(rootView);


        for (HashMap<String, String> hashMap : allRecordsOnThisDate){
            caffeineConsumedToday += Double.parseDouble(hashMap.get(CommonConstants.CAFFEINE_MASS));
        }
        alert("Caffeine Consumed Today "+caffeineConsumedToday);
        tv_total.setText("" + ((int) Calculations.round(caffeineConsumedToday, 0)));
        int left_num = (int) Calculations.round((Integer.parseInt(threshold)-caffeineConsumedToday), 0);
        if (left_num>=0){
            tv_left_num.setText(""+left_num);
            tv_left_desc_text.setText(" left");
        }else{
            tv_left_num.setText(""+(-1*left_num));
            tv_left_desc_text.setText(" above");
        }

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
        mDataSet = allRecordsOnThisDate;//dbTools.getAllRecords();
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
        MyApplication.activityResumed();
        tv_total.setText(""+((int) Calculations.round(caffeineConsumedToday, 0)));
    }

    @Override
    public void onPause(){
        super.onPause();
        MyApplication.activityPaused();
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
        // Restore preferences
        if (getActivity()!=null) { settings = PreferenceManager.getDefaultSharedPreferences(getActivity()); }
        notifications = settings.getBoolean("notif_checkbox_pref", true);
        threshold = settings.getString("threshold_pref", "400");
        units = settings.getString("units_drinks_pref", "");

        //alert("NOTIFICATIONS: "+notifications+" UNITS: "+units);

        double concentration = 0.0;
        caffeineConsumedToday = 0;
        effectsBy = 0;
        calendar = Calendar.getInstance();
        calPrev = Calendar.getInstance();
        calPrev.add(Calendar.DATE, -1);
        ArrayList<HashMap<String, String>> allRecordsOnThisDate = dbTools.getAllRecordsOnThisDate(Util.convertDateForDB(dateFormat.format(calendar.getTime())),
                                                                                                  Util.convertDateForDB(dateFormat.format(calPrev.getTime())));

        for (int i = 0; i < allRecordsOnThisDate.size(); i++) {
            //alert("FROM ARRAYLIST "+allRecordsOnThisDate.get(i).get(CommonConstants.DATE_CREATED));
            //alert("FROM CALENDAR "+Util.convertDateForDB(dateFormat.format(calendar.getTime())));
            if (allRecordsOnThisDate.get(i).get(CommonConstants.DATE_CREATED).equals(Util.convertDateForDB(dateFormat.format(calendar.getTime())))) { //only sum up caffeine if consumed today
                caffeineConsumedToday += Double.parseDouble(allRecordsOnThisDate.get(i).get(CommonConstants.CAFFEINE_MASS));
            }
            // caffeineToStart, start, duration, timeOfInterest
            double caffeineToStart = Double.parseDouble(allRecordsOnThisDate.get(i).get(CommonConstants.CAFFEINE_MASS));
            int start = Util.timeToMilliseconds(allRecordsOnThisDate.get(i).get(CommonConstants.TIME_STARTED));
            int duration = Integer.parseInt(allRecordsOnThisDate.get(i).get(CommonConstants.DURATION))*60*1000;
            int timeOfInterest = 0;
            int effectsDelay = duration+6*3600*1000;
            if (allRecordsOnThisDate.get(i).get(CommonConstants.DATE_CREATED).equals(Util.convertDateForDB(dateFormat.format(calendar.getTime())))) {

                timeOfInterest = Util.stripeDateReturnMilliseconds(calendar.getTimeInMillis());
                if (effectsBy<(start+effectsDelay)){ // date is today
                    effectsBy = start+effectsDelay; // check later, might need to correct this time later
                }
            } else{ // Consumed caffeine yesterday
                int _24hrsInMilliseconds = 24*3600*1000;
                timeOfInterest = _24hrsInMilliseconds+Util.stripeDateReturnMilliseconds(calendar.getTimeInMillis());
                if (start > 18*3600*1000 && effectsBy<(start+effectsDelay-24*3600*1000)){ // date is today, before afternoon
                    effectsBy = start+effectsDelay-24*3600*1000;
                }
            }

            // if effectsBy < time now, effectsBy = 0
            if (effectsBy < Util.stripeDateReturnMilliseconds(calendar.getTimeInMillis())){
                effectsBy = 0;
            }

            alert("effectsBy = "+effectsBy);
            alert("time now = "+Util.stripeDateReturnMilliseconds(calendar.getTimeInMillis()));

            concentration += Calculations.calcConcentration(caffeineToStart, start, duration, timeOfInterest);

        }
        tv_total.setText("" + ((int) Calculations.round(caffeineConsumedToday, 0)));
        tv_rate_num.setText("" + (int) concentration);
        if (effectsBy!=0) {
            tv_effects_desc_time.setText(Util.convertTimeForDisplay(effectsBy));
        }else{
            tv_effects_desc_text.setText("Get some caffeine");
            tv_effects_desc_time.setText("in you");
        }
        if (prevConcentration < concentration){ // rising rate
            iv_rate_indicator.setVisibility(View.VISIBLE);
            if (getActivity()!=null) iv_rate_indicator.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.up_arrow));
        }else if (prevConcentration==concentration){ // stable
            iv_rate_indicator.setVisibility(View.INVISIBLE);
        }else{ // falling rate
            iv_rate_indicator.setVisibility(View.VISIBLE);
            if (getActivity()!=null) iv_rate_indicator.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.down_arrow));
        }
        int left_num = (int) Calculations.round((Integer.parseInt(threshold)-caffeineConsumedToday), 0);
        if (left_num>=0){
            tv_left_num.setText(""+left_num);
            tv_left_desc_text.setText(" left");
        }else{
            tv_left_num.setText(""+(-1*left_num));
            tv_left_desc_text.setText(" above");
        }

        if (notifications) { createNotification(); }

        prevConcentration = concentration;
    }

    private void createNotification() {
        if (getActivity()!=null && effectsBy==0 && showNotification && !MyApplication.isActivityVisible()) {
            Intent intent = new Intent(this.getActivity(), AddNewCaffeineFragment.class);
            PendingIntent pIntent = PendingIntent.getActivity(this.getActivity(), 0, intent, 0);

            // build notification
            // the addAction re-use the same intent to keep the example short
            Notification n = new Notification.Builder(this.getActivity())
                    .setContentTitle("Feeling sluggish?")
                    .setContentText("Your caffeine levels are too low!")
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentIntent(pIntent)
                    .setAutoCancel(true).build();

            // hide the notification after its selected
            n.flags |= Notification.FLAG_AUTO_CANCEL;

            notificationManager.notify(LOW_ID, n);
            showNotification = false;
        }else if (effectsBy!=0 || MyApplication.isActivityVisible()){
            notificationManager.cancelAll();
            showNotification = true;
        }
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
