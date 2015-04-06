package sleeping_vityaz.trackmycaffeine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.HashMap;

import sleeping_vityaz.trackmycaffeine.databases.DBTools;
import sleeping_vityaz.trackmycaffeine.util.Calculations;
import sleeping_vityaz.trackmycaffeine.util.CommonConstants;
import sleeping_vityaz.trackmycaffeine.util.Util;

/**
 * Created by naja-ox on 3/24/15.
 */
public class EditCustomRecord extends ActionBarActivity {

    public static final String TAG = "EDIT-CUSTOM-RECORD";

    private TextView tv_item;
    private TextView tv_mass;
    private TextView tv_volume;
    private EditText et_item;
    private EditText et_mass;
    private EditText et_volume;
    private RadioGroup rg_units;
    private RadioButton rb_ounces;
    private RadioButton rb_ml;

    private String product="";
    private String keyId="";

    private SharedPreferences settings;
    private String units;

    DBTools dbTools = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_custom_record);

        // Restore preferences
        if (this != null) {
            settings = PreferenceManager.getDefaultSharedPreferences(this);
        }
        units = settings.getString("units_drinks_pref", "");

        dbTools = DBTools.getInstance(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setBackgroundColor(getResources().getColor(R.color.primary));

        findViewsById();

        getInfoFromDB();
        radioGroupListener();
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



                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                String density = "";
                String volume = "";
                if (rb_ounces.isChecked()) {
                    density = ""+(Double.parseDouble(et_mass.getText().toString())/Double.parseDouble(et_volume.getText().toString()));
                    volume = et_volume.getText().toString();
                } else if (rb_ml.isChecked()) {
                    density = "" + Calculations.round(((Double.parseDouble(et_mass.getText().toString()) / Double.parseDouble(et_volume.getText().toString())) / 0.033814), 1);
                    volume = ""+Calculations.round((Double.parseDouble(et_volume.getText().toString()) * 0.033814), 1);
                }

                // KEY_ID | PRODUCT | DRINK_VOLUME | CAFFEINE_MASS | DATE_CREATED | TIME_STARTED
                HashMap<String, String> queryValuesMap = new HashMap<String, String>();
                queryValuesMap.put(CommonConstants.C_POSITION, keyId);
                queryValuesMap.put(CommonConstants.C_PRODUCT, et_item.getText().toString());
                queryValuesMap.put(CommonConstants.C_MASS_CAFFEINE, et_mass.getText().toString());
                queryValuesMap.put(CommonConstants.C_VOLUME_DRINK, volume);
                queryValuesMap.put(CommonConstants.C_DENSITY_CAFFEINE, density);
                alert(queryValuesMap.toString());

                dbTools.updateCustomRecord(queryValuesMap);

                startActivity(intent);
                finish();
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

    private void radioGroupListener() {
        rg_units.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.ounces:
                        if (!et_volume.getText().toString().equals("")) {
                            Log.d(TAG, "" + Calculations.round((Double.parseDouble(et_volume.getText().toString()) * 0.033814), 1));
                            et_volume.setText("" + Calculations.round((Double.parseDouble(et_volume.getText().toString()) * 0.033814), 1));
                        }
                        break;
                    case R.id.ml:
                        if (!et_volume.getText().toString().equals(""))
                            et_volume.setText("" + Calculations.round((Double.parseDouble(et_volume.getText().toString()) / 0.033814), 1));
                        break;
                }
            }
        });
    }

    public void getInfoFromDB() {

        Intent theIntent = getIntent();
        product = theIntent.getStringExtra("product");
        keyId = theIntent.getStringExtra("keyId");

        alert(product);

        HashMap<String, String> recordMap = dbTools.getCustomRecordInfo(product);
        if (recordMap.size() != 0) {
            et_item.setText(recordMap.get(CommonConstants.C_PRODUCT));
            et_mass.setText(recordMap.get(CommonConstants.C_MASS_CAFFEINE));

            if (units.equals("fl oz")) {
                et_volume.setText(recordMap.get(CommonConstants.C_VOLUME_DRINK));
                rb_ounces.setChecked(true);
            } else {
                et_volume.setText(""+Calculations.round((Double.parseDouble(recordMap.get(CommonConstants.C_VOLUME_DRINK)) / 0.033814), 1));
                rb_ml.setChecked(true);
            }
        }
    }


}
