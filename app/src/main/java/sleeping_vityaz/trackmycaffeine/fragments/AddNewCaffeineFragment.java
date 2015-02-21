package sleeping_vityaz.trackmycaffeine.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerBuilder;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerDialogFragment;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Calendar;

import java.util.HashMap;
import java.util.List;

import sleeping_vityaz.trackmycaffeine.MyApplication;
import sleeping_vityaz.trackmycaffeine.databases.CustomAutoCompleteView;
import sleeping_vityaz.trackmycaffeine.databases.DBAdapter;
import sleeping_vityaz.trackmycaffeine.databases.DBTools;
import sleeping_vityaz.trackmycaffeine.MainActivity;
import sleeping_vityaz.trackmycaffeine.R;
import sleeping_vityaz.trackmycaffeine.util.Calculations;
import sleeping_vityaz.trackmycaffeine.util.CommonConstants;
import sleeping_vityaz.trackmycaffeine.util.Util;

/**
 * Created by naja-ox on 1/30/15.
 */
public class AddNewCaffeineFragment extends ActionBarActivity implements DatePickerDialog.OnDateSetListener, RadialTimePickerDialog.OnTimeSetListener, NumberPickerDialogFragment.NumberPickerDialogHandler  {

    public static final String TAG = "ADD-NEW-CAFFEINE-FRAGMENT";
    private static final String FRAG_TAG_TIME_PICKER = "timePickerDialogFragment";
    private boolean mHasDialogFrame;
    private static final String TIME_PATTERN = "hh:mm a";

    CustomAutoCompleteView myAutoComplete;
    // adapter for auto-complete
    ArrayAdapter<String> myAdapter;
    DBAdapter dbAdapter;


    // just to add some initial value
    String[] item = new String[] {"Please search..."};

    private TextView tv_title;
    private TextView tv_date;
    private TextView tv_start;
    private EditText et_date;
    private EditText et_start;
    private EditText et_volume;
    private RadioButton rb_floz;
    private RadioButton rb_ml;
    private RadioGroup rg_units;


    private Calendar calendar;
    private DateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_new_caffeine);

        if (savedInstanceState == null) {
            mHasDialogFrame = findViewById(R.id.frame) != null;
        }

        findViewsById();

        dbAdapter = new DBAdapter(this);
        dbAdapter.createDatabase();

        calendar = Calendar.getInstance();
        dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        timeFormat = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());

        update();


        myAutoComplete.addTextChangedListener(watch);

        // set our adapter
        myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, item);
        myAutoComplete.setAdapter(myAdapter);

        rg_units.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
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
                            et_volume.setText(""+ Calculations.round((Double.parseDouble(et_volume.getText().toString()) / 0.033814), 1));
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
            // query the database based on the user input
            item = getItemsFromDb(userInput.toString());

            // update the adapater
            myAdapter.notifyDataSetChanged();
            myAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.custom_autocomplete_dropdown, item);
            myAutoComplete.setAdapter(myAdapter);

        }

        @Override
        public void afterTextChanged(Editable s) {
            // see how much volume is set in the product db
            getVolumeInfoAndSetFields(myAutoComplete.getText().toString());
        }
    };

    private void getVolumeInfoAndSetFields(String product) {
        dbAdapter.open();
        HashMap<String, String> recordMap = dbAdapter.getRecordInfo(product);
        dbAdapter.close();
        et_volume.setText(recordMap.get(CommonConstants.C_VOLUME_DRINK));
        rb_floz.setChecked(true);
    }

    // this function is used in CustomAutoCompleteTextChangedListener.java
    public String[] getItemsFromDb(String searchTerm){

        dbAdapter.open();

        // add items on the array dynamically
        List<String> products = dbAdapter.read(searchTerm);
        int rowCount = products.size();

        String[] item = new String[rowCount];
        int x = 0;

        for (String product : products) {
            item[x] = product;
            x++;
        }
        dbAdapter.close();

        return item;
    }

    private void findViewsById() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_start = (TextView) findViewById(R.id.tv_start);
        et_date = (EditText) findViewById(R.id.et_date);
        et_start = (EditText) findViewById(R.id.et_start);
        myAutoComplete = (CustomAutoCompleteView) findViewById(R.id.ac_product_autocomplete);
        et_volume = (EditText) findViewById(R.id.et_volume);
        rg_units = (RadioGroup) findViewById(R.id.rg_units);
        rb_floz = (RadioButton) findViewById(R.id.ounces);
        rb_ml = (RadioButton) findViewById(R.id.ml);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause(){
        super.onPause();
        MyApplication.activityPaused();
    }

    @Override
    public void onResume() {
        // Example of reattaching to the fragment
        super.onResume();

        MyApplication.activityResumed();

        RadialTimePickerDialog rtpd = (RadialTimePickerDialog) getSupportFragmentManager().findFragmentByTag(
                FRAG_TAG_TIME_PICKER);
        if (rtpd != null) {
            rtpd.setOnTimeSetListener(this);
        }
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
            alert(et_date.getText().toString());
            if (et_date.getText().toString()!=""
                    && !et_start.getText().toString().equals("")
                    && !myAutoComplete.getText().toString().equals("")
                    && !et_volume.getText().toString().equals("")
                    ) {
                final DBTools dbTools = DBTools.getInstance(this);

                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                dbAdapter.open();
                HashMap<String, String> recordMap = dbAdapter.getRecordInfo(myAutoComplete.getText().toString());
                dbAdapter.close();

                String toDB_volume="";
                if (rb_floz.isChecked()) {
                    toDB_volume = et_volume.getText().toString();
                } else if(rb_ml.isChecked()){
                    toDB_volume = ""+Calculations.round((Double.parseDouble(et_volume.getText().toString()) * 0.033814), 1);
                }
                // KEY_ID | PRODUCT | DRINK_VOLUME | CAFFEINE_MASS | DATE_CREATED | TIME_STARTED
                HashMap<String, String> queryValuesMap = new HashMap<String, String>();
                queryValuesMap.put(CommonConstants.PRODUCT, myAutoComplete.getText().toString());
                queryValuesMap.put(CommonConstants.DRINK_VOLUME, toDB_volume); //get from spinner
                queryValuesMap.put(CommonConstants.CAFFEINE_MASS, Util.adjustCaffeineMass(recordMap.get(CommonConstants.C_DENSITY_CAFFEINE), toDB_volume));  // get from product_db once spinner is known
                queryValuesMap.put(CommonConstants.DATE_CREATED, Util.convertDateForDB(et_date.getText().toString()));//changeDateFormat(dateFormatter.format(et_date.getText().toString())));
                queryValuesMap.put(CommonConstants.TIME_STARTED, Util.convertTimeForDB(et_start.getText().toString()));

                alert(queryValuesMap.toString());

                dbTools.insertRecord(queryValuesMap);

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
                        .newInstance(AddNewCaffeineFragment.this, now.getHourOfDay(), now.getMinuteOfHour(), false);
                if (mHasDialogFrame) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                    ft.add(R.id.frame, timePickerDialog, FRAG_TAG_TIME_PICKER)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commit();
                } else {
                    timePickerDialog.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER);
                }
                break;
            case R.id.et_volume:
                NumberPickerBuilder npb = new NumberPickerBuilder()
                        .setFragmentManager(getSupportFragmentManager())
                        .setStyleResId(R.style.BetterPickersDialogFragment_Light);
                npb.show();
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
}
