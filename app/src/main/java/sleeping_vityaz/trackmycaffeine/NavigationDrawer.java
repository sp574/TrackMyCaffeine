package sleeping_vityaz.trackmycaffeine;

import android.content.Intent;
import android.os.Bundle;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import sleeping_vityaz.trackmycaffeine.fragments.FragmentIndex;


public class NavigationDrawer extends MaterialNavigationDrawer {

    @Override
    public void init(Bundle savedInstanceState) {
        // create sections
        this.addSection(newSection("Section 1", new FragmentIndex()));
        this.addSection(newSection("Section 2", new FragmentIndex()));

        // create bottom section
        this.addBottomSection(newSection("Bottom Section",R.drawable.ic_settings_black_24dp,new Intent(this,Settings.class)));

    }
}
