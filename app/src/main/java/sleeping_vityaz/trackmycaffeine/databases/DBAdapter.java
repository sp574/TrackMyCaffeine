package sleeping_vityaz.trackmycaffeine.databases;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import sleeping_vityaz.trackmycaffeine.util.CommonConstants;

public class DBAdapter
{
    protected static final String TAG = "DataAdapter";
    //private static String DB_NAME ="coffee_db";// Database name

    private final Context mContext;
    private SQLiteDatabase mDb;
    private DataBaseHelper mDbHelper;

    public DBAdapter(Context context)
    {
        this.mContext = context;
        mDbHelper = new DataBaseHelper(mContext);
    }

    public DBAdapter createDatabase() throws SQLException
    {
        try
        {
            mDbHelper.createDataBase();
        }
        catch (IOException mIOException)
        {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public DBAdapter open() throws SQLException
    {
        try
        {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "open >>"+ mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }

    public void close()
    {
        mDbHelper.close();
    }

    public Cursor getTestData()
    {
        try
        {
            String sql ="SELECT * FROM myTable";

            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur!=null)
            {
                mCur.moveToNext();
            }
            return mCur;
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "getTestData >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }

    public HashMap<String, String> getRecordInfo(String product){

        HashMap<String, String> recordMap = new HashMap<String, String>();

        String selectQuery = "SELECT * FROM coffee_table WHERE "+CommonConstants.C_PRODUCT+" = '" + product + "'";

        Cursor cursor = mDb.rawQuery(selectQuery, null);

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

        // select query
        String query = "SELECT * FROM coffee_table "+
                "WHERE " + CommonConstants.C_PRODUCT + " LIKE '%" + searchTerm + "%'" + " "+
                "ORDER BY " + CommonConstants.C_PRODUCT + " ASC ";//+
                //"LIMIT 0,5";


        String query2 = "select name from sqlite_master where type = 'table'";
        // execute the query
        Cursor c = mDb.rawQuery(query2, null);

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                Log.d("DBAdapter-cursor", "Table Name=> "+c.getString(0));
                c.moveToNext();
            }
        }
        Cursor cursor = mDb.rawQuery(query, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String product = cursor.getString(cursor.getColumnIndex(CommonConstants.C_PRODUCT));
                // add to list
                coffeeList.add(product);

            } while (cursor.moveToNext());
        }

        cursor.close();
        mDb.close();

        // return the list of records
        return coffeeList;
    }
}