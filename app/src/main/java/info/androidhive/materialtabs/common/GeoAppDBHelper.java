package info.androidhive.materialtabs.common;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.parse.ParseGeoPoint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import info.androidhive.materialtabs.R;
import info.androidhive.materialtabs.activity.IconTabsActivity;


/**
 * Created by zinoo on 11/06/2016.
 */
public class GeoAppDBHelper extends SQLiteOpenHelper {
    //Database Settings
    private static final String DB_NAME = "GeoDB";
    private static final int DB_VERTION = 1;
    //Database Tables
    private static final String SETTINGS = "Setting";
    private static final String USER = "";
    private static final String COMPANY = "";
    private static final String SHIFT = "";
    //User columns
    private static final String User_active = "User_active";
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
    private static final String ShiftBuffer="ShiftBuffer";
    private static final String SystemID = "SystemID";
    private static final String CompanyCode = "CompanyCode";
    private static final String UserID = "UserID";
    private static final String UserEmail = "UserEmail";
    private static final String EnterTime = "shift_start_time";
    private static final String ExitTime = "shift_end_time";
    private static final String EnterLocation = "EnterLocation";
    private static final String ExitLocation_LNG = "shift_exit_lng";
    private static final String ExitLocation_LAT = "shift_exit_lat";
    private static final String ShiftStatus = "Shift_status";
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
    public boolean isUserWasAtive(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+SETTINGS+" WHERE "+User_active+" = 1;", null);
        res.moveToFirst();
        return res.getCount() > 0;
    }
    public boolean isShiftActive(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+ShiftBuffer+" WHERE "+ShiftStatus+" = 1;", null);
        res.moveToFirst();
        return res.getCount() > 0;
    }
    public String get_User_Start_shift() {
        SQLiteDatabase db = this.getReadableDatabase();
            Cursor resultSet = db.rawQuery("SELECT " + EnterTime + " FROM " + ShiftBuffer + " WHERE " + ShiftStatus + " = 1;", null);
            if (resultSet.getCount() > 0) {
                resultSet.moveToNext();
                String l = resultSet.getString(0);
                return l;
            }
        return "";
    }
    public Cursor getShift(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM "+ShiftBuffer+";", null);
    }
    public void EnterNewShift(String SystemID){
        SQLiteDatabase db = this.getReadableDatabase();
        String time = getDateTime();
        db.execSQL("INSERT INTO "+ShiftBuffer+" VALUES(NULL,1,'" + time
                + "',NULL,NULL,NULL,NULL,NULL,'"+SystemID+"');");
    }
    public void EnterNewShift(double lat,double lng,String SystemID){
        SQLiteDatabase db = this.getReadableDatabase();
        String time = getDateTime();
        db.execSQL("INSERT INTO "+ShiftBuffer+""
                + " VALUES(NULL,1,'" + time + "',NULL,'" + String.valueOf(lat)
                + "','" + String.valueOf(lng)  + "',NULL,NULL,'"
                +SystemID+"');");
    }
    public void ExitShift()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String time = getDateTime();
        db.execSQL("UPDATE "+ShiftBuffer+" SET "+ShiftStatus+"=0 " +
                ","+ExitTime+"='" + time + "',"+ExitLocation_LNG+"=NULL,"+ExitLocation_LAT+"=NULL WHERE "+ShiftStatus+"=1;");
    }
    public void ExitShift(double lat,double lng)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String time = getDateTime();
        db.execSQL("UPDATE "+ShiftBuffer+" SET "+ShiftStatus+"=0 " +
                ","+ExitTime+"='"+time+"',"+ExitLocation_LNG+"="+String.valueOf(lat)+","+ExitLocation_LAT
                +"="+String.valueOf(lng)+ "WHERE "+ShiftStatus+"=1;");
    }
    public Date getShiftEnterTime()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor resultSet =db.rawQuery("SELECT " + EnterTime + " FROM "+ShiftBuffer
                + " WHERE " + ShiftStatus + "=1;", null);
        if (resultSet.getCount() > 0) {
            resultSet.moveToNext();
            String l = resultSet.getString(0);
            return getDateTIme(l);
        }
        return new Date(System.currentTimeMillis()) ;
    }




    public String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
    public Date getDateTIme(String str){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }
}
