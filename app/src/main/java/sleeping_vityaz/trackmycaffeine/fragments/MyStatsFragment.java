package sleeping_vityaz.trackmycaffeine.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import sleeping_vityaz.trackmycaffeine.MainActivity;
import sleeping_vityaz.trackmycaffeine.MyApplication;
import sleeping_vityaz.trackmycaffeine.R;

/**
 * Created by naja-ox on 1/29/15.
 */
public class MyStatsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_my_stats, container, false);

        /* ADVERTISEMENTS */
        AdView mAdView = (AdView) rootView.findViewById(R.id.adView); //TODO: remove & change ad_ID before publishing
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(getResources().getString(R.string.hash)).build();
        mAdView.loadAd(adRequest);

        if (MainActivity.mInterstitialAd.isLoaded() && MyApplication.adsDisabled==false) {
            MainActivity.mInterstitialAd.show();
        }

        return rootView;

    }
}
