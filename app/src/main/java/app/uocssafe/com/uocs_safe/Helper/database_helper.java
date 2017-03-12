package app.uocssafe.com.uocs_safe.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

/**
 * Created by User on 7/2/2017.
 */

public class database_helper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "uocs";
    public static final String TABLE_NAME = "offline_table";
    public static final String col_1 = "id";
    public static final String col_2 = "urllink";
    public static final String col_3 = "method";
    public static final String col_4 = "datastore";
    public static final String col_5 = "saved_at";

    private static final String TAG = "MyActivity";

    public database_helper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase myDB) {
        myDB.execSQL("CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" " +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "urllink TEXT, " +
                    "method TEXT, " +
                    "datastore TEXT, " +
                    "saved_at INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase myDB, int i, int i1) {
        myDB.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(myDB);
    }

    public boolean insertData(String result, String url, String method)
    {
        SQLiteDatabase uocs = this.getWritableDatabase();
        Date date = new Date();
        Log.d(TAG, result);

        String updateExistingRow = "SELECT * FROM offline_table WHERE urllink = " + "\"" + url + "\"";

        Cursor cursor = uocs.rawQuery(updateExistingRow, null);

        ContentValues contentValues = new ContentValues();
        contentValues.put(col_2, url);
        contentValues.put(col_3, method);
        contentValues.put(col_4, result);
        contentValues.put(col_5, date.getTime());

        if (cursor != null && cursor.getCount() > 0) {
            uocs.update(TABLE_NAME, contentValues, "urllink = "+ "\"" + url +"\"", null);
            return true;
        }

        long saved_status = uocs.insert(TABLE_NAME, null, contentValues);
        Log.d(TAG, "status ---------------------------------" + saved_status);
        cursor.close();
        uocs.close();
        if(saved_status == -1)
            return false;
        else
            return true;
    }

    public Cursor getOfflineData (String url, String method) {
//        String[] params = new String[]{ url };
        SQLiteDatabase uocs = this.getReadableDatabase();
        //String query = "Select * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_PRODUCTNAME + " =  \"" + productname + "\"";
        String query = "SELECT * FROM offline_table WHERE urllink = \"" + url + "\"";
        Cursor res = uocs.rawQuery(query, null);
        Cursor res2 = uocs.rawQuery("select * from offline_table", null);
        Log.d(TAG, "data"+res);
        return res;
    }
//    public boolean insertData (String url, )
}
