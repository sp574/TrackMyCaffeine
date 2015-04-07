package sleeping_vityaz.trackmycaffeine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.datetimepicker.date.DatePickerDialog;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerBuilder;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerDialogFragment;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import sleeping_vityaz.trackmycaffeine.databases.DBAdapter;
import sleeping_vityaz.trackmycaffeine.databases.DBTools;
import sleeping_vityaz.trackmycaffeine.util.Calculations;
import sleeping_vityaz.trackmycaffeine.util.CommonConstants;
import sleeping_vityaz.trackmycaffeine.util.Util;

/**
 * Created by naja-ox on 3/23/15.
 */
public class EditRecord extends ActionBarActivity implements DatePickerDialog.OnDateSetListener, RadialTimePickerDialog.OnTimeSetListener, NumberPickerDialogFragment.NumberPickerDialogHandler {

    public static final String TAG = "UPDATE-CAFFEINE-ACTIVITY";
    private static final String FRAG_TAG_TIME_PICKER = "timePickerDialogFragment";
    private boolean mHasDialogFrame;
    private static final String TIME_PATTERN = "hh:mm a";

    DBAdapter dbAdapter;

    private TextView tv_item;
    private TextView tv_date;
    private TextView tv_start;
    private TextView tv_duration;
    private TextView tv_caffeine_content;
    private TextView tv_caffeine_num;
    private EditText et_date;
    private EditText et_start;
    private EditText et_volume;
    private EditText et_duration;
    private RadioButton rb_floz;
    private RadioButton rb_ml;
    private RadioGroup rg_units;


    private Calendar calendar;
    private DateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    private SharedPreferences settings;
    private String units;

    private String keyId;

