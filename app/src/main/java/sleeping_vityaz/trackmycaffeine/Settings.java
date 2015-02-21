package sleeping_vityaz.trackmycaffeine;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import java.util.List;

public class Settings extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(R.id.content_frame,
                new PreferenceFragmentOne()).commit();
    }



    /*public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;

    }*/

    /**
     * This fragment shows the preferences for the first header.
     */
    public static class PreferenceFragmentOne extends PreferenceFragment implements
            SharedPreferences.OnSharedPreferenceChangeListener,
            Preference.OnPreferenceClickListener{
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Make sure default values are applied.  In a real app, you would
            // want this in a shared function that is used to retrieve the
            // SharedPreferences wherever they are needed.
            //PreferenceManager.setDefaultValues(getActivity(),
            //        R.xml.advanced_preferences, false);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.fragmented_preferences);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            Preference pref = findPreference(key);
            Log.d("Settings", "key: "+key.toString());
            if (pref instanceof EditTextPreference) {
                EditTextPreference editTextPref = (EditTextPreference) pref;
                pref.setSummary(editTextPref.getText()+"mg");
            }
            if (pref instanceof ListPreference) {
                ListPreference listPref = (ListPreference) pref;
                pref.setSummary(listPref.getEntry());
            }
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            return false;
        }

        @Override
        public void onResume() {
            super.onResume();
            MyApplication.activityResumed();
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            MyApplication.activityPaused();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);

        }
    }




}