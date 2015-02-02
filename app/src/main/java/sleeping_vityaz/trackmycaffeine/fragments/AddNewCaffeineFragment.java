package sleeping_vityaz.trackmycaffeine.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;


import com.doomonafireball.betterpickers.datepicker.DatePickerBuilder;
import com.doomonafireball.betterpickers.datepicker.DatePickerDialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import sleeping_vityaz.trackmycaffeine.databases.CustomAutoCompleteView;
import sleeping_vityaz.trackmycaffeine.databases.DBAdapter;
import sleeping_vityaz.trackmycaffeine.databases.DBTools;
import sleeping_vityaz.trackmycaffeine.MainActivity;
import sleeping_vityaz.trackmycaffeine.R;
import sleeping_vityaz.trackmycaffeine.util.CommonConstants;

/**
 * Created by naja-ox on 1/30/15.
 */
public class AddNewCaffeineFragment extends ActionBarActivity implements
        DatePickerDialogFragment.DatePickerDialogHandler {

    public static final String TAG = "ADD-NEW-CAFFEINE-FRAGMENT";
    public static final String DATEPICKER_TAG = "datepicker";
    public static final String TIMEPICKER_TAG = "timepicker";

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
    private EditText et_item;

    private SimpleDateFormat dateFormatter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_new_caffeine);

        findViewsById();

        dbAdapter = new DBAdapter(this);
        dbAdapter.createDatabase();

        dateFormatter = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());



        final Calendar calendar = Calendar.getInstance();


        findViewById(R.id.et_date).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DatePickerBuilder dpb = new DatePickerBuilder()
                        .setFragmentManager(getSupportFragmentManager())
                        .setStyleResId(R.style.BetterPickersDialogFragment);
                dpb.show();
            }
        });

        findViewById(R.id.et_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        myAutoComplete.addTextChangedListener(watch);


        // set our adapter
        myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, item);
        myAutoComplete.setAdapter(myAdapter);

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

        }
    };

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

    }


    @Override
    public void onDialogDateSet(int i, int year, int monthOfYear, int dayOfMonth) {
        et_date.setText((monthOfYear+1)+"-"+dayOfMonth+"-"+year);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
                    //&& !et_item.getText().toString().equals("")
                    ) {
                final DBTools dbTools = DBTools.getInstance(this);

                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                // KEY_ID | PRODUCT | DRINK_VOLUME | CAFFEINE_MASS | DATE_CREATED | TIME_STARTED
                HashMap<String, String> queryValuesMap = new HashMap<String, String>();
                queryValuesMap.put(CommonConstants.PRODUCT, myAutoComplete.getText().toString());
                queryValuesMap.put(CommonConstants.DRINK_VOLUME, "" + 1); //get from spinner
                queryValuesMap.put(CommonConstants.CAFFEINE_MASS, "" + 1);  // get from product_db once spinner is known
                queryValuesMap.put(CommonConstants.DATE_CREATED, "2015-01-31");//changeDateFormat(dateFormatter.format(et_date.getText().toString())));
                queryValuesMap.put(CommonConstants.TIME_STARTED, et_start.getText().toString());

                alert(queryValuesMap.toString());

                dbTools.insertRecord(queryValuesMap);

                startActivity(intent);
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String changeDateFormat(String oldDateFormatString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyy");
            Date d = sdf.parse(oldDateFormatString);
            sdf.applyPattern("yyyy-MM-dd");
            return sdf.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void alert(String s) {
        Log.d(TAG, s);
    }
}
