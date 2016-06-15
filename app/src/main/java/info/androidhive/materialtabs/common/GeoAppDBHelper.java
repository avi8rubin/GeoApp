package info.androidhive.materialtabs.common;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.parse.ParseGeoPoint;

import java.util.Date;

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
    //private static final String UserID = "UserID";
    //private static final String SystemID = "SystemID";
    private static final String Email = "Email";
    private static final String Password = "Password";
    private static final String FirstName = "FirstName";
    private static final String LastName = "LastName";
    private static final String Phone = "Phone";
    private static final String Role = "Role";
    private static final String CompanyName = "CompanyName";
    //private static final String CompanyCode = "CompanyCode";

    //Shift columns
    private static final String SystemID = "SystemID";
    private static final String CompanyCode = "CompanyCode";
    private static final String UserID = "UserID";
    private static final String UserEmail = "UserEmail";
    private static final String EnterTime = "EnterTime";
    private static final String ExitTime = "ExitTime";
    private static final String EnterLocation = "EnterLocation";
    private static final String ExitLocation = "ExitLocation";
    private static final String ShiftStatus = "ShiftStatus";
    private static final String Duration = "Duration";

    //Company columns
    //private static final String CompanyCode = "CompanyCode";
    //private static final String CompanyName = "CompanyName";
    private static final String ManagerID = "ManagerID";
    private static final String ManagerEmail = "ManagerEmail";
    private static final String CompanyAddress = "CompanyAddress";
    private static final String Location = "Location";
    private static final String CreateDate = "CreateDate";

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
