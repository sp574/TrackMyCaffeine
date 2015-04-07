package sleeping_vityaz.trackmycaffeine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.datetimepicker.date.DatePickerDialog;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerBuilder;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.HashMap;

import sleeping_vityaz.trackmycaffeine.databases.DBTools;
import sleeping_vityaz.trackmycaffeine.util.Calculations;
import sleeping_vityaz.trackmycaffeine.util.CommonConstants;
import sleeping_vityaz.trackmycaffeine.util.Util;

/**
 * Created by naja-ox on 3/24/15.
 */
public class AddNewCustomCaffeineActivity extends ActionBarActivity {

    public static final String TAG = "ADD-NEW-CUSTOM-CAFFEINE";



    private TextView tv_item;
    private TextView tv_mass;
    private TextView tv_volume;
    private EditText et_item;
    private EditText et_mass;
    private EditText et_volume;
    private RadioGroup rg_units;
    private RadioButton rb_ounces;
    private RadioButton rb_ml;

    private SharedPreferences settings;
    private String units;


    DBTools dbTools = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_custom_caffeine);

        // Restore preferences
        if (this != null) {
            settings = PreferenceManager.getDefaultSharedPreferences(this);
        }
        units = settings.getString("units_drinks_pref", "");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setBackgroundColor(getResources().getColor(R.color.primary));

        findViewsById();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_new, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_save) {

            if (!et_item.getText().toString().equals("")
                    && !et_mass.getText().toString().equals("")
                    && !et_volume.getText().toString().equals("")
                    && (rb_ounces.isChecked() || rb_ml.isChecked())
                    ) {

                final DBTools dbTools = DBTools.getInstance(this);

                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                String density = "";
                String volume = "";
                if (rb_ounces.isChecked()) {
                    density = ""+(Double.parseDouble(et_mass.getText().toString())/Double.parseDouble(et_volume.getText().toString()));
                    volume = et_volume.getText().toString();
                } else if (rb_ml.isChecked()) {
                    density = "" + Calculations.round(((Double.parseDouble(et_mass.getText().toString())/Double.parseDouble(et_volume.getText().toString())) / 0.033814), 1);
                    volume = ""+Calculations.round((Double.parseDouble(et_volume.getText().toString()) * 0.033814), 1);
                }

                // KEY_ID | PRODUCT | DRINK_VOLUME | CAFFEINE_MASS | DATE_CREATED | TIME_STARTED
                HashMap<String, String> queryValuesMap = new HashMap<String, String>();
                queryValuesMap.put(CommonConstants.C_PRODUCT, et_item.getText().toString());
                queryValuesMap.put(CommonConstants.C_MASS_CAFFEINE, et_mass.getText().toString());
                queryValuesMap.put(CommonConstants.C_VOLUME_DRINK, volume);
                queryValuesMap.put(CommonConstants.C_DENSITY_CAFFEINE, density);
                alert(queryValuesMap.toString());

                dbTools.insertCustomRecord(queryValuesMap);

                startActivity(intent);
                finish();
            }else{
                Toast.makeText(this, "You must fill out all fields", Toast.LENGTH_LONG).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void alert(String s) {
        Log.d(TAG, s);
    }

    private void findViewsById() {

        tv_item = (TextView) findViewById(R.id.tv_item);
        tv_mass = (TextView) findViewById(R.id.tv_mass);
        tv_volume = (TextView) findViewById(R.id.tv_volume);

        et_item = (EditText) findViewById(R.id.et_item);
        et_mass = (EditText) findViewById(R.id.et_mass);
        et_volume = (EditText) findViewById(R.id.et_volume);

        rg_units = (RadioGroup) findViewById(R.id.rg_units);
        rb_ounces = (RadioButton) findViewById(R.id.ounces);
        rb_ml = (RadioButton) findViewById(R.id.ml);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.activityPaused();
    }

    @Override
    public void onResume() {
        // Example of reattaching to the fragment
        super.onResume();

        MyApplication.activityResumed();

    }


}
