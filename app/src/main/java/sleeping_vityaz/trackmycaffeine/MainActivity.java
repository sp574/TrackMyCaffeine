package sleeping_vityaz.trackmycaffeine;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import sleeping_vityaz.trackmycaffeine.fragments.AboutFragment;
import sleeping_vityaz.trackmycaffeine.fragments.AddNewCaffeineFragment;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_add) {
            startActivity(new Intent(this, AddNewCaffeineFragment.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
