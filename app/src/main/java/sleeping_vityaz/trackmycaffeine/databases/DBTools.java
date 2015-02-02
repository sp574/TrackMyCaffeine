package sleeping_vityaz.trackmycaffeine.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sleeping_vityaz.trackmycaffeine.util.CommonConstants;

public class DBTools extends SQLiteOpenHelper {

    public static final String TAG = "DBTOOLS";

    private static DBTools sInstance;

    // Database Version
    private static final int DATABASE_VERSION = 1;




    public DBTools(Context applicationContext) {
        super(applicationContext, CommonConstants.DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DBTools getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DBTools(context.getApplicationContext());
        }
        return sInstance;
    }


    @Override
    public void onCreate(SQLiteDatabase database) {

        database.execSQL(createTable(CommonConstants.HISTORY_TABLE));

    }

    /* This will alter existing table without erasing any of the user's data
    *  Loops through each database version and  necessary columns*/
    @Override
    public void onUpgrade(final SQLiteDatabase database, final int oldVersion, final int newVersion) {

        int upgradeTo = oldVersion + 1;
        while (upgradeTo <= newVersion) {
            /*switch (upgradeTo)
            {
                case 2:
                    database.execSQL("ALTER TABLE " + DEADLIFT + " ADD COLUMN " + NEW_COLUMN_NAME + TYPE);
                    database.execSQL("ALTER TABLE " + BENCH + " ADD COLUMN " + NEW_COLUMN_NAME + TYPE);
                    database.execSQL("ALTER TABLE " + SQUAT + " ADD COLUMN " + NEW_COLUMN_NAME + TYPE);
                    database.execSQL("ALTER TABLE " + PRESS + " ADD COLUMN " + NEW_COLUMN_NAME + TYPE);
                    break;
            }*/
            upgradeTo++;
        }
    }

    /*
     * // Common column names
    KEY_ID | PRODUCT | DRINK_VOLUME | CAFFEINE_MASS | DATE_CREATED | TIME_STARTED
     */
    // Table Create Statement
    public String createTable(String TABLE) {

        String create = "CREATE TABLE "
                + TABLE + " ( " + CommonConstants.KEY_ID + " INTEGER PRIMARY KEY, " + CommonConstants.PRODUCT
                + " TEXT, " + CommonConstants.DRINK_VOLUME + " REAL, " + CommonConstants.CAFFEINE_MASS + " REAL, " + CommonConstants.DATE_CREATED + " INTEGER, " + CommonConstants.TIME_STARTED + " INTEGER" +" )";

        return create;
    }

    /*
    KEY_ID | PRODUCT | DRINK_VOLUME | CAFFEINE_MASS | DATE_CREATED | TIME_STARTED
     */
    public void insertRecord(HashMap<String, String> queryValues) {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(CommonConstants.PRODUCT, queryValues.get(CommonConstants.PRODUCT));
        values.put(CommonConstants.DRINK_VOLUME, queryValues.get(CommonConstants.DRINK_VOLUME));
        values.put(CommonConstants.CAFFEINE_MASS, queryValues.get(CommonConstants.CAFFEINE_MASS));
        values.put(CommonConstants.DATE_CREATED, queryValues.get(CommonConstants.DATE_CREATED));
        values.put(CommonConstants.TIME_STARTED, queryValues.get(CommonConstants.TIME_STARTED));

        database.insert(CommonConstants.HISTORY_TABLE, null, values);

        database.close();

    }

    public void deleteRecord(String id) {

        SQLiteDatabase database = this.getWritableDatabase();

        String deleteQuery = "DELETE FROM " + CommonConstants.HISTORY_TABLE + " WHERE " + CommonConstants.KEY_ID + " = '" + id + "'";

        database.execSQL(deleteQuery);

    }

    void deleteAllData(String TABLE) {
        SQLiteDatabase sdb = this.getWritableDatabase();
        sdb.delete(TABLE, null, null);
    }

