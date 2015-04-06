package sleeping_vityaz.trackmycaffeine.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import sleeping_vityaz.trackmycaffeine.MainActivity;
import sleeping_vityaz.trackmycaffeine.MyApplication;
import sleeping_vityaz.trackmycaffeine.R;

/**
 * Created by naja-ox on 1/29/15.
 */
public class MyStatsFragment extends Fragment {


    private AdView mAdView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_my_stats, container, false);


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
}
