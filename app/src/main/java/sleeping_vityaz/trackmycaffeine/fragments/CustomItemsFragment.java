package sleeping_vityaz.trackmycaffeine.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.daimajia.swipe.util.Attributes;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.melnykov.fab.FloatingActionButton;



import java.util.ArrayList;
import java.util.HashMap;

import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;
import sleeping_vityaz.trackmycaffeine.AddNewCustomCaffeineActivity;
import sleeping_vityaz.trackmycaffeine.MainActivity;
import sleeping_vityaz.trackmycaffeine.MyApplication;
import sleeping_vityaz.trackmycaffeine.R;
import sleeping_vityaz.trackmycaffeine.adapters.CustomRecyclerViewAdapter;
import sleeping_vityaz.trackmycaffeine.adapters.util.DividerItemDecoration;
import sleeping_vityaz.trackmycaffeine.databases.DBTools;

/**
 * Created by naja-ox on 3/18/15.
 */
public class CustomItemsFragment extends Fragment{

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<HashMap<String, String>> mDataSet;

    private FloatingActionButton fab;

    private AdView mAdView;

    DBTools dbTools = null;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_custom_items, container, false);


        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_all);

        dbTools = DBTools.getInstance(this.getActivity());

        recycleViewSetUp(rootView);

        if (MyApplication.adsDisabled==false) {
            setupBannerAd(rootView);
        }

        /* ADVERTISEMENTS */
        /*AdView mAdView = (AdView) rootView.findViewById(R.id.adView); //TODO: remove & change ad_ID before publishing
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(getResources().getString(R.string.hash)).build();
        mAdView.loadAd(adRequest);

        if (MainActivity.mInterstitialAd.isLoaded() && MyApplication.adsDisabled==false) {
            MainActivity.mInterstitialAd.show();
        }*/

        return rootView;
    }

    private void setupBannerAd(View rootView) {
        LinearLayout lin_layout = (LinearLayout) rootView.findViewById(R.id.lin_layout);

        // Create a banner ad. The ad size and ad unit ID must be set before calling loadAd.
        mAdView = new AdView(rootView.getContext());
        mAdView.setAdSize(AdSize.SMART_BANNER);
        mAdView.setAdUnitId(getResources().getString(R.string.test_banner_ad_unit_id));

        AdRequest adRequest = new AdRequest.Builder().addTestDevice(getResources().getString(R.string.hash)).build();
        lin_layout.addView(mAdView);
        mAdView.loadAd(adRequest);

        if (MainActivity.mInterstitialAd.isLoaded() && MyApplication.adsDisabled==false) {
            MainActivity.mInterstitialAd.show();
        }
    }




    private void recycleViewSetUp(View rootView) {
        // Layout Managers:
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        // Item Decorator:
        recyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        recyclerView.setItemAnimator(new FadeInLeftAnimator());

        // Adapter:
        mDataSet = dbTools.getAllCustomRecords();
        mAdapter = new CustomRecyclerViewAdapter(this.getActivity(), mDataSet);
        ((CustomRecyclerViewAdapter) mAdapter).setMode(Attributes.Mode.Single);
        recyclerView.setAdapter(mAdapter);

        /* Listeners */
        recyclerView.setOnScrollListener(onScrollListener);

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddNewCustomCaffeineActivity.class));
            }
        });
        fab.attachToRecyclerView(recyclerView);
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



}
