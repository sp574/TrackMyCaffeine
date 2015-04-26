package sleeping_vityaz.trackmycaffeine.fragments;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonRectangle;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import sleeping_vityaz.trackmycaffeine.MyApplication;
import sleeping_vityaz.trackmycaffeine.R;
import sleeping_vityaz.trackmycaffeine.util.IabHelper;
import sleeping_vityaz.trackmycaffeine.util.IabResult;
import sleeping_vityaz.trackmycaffeine.util.Inventory;
import sleeping_vityaz.trackmycaffeine.util.Purchase;

/**
 * Created by naja-ox on 1/29/15.
 */
public class StoreFragment extends Fragment {

    private String TAG = "Store-Fragment";

    private ButtonRectangle bt_remove_ads;
    IabHelper mHelper;
    private AdView mAdView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_store, container, false);

        iapStartup();

        findViewsById(rootView);

        if (MyApplication.adsDisabled==false) {
            setupAds(rootView);
        }

        buttonClicked(rootView);

        // disabling button once the purchase is done
        if (MyApplication.adsDisabled == true){
            bt_remove_ads.setEnabled(false);
        }/*else{
            bt_remove_ads.setEnabled(true);
        }*/
        //bt_remove_ads.setEnabled(true);
        Log.d(TAG, "button is disabled: "+MyApplication.adsDisabled);

        return rootView;
    }

    private void buttonClicked(View rootView) {
        bt_remove_ads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mHelper.launchPurchaseFlow(getActivity(), getResources().getString(R.string.SKU_ad_removal), 10001,
                            mPurchaseFinishedListener, "mypurchasetoken1");
                }catch (IllegalStateException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void iapStartup() {

        // compute your public key and store it in base64EncodedPublicKey
        mHelper = new IabHelper(getActivity(), getResources().getString(R.string.base64EncodedPublicKey));

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.d(TAG, "Problem setting up In-app Billing: " + result);
                } else {
                    // Hooray, IAB is fully set up!
                    Log.d(TAG, "In-app Billing is set up OK");
                }
            }
        });
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result,
                                          Purchase purchase)
        {
            if (result.isFailure()) {
                // Handle error
                Log.d(TAG, "PurchaseFinishedListener: Error");
                return;
            }
            else if (purchase.getSku().equals(getResources().getString(R.string.SKU_ad_removal))) {
                Log.d(TAG, "OnIabPurchaseFinishedListener: disabling button");
                bt_remove_ads.setEnabled(false);
                consumeItem();
            }

        }
    };

    public void consumeItem() {
        mHelper.queryInventoryAsync(mReceivedInventoryListener);
    }

    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {


            if (result.isFailure()) {
                // Handle failure
                Log.d(TAG, "QueryInventoryFinishedListener: Error");
            } else {
                mHelper.consumeAsync(inventory.getPurchase(getResources().getString(R.string.SKU_ad_removal)),
                        mConsumeFinishedListener);
            }
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase,
                                              IabResult result) {

                    if (result.isSuccess()) {
                        MyApplication.adsDisabled = true;
                        Toast.makeText(getActivity(), "Please restart the app now to remove ads", Toast.LENGTH_LONG).show();
                    } else {
                        // handle error
                        Log.d(TAG, "OnConsumeFinishedListener: Error");
                    }
                }
            };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



    private void findViewsById(View rootView) {
        bt_remove_ads = (ButtonRectangle) rootView.findViewById(R.id.bt_remove_ads);
    }

    private void setupAds(View rootView){
        /* ADVERTISEMENTS */

        LinearLayout lin_layout = (LinearLayout) rootView.findViewById(R.id.lin_layout);

        // Create a banner ad. The ad size and ad unit ID must be set before calling loadAd.
        mAdView = new AdView(rootView.getContext());
        mAdView.setAdSize(AdSize.SMART_BANNER);
        mAdView.setAdUnitId(getResources().getString(R.string.banner_ad_unit_id));

        AdRequest adRequest = new AdRequest.Builder().build();
        lin_layout.addView(mAdView);
        mAdView.loadAd(adRequest);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }

}