    /*
    KEY_ID | PRODUCT | DRINK_VOLUME | CAFFEINE_MASS | DATE_CREATED | TIME_STARTED
     */
    public ArrayList<HashMap<String, String>> getAllRecords() {

        ArrayList<HashMap<String, String>> recordArrayList = new ArrayList<HashMap<String, String>>();
        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + CommonConstants.HISTORY_TABLE + " ORDER BY " + CommonConstants.DATE_CREATED + " DESC, " + CommonConstants.TIME_STARTED + " DESC";

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> recordMap = new HashMap<String, String>();

                recordMap.put(CommonConstants.KEY_ID, cursor.getString(cursor.getColumnIndex(CommonConstants.KEY_ID)));
                recordMap.put(CommonConstants.PRODUCT, cursor.getString(cursor.getColumnIndex(CommonConstants.PRODUCT)));
                recordMap.put(CommonConstants.DRINK_VOLUME, cursor.getString(cursor.getColumnIndex(CommonConstants.DRINK_VOLUME)));
                recordMap.put(CommonConstants.CAFFEINE_MASS, cursor.getString(cursor.getColumnIndex(CommonConstants.CAFFEINE_MASS)));
                recordMap.put(CommonConstants.DATE_CREATED, cursor.getString(cursor.getColumnIndex(CommonConstants.DATE_CREATED)));
                recordMap.put(CommonConstants.TIME_STARTED, cursor.getString(cursor.getColumnIndex(CommonConstants.TIME_STARTED)));

                recordArrayList.add(recordMap);

            } while (cursor.moveToNext());
        }
        return recordArrayList;
    }

    public HashMap<String, String> getRecordInfo(String id){

        HashMap<String, String> recordMap = new HashMap<String, String>();
        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM "+CommonConstants.HISTORY_TABLE+" WHERE "+CommonConstants.KEY_ID+" = '" + id + "'";

        Cursor cursor = database.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do{
                recordMap.put(CommonConstants.KEY_ID, cursor.getString(cursor.getColumnIndex(CommonConstants.KEY_ID)));
                recordMap.put(CommonConstants.PRODUCT, cursor.getString(cursor.getColumnIndex(CommonConstants.PRODUCT)));
                recordMap.put(CommonConstants.DRINK_VOLUME, cursor.getString(cursor.getColumnIndex(CommonConstants.DRINK_VOLUME)));
                recordMap.put(CommonConstants.CAFFEINE_MASS, cursor.getString(cursor.getColumnIndex(CommonConstants.CAFFEINE_MASS)));
                recordMap.put(CommonConstants.DATE_CREATED, cursor.getString(cursor.getColumnIndex(CommonConstants.DATE_CREATED)));
                recordMap.put(CommonConstants.TIME_STARTED, cursor.getString(cursor.getColumnIndex(CommonConstants.TIME_STARTED)));

            }while(cursor.moveToNext());
        }
        return recordMap;
    }




    public HashMap<String, String> getRecordInfoProduct(String product){

        HashMap<String, String> recordMap = new HashMap<String, String>();
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM product_table WHERE "+CommonConstants.C_PRODUCT+" = '" + product + "'";

        Cursor cursor = database.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do{
                recordMap.put(CommonConstants.C_POSITION, cursor.getString(cursor.getColumnIndex(CommonConstants.C_POSITION)));
                recordMap.put(CommonConstants.C_PRODUCT, cursor.getString(cursor.getColumnIndex(CommonConstants.C_PRODUCT)));
                recordMap.put(CommonConstants.C_VOLUME_DRINK, cursor.getString(cursor.getColumnIndex(CommonConstants.C_VOLUME_DRINK)));
                recordMap.put(CommonConstants.C_MASS_CAFFEINE, cursor.getString(cursor.getColumnIndex(CommonConstants.C_MASS_CAFFEINE)));
                recordMap.put(CommonConstants.C_DENSITY_CAFFEINE, cursor.getString(cursor.getColumnIndex(CommonConstants.C_DENSITY_CAFFEINE)));

            }while(cursor.moveToNext());
        }
        return recordMap;
    }

    // Read records related to the search term
    public List<String> read(String searchTerm) {

        List<String> coffeeList = new ArrayList<String>();
        SQLiteDatabase database = this.getReadableDatabase();
        // select query
        String query = "SELECT * FROM product_table "+
                "WHERE " + CommonConstants.C_PRODUCT + " LIKE '%" + searchTerm + "%'" + " "+
                "ORDER BY " + CommonConstants.C_MASS_CAFFEINE + " DESC "+
                "LIMIT 0,5";


        /*String query2 = "select name from sqlite_master where type = 'table'";
        // execute the query
        Cursor c = mDb.rawQuery(query2, null);

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                Log.d("DBAdapter-cursor", "Table Name=> "+c.getString(0));
                c.moveToNext();
            }
        }*/
        Cursor cursor = database.rawQuery(query, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String product = cursor.getString(cursor.getColumnIndex(CommonConstants.C_PRODUCT));
                // add to list
                coffeeList.add(product);

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        // return the list of records
        return coffeeList;
    }

    private void alert(String s) {
        Log.d(TAG, s);
    }




}
