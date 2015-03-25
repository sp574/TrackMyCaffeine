package sleeping_vityaz.trackmycaffeine;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import sleeping_vityaz.trackmycaffeine.fragments.AboutFragment;
import sleeping_vityaz.trackmycaffeine.fragments.AddNewCaffeineFragment;
import sleeping_vityaz.trackmycaffeine.fragments.CustomItemsFragment;
import sleeping_vityaz.trackmycaffeine.fragments.GraphFragment;
import sleeping_vityaz.trackmycaffeine.fragments.MyStatsFragment;
import sleeping_vityaz.trackmycaffeine.fragments.PastRecordsFragment;
import sleeping_vityaz.trackmycaffeine.fragments.StoreFragment;
import sleeping_vityaz.trackmycaffeine.fragments.TrackerFragment;
import sleeping_vityaz.trackmycaffeine.util.IabHelper;
import sleeping_vityaz.trackmycaffeine.util.IabResult;
import sleeping_vityaz.trackmycaffeine.util.Inventory;
import sleeping_vityaz.trackmycaffeine.util.Purchase;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;


public class MainActivity extends MaterialNavigationDrawer {


    public static InterstitialAd mInterstitialAd;
    // Helper billing object
    private IabHelper mHelper;

    @Override
    public void init(Bundle savedInstanceState) {

        initialiseBilling();

        // create sections
        this.addSection(newSection(getString(R.string.tracker), new TrackerFragment()));
        this.addSection(newSection(getString(R.string.custom_items), new CustomItemsFragment()));
        this.addSection(newSection(getString(R.string.graph), new GraphFragment()));
        this.addSection(newSection(getString(R.string.past_records), new PastRecordsFragment()));
        this.addSection(newSection(getString(R.string.my_stats), new MyStatsFragment()));
        this.addSection(newSection(getString(R.string.store), new StoreFragment()));
        this.addSection(newSection(getString(R.string.settings), R.drawable.ic_settings_black_24dp, new Intent(this, Settings.class)));

        // create bottom section
        this.addBottomSection(newSection(getString(R.string.about), new AboutFragment()));
        // prevents the nav drawer from opening on application start
        disableLearningPattern();

        setupInterstitialAds();

    }

    private void setupInterstitialAds(){
        /* ADVERTISEMENTS */
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.test_interstitial_ad_unit_id));
        requestNewInterstitial();

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder() //TODO: remove & change ad_ID before publishing
                .addTestDevice(getResources().getString(R.string.hash))
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    private void initialiseBilling() {
        if (mHelper != null) {
            return;
        }
        // Create the helper, passing it our context and the public key to verify signatures with
        mHelper = new IabHelper(this, getResources().getString(R.string.base64EncodedPublicKey));

        // Enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener will be called once setup completes.
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) {
                    return;
                }

                // Something went wrong
                if (!result.isSuccess()) {
                    Log.e("Main-Activity", "Problem setting up in-app billing: " + result.getMessage());
                    return;
                }

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                mHelper.queryInventoryAsync(iabInventoryListener());
            }
        });
    }

    /**
     * Listener that's called when we finish querying the items and subscriptions we own
     */
    private IabHelper.QueryInventoryFinishedListener iabInventoryListener() {
        return new IabHelper.QueryInventoryFinishedListener() {
            @Override
            public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) {
                    return;
                }

                // Something went wrong
                if (!result.isSuccess()) {
                    return;
                }

                // Do your checks here...

                // Do we have the premium upgrade?
                Purchase purchasePro = inventory.getPurchase(getResources().getString(R.string.SKU_test_purchased)); // Where G.SKU_PRO is your product ID (eg. permanent.ad_removal)
                MyApplication.adsDisabled = (purchasePro != null);
                Log.d("Main-Activity", "User is " + (MyApplication.adsDisabled ? "PREMIUM" : "NOT PREMIUM"));
                // After checking inventory, re-jig stuff which the user can access now
                // that we've determined what they've purchased
                Log.d("Main-Activity", "Initial inventory query finished; enabling main UI.");

            }
        };
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //MyApplication.adsDisabled = false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*int id = item.getItemId();
        if (id == R.id.action_add) {
            startActivity(new Intent(this, AddNewCaffeineFragment.class));
            return true;
        }
        */return super.onOptionsItemSelected(item);
    }

}