    DBTools dbTools = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_record);

        dbTools = DBTools.getInstance(getApplicationContext());

        // Restore preferences
        if (this != null) {
            settings = PreferenceManager.getDefaultSharedPreferences(this);
        }
        units = settings.getString("units_drinks_pref", "");

        if (savedInstanceState == null) {
            mHasDialogFrame = findViewById(R.id.frame) != null;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setBackgroundColor(getResources().getColor(R.color.primary));

        findViewsById();

        dbAdapter = new DBAdapter(this);
        dbAdapter.createDatabase();

        calendar = Calendar.getInstance();
        dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        timeFormat = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());

        getInfoFromDB();

        et_volume.addTextChangedListener(watch);
        radioGroupListener();


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

    TextWatcher watch = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence userInput, int start, int before, int count) {
            // updare tv_caffeine_num
            getVolumeInfoAndSetFields(tv_item.getText().toString());
        }

        @Override
        public void afterTextChanged(Editable s) {
            // see how much volume is set in the product db
            getVolumeInfoAndSetFields(tv_item.getText().toString());
        }
    };

    private void findViewsById() {
        tv_item = (TextView) findViewById(R.id.tv_item);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_start = (TextView) findViewById(R.id.tv_start);
        tv_duration = (TextView) findViewById(R.id.tv_duration);
        tv_caffeine_content = (TextView) findViewById(R.id.tv_caffeine_content);
        tv_caffeine_num = (TextView) findViewById(R.id.tv_caffeine_num);
        et_date = (EditText) findViewById(R.id.et_date);
        et_start = (EditText) findViewById(R.id.et_start);
        et_duration = (EditText) findViewById(R.id.et_duration);
        et_volume = (EditText) findViewById(R.id.et_volume);
        rg_units = (RadioGroup) findViewById(R.id.rg_units);
        rb_floz = (RadioButton) findViewById(R.id.ounces);
        rb_ml = (RadioButton) findViewById(R.id.ml);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_save) {
            alert(et_date.getText().toString());
            if (et_date.getText().toString() != ""
                    && !et_start.getText().toString().equals("")
                    && !et_volume.getText().toString().equals("")
                    ) {
                dbTools = DBTools.getInstance(this);

                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                dbAdapter.open();
                HashMap<String, String> recordMap = dbAdapter.getRecordInfo(tv_item.getText().toString());
                dbAdapter.close();

                if (!recordMap.isEmpty()) {

                    if (null == recordMap.get(CommonConstants.C_DENSITY_CAFFEINE))
                        alert("It's null");

                    String toDB_volume = "";
                    if (rb_floz.isChecked()) {
                        toDB_volume = et_volume.getText().toString();
                    } else if (rb_ml.isChecked()) {
                        toDB_volume = "" + Calculations.round((Double.parseDouble(et_volume.getText().toString()) * 0.033814), 1);
                    }
                    // KEY_ID | PRODUCT | DRINK_VOLUME | CAFFEINE_MASS | DATE_CREATED | TIME_STARTED
                    HashMap<String, String> queryValuesMap = new HashMap<String, String>();
                    queryValuesMap.put(CommonConstants.KEY_ID, keyId);
                    queryValuesMap.put(CommonConstants.PRODUCT, tv_item.getText().toString());
                    queryValuesMap.put(CommonConstants.DRINK_VOLUME, toDB_volume); //get from spinner
                    queryValuesMap.put(CommonConstants.CAFFEINE_MASS, Util.adjustCaffeineMass(recordMap.get(CommonConstants.C_DENSITY_CAFFEINE), toDB_volume));  // get from product_db once spinner is known
                    queryValuesMap.put(CommonConstants.DATE_CREATED, Util.convertDateForDB(et_date.getText().toString()));//changeDateFormat(dateFormatter.format(et_date.getText().toString())));
                    queryValuesMap.put(CommonConstants.TIME_STARTED, Util.convertTimeForDB(et_start.getText().toString()));
                    queryValuesMap.put(CommonConstants.DURATION, et_duration.getText().toString());

                    alert(queryValuesMap.toString());

                    dbTools.updateRecord(queryValuesMap);
                }else{ // must be a custom item
                    recordMap = dbTools.getCustomRecordInfo(tv_item.getText().toString());

                    String toDB_volume = "";
                    if (rb_floz.isChecked()) {
                        toDB_volume = et_volume.getText().toString();
                    } else if (rb_ml.isChecked()) {
                        toDB_volume = "" + Calculations.round((Double.parseDouble(et_volume.getText().toString()) * 0.033814), 1);
                    }
                    // KEY_ID | PRODUCT | DRINK_VOLUME | CAFFEINE_MASS | DATE_CREATED | TIME_STARTED
                    HashMap<String, String> queryValuesMap = new HashMap<String, String>();
                    queryValuesMap.put(CommonConstants.KEY_ID, keyId);
                    queryValuesMap.put(CommonConstants.PRODUCT, tv_item.getText().toString());
                    queryValuesMap.put(CommonConstants.DRINK_VOLUME, toDB_volume); //get from spinner
                    queryValuesMap.put(CommonConstants.CAFFEINE_MASS, Util.adjustCaffeineMass(recordMap.get(CommonConstants.C_DENSITY_CAFFEINE), toDB_volume));  // get from product_db once spinner is known
                    queryValuesMap.put(CommonConstants.DATE_CREATED, Util.convertDateForDB(et_date.getText().toString()));//changeDateFormat(dateFormatter.format(et_date.getText().toString())));
                    queryValuesMap.put(CommonConstants.TIME_STARTED, Util.convertTimeForDB(et_start.getText().toString()));
                    queryValuesMap.put(CommonConstants.DURATION, et_duration.getText().toString());

                    alert(queryValuesMap.toString());

                    dbTools.updateRecord(queryValuesMap);
                }

                startActivity(intent);
                finish();
            }else{
                Toast.makeText(this, "You must fill out all fields", Toast.LENGTH_LONG).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_new, menu);
        return true;
    }

    private void getVolumeInfoAndSetFields(String product) {
        dbAdapter.open();
        HashMap<String, String> recordMap = dbAdapter.getRecordInfo(product);
        dbAdapter.close();

        // setting caffeine num
        if (recordMap.get(CommonConstants.C_DENSITY_CAFFEINE) != null && !et_volume.getText().toString().equals("")) {
            String toDB_volume = "";
            if (rb_floz.isChecked()) {
                toDB_volume = et_volume.getText().toString();
            } else if (rb_ml.isChecked()) {
                toDB_volume = "" + Calculations.round((Double.parseDouble(et_volume.getText().toString()) * 0.033814), 1);
            }
            tv_caffeine_num.setText(Util.adjustCaffeineMass(recordMap.get(CommonConstants.C_DENSITY_CAFFEINE), toDB_volume) + "mg");
        } else { // must be custom product
            recordMap = dbTools.getCustomRecordInfo(product);
            if (!recordMap.isEmpty() && !et_volume.getText().toString().equals("")) {
                // setting caffeine num
                if (recordMap.get(CommonConstants.C_DENSITY_CAFFEINE) != null) {
                    String toDB_volume = "";
                    if (rb_floz.isChecked()) {
                        toDB_volume = et_volume.getText().toString();
                    } else if (rb_ml.isChecked()) {
                        toDB_volume = "" + Calculations.round((Double.parseDouble(et_volume.getText().toString()) * 0.033814), 1);
                    }
                    tv_caffeine_num.setText(Util.adjustCaffeineMass(recordMap.get(CommonConstants.C_DENSITY_CAFFEINE), toDB_volume) + "mg");
                }
            }

        }
    }

    private void update() {
        et_date.setText(dateFormat.format(calendar.getTime()));
        et_start.setText(timeFormat.format(calendar.getTime()));
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.et_date:
                DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show(getFragmentManager(), "datePicker");
                break;
            case R.id.et_start:
                DateTime now = DateTime.now();
                RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog
                        .newInstance(EditRecord.this, now.getHourOfDay(), now.getMinuteOfHour(), false);
                if (mHasDialogFrame) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                    ft.add(R.id.frame, timePickerDialog, FRAG_TAG_TIME_PICKER)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commit();
                } else {
                    timePickerDialog.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER);
                }
                break;
        }
    }

    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(year, monthOfYear, dayOfMonth);
        update();
    }

    @Override
    public void onTimeSet(RadialTimePickerDialog radialTimePickerDialog, int hour_of_day, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hour_of_day);
        calendar.set(Calendar.MINUTE, minute);
        update();
    }

    @Override
    public void onDialogNumberSet(int reference, int number, double decimal, boolean isNegative, double fullNumber) {
        et_volume.setText("" + fullNumber);
    }


    private void alert(String s) {
        Log.d(TAG, s);
    }

    public void getInfoFromDB() {
        Intent theIntent = getIntent();
        keyId = theIntent.getStringExtra("keyId");

        HashMap<String, String> recordMap = dbTools.getRecordInfo(keyId);
        if (recordMap.size() != 0) {
            tv_item.setText(recordMap.get(CommonConstants.PRODUCT));
            et_date.setText(Util.convertDateFromDB(recordMap.get(CommonConstants.DATE_CREATED)));
            et_start.setText(Util.convertTimeFromDB(recordMap.get(CommonConstants.TIME_STARTED)));
            et_duration.setText(recordMap.get(CommonConstants.DURATION));
            tv_caffeine_num.setText(recordMap.get(CommonConstants.CAFFEINE_MASS) + "mg");

            if (units.equals("fl oz")) {
                et_volume.setText(recordMap.get(CommonConstants.DRINK_VOLUME));
                rb_floz.setChecked(true);
            } else {
                et_volume.setText(""+Calculations.round((Double.parseDouble(recordMap.get(CommonConstants.DRINK_VOLUME)) / 0.033814), 1));
                rb_ml.setChecked(true);
            }
        }
    }
}
