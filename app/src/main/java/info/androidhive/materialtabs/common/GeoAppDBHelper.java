package info.androidhive.materialtabs.common;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import info.androidhive.materialtabs.R;


/**
 * Created by zinoo on 11/06/2016.
 */
public class GeoAppDBHelper extends SQLiteOpenHelper {
    //Database Settings
    private static final String DB_NAME = "GeoDB";
    private static final int DB_VERTION = 1;
    //Database Tables
    private static final String SETTINGS = "";
    private static final String USER = "";
    private static final String COMPANY = "";
    private static final String SHIFT = "";
    //User columns
    //private static final String
    private String SQLQuery;

    public GeoAppDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERTION);
        SQLQuery = ReadFile.getStringFromFile(context.getResources().openRawResource(R.raw.sqlite_db_geoapp));
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        readQueryFromFile(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    private void readQueryFromFile(SQLiteDatabase myDB){
        String[] sqlQueries = SQLQuery.split(";");
        for (String sqlQuery : sqlQueries) {
            myDB.execSQL(sqlQuery);
        }
    }
}
