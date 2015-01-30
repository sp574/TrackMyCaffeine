package sleeping_vityaz.trackmycaffeine;


import android.content.Intent;
import android.os.Bundle;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import sleeping_vityaz.trackmycaffeine.fragments.AboutFragment;
import sleeping_vityaz.trackmycaffeine.fragments.CustomItemsFragment;
import sleeping_vityaz.trackmycaffeine.fragments.GraphFragment;
import sleeping_vityaz.trackmycaffeine.fragments.MyStatsFragment;
import sleeping_vityaz.trackmycaffeine.fragments.StoreFragment;
import sleeping_vityaz.trackmycaffeine.fragments.TrackerFragment;


public class MainActivity extends MaterialNavigationDrawer {


    @Override
    public void init(Bundle savedInstanceState) {
        // create sections
        this.addSection(newSection(getString(R.string.tracker), new TrackerFragment()));
        this.addSection(newSection(getString(R.string.custom_items), new CustomItemsFragment()));
        this.addSection(newSection(getString(R.string.graph), new GraphFragment()));
        this.addSection(newSection(getString(R.string.my_stats), new MyStatsFragment()));
        this.addSection(newSection(getString(R.string.store), new StoreFragment()));
        this.addSection(newSection(getString(R.string.settings), R.drawable.ic_settings_black_24dp, new Intent(this, Settings.class)));

        // create bottom section
        this.addBottomSection(newSection(getString(R.string.about), new AboutFragment()));
        // prevents the nav drawer from opening on application start
        disableLearningPattern();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
