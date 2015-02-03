package sleeping_vityaz.trackmycaffeine.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;
import com.daimajia.swipe.util.Attributes;
import com.github.lzyzsd.circleprogress.ArcProgress;

import java.util.ArrayList;
import java.util.HashMap;

import sleeping_vityaz.trackmycaffeine.databases.DBTools;
import sleeping_vityaz.trackmycaffeine.R;
import sleeping_vityaz.trackmycaffeine.adapters.RecyclerViewAdapter;
import sleeping_vityaz.trackmycaffeine.adapters.util.DividerItemDecoration;
import sleeping_vityaz.trackmycaffeine.util.Calculations;


public class TrackerFragment extends Fragment {


    public static final String TAG = "TRACK-FRAGMENT";

    private TextView date, start, duration, results;

    private Context mContext = this.getActivity();
    private ArcProgress arcProgress;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<HashMap<String, String>> mDataSet;


    DBTools dbTools = null;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_tracker, container, false);

        dbTools = DBTools.getInstance(this.getActivity());

        findViewsById(rootView);

        recyclyViewSetUp(rootView);

        final int i_date = 21*3600*1000, i_start = 20*3600*1000;

        double concentration = Calculations.calculateConcentration(i_date, i_start, 0);
        results.setText("Concentration of caffeine: " + concentration);
        alert("" + concentration);
        arcProgress.setProgress((int) 0.0);

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
        arcProgress = (ArcProgress) rootView.findViewById(R.id.arc_progress);
        date = (TextView) rootView.findViewById(R.id.tv_date);
        start = (TextView) rootView.findViewById(R.id.tv_start);
        duration = (TextView) rootView.findViewById(R.id.tv_duration);
        results = (TextView) rootView.findViewById(R.id.tv_results);

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
