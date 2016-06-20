package info.androidhive.materialtabs.common;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.parse.ParseGeoPoint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.Locale;

import info.androidhive.materialtabs.GeoObjects.Company;
import info.androidhive.materialtabs.GeoObjects.Shift;
import info.androidhive.materialtabs.GeoObjects.User;
import info.androidhive.materialtabs.R;
import info.androidhive.materialtabs.activity.IconTabsActivity;


/**
 * Created by zinoo on 11/06/2016.
 */
public class GeoAppDBHelper extends SQLiteOpenHelper {
    //Database Settings
    private static final String DB_NAME = "GeoDB";
    private static final int DB_VERSION = 1;
    //Database Tables
    private static final String SETTINGS = "Settings";
    private static final String USER = "User";
    private static final String COMPANY = "Company";
    private static final String SHIFT = "Shift";

    //Settings column
    private static final String LastUserID = "LastUserID";
    private static final String LastCompanyID = "LastCompanyCode";

    //User columns
    //private static final String User_active = "User_active";//XXXXXXXXX
    //private static final String SystemID = "SystemID";    //PK
    private static final String Email = "Email";
    private static final String Password = "Password";
    private static final String FirstName = "FirstName";
    private static final String LastName = "LastName";
    private static final String UserTZ = "UserTZ";
    private static final String Phone = "Phone";
    private static final String Role = "Role";
    //private static final String CompanyName = "CompanyName";
    //private static final String CompanyCode = "CompanyCode";
    private static final String AutoLogin = "AutoLogin";


    //Shift columns
    //private static final String ShiftBuffer="ShiftBuffer";
    private static final String SystemID = "SystemID";      //PK
    //private static final String CompanyCode = "CompanyCode";
    private static final String UserID = "UserID";
    private static final String UserEmail = "UserEmail";
    private static final String EnterTime = "EnterTime";
    private static final String ExitTime = "ExitTime";
    private static final String EnterLocation_LNG = "EnterLocation_LNG";
    private static final String EnterLocation_LAT = "EnterLocation_LAT";
    private static final String ExitLocation_LNG = "ExitLocation_LNG";
    private static final String ExitLocation_LAT = "ExitLocation_LAT";
    private static final String ShiftStatus = "ShiftStatus";
    private static final String Duration = "Duration";


    //Company columns
    private static final String CompanyCode = "CompanyCode";    //PK
    private static final String CompanyName = "CompanyName";
    private static final String ManagerID = "ManagerID";
    private static final String ManagerEmail = "ManagerEmail";
    private static final String CompanyAddress = "CompanyAddress";
    private static final String Location_LNG = "Location_LNG";
    private static final String Location_LAT = "Location_LAT";
    private static final String CreateDate = "CreateDate";

    //Other
    private String SQLQuery;
    private SQLiteDatabase DB;
    private String[] ALL = new String[]{"*"};

    public GeoAppDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        //SQLQuery = ReadFile.getStringFromFile(context.getResources().openRawResource(R.raw.sqlite_db_geoapp));
        context.deleteDatabase(DB_NAME);
        SQLQuery = ReadFile.getStringFromFile(context.getResources().openRawResource(R.raw.geoapp_db));
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(SQLQuery);
        readQueryFromFile(db);
//        DB = db;
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
/*    public boolean isUserWasActive(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+SHIFT+" WHERE "+User_active+" = 1;", null);
        res.moveToFirst();
        return res.getCount() > 0;
    }*/
    public boolean isShiftActive(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+SHIFT+" WHERE "+ShiftStatus+" = 1;", null);
        res.moveToFirst();
        return res.getCount() > 0;
    }
    public String get_User_Start_shift() {
        SQLiteDatabase db = this.getReadableDatabase();
            Cursor resultSet = db.rawQuery("SELECT " + EnterTime + " FROM " + SHIFT + " WHERE " + ShiftStatus + " = 1;", null);
            if (resultSet.getCount() > 0) {
                resultSet.moveToNext();
                String l = resultSet.getString(0);
                return l;
            }
        return "";
    }
    public Cursor getShift(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM "+SHIFT+";", null);
    }
    public void EnterNewShift(String SystemID){
        SQLiteDatabase db = this.getReadableDatabase();
        String time = getDateTime();
        db.execSQL("INSERT INTO "+SHIFT+" VALUES(NULL,1,'" + time
                + "',NULL,NULL,NULL,NULL,NULL,'"+SystemID+"');");
    }
    public void EnterNewShift(double lat,double lng,String SystemID){
        SQLiteDatabase db = this.getReadableDatabase();
        String time = getDateTime();
        db.execSQL("INSERT INTO "+SHIFT+""
                + " VALUES(NULL,1,'" + time + "',NULL,'" + String.valueOf(lat)
                + "','" + String.valueOf(lng)  + "',NULL,NULL,'"
                +SystemID+"');");
    }
    public void ExitShift()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String time = getDateTime();
        db.execSQL("UPDATE "+SHIFT+" SET "+ShiftStatus+"=0 " +
                ","+ExitTime+"='" + time + "',"+ExitLocation_LNG+"=NULL,"+ExitLocation_LAT+"=NULL WHERE "+ShiftStatus+"=1;");
    }
    public void ExitShift(double lat,double lng)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String time = getDateTime();
        db.execSQL("UPDATE "+SHIFT+" SET "+ShiftStatus+"=0 " +
                ","+ExitTime+"='"+time+"',"+ExitLocation_LNG+"="+String.valueOf(lat)+","+ExitLocation_LAT
                +"="+String.valueOf(lng)+ "WHERE "+ShiftStatus+"=1;");
    }
    public Date getShiftEnterTime()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor resultSet =db.rawQuery("SELECT " + EnterTime + " FROM "+SHIFT
                + " WHERE " + ShiftStatus + "=1;", null);
        if (resultSet.getCount() > 0) {
            resultSet.moveToNext();
            String l = resultSet.getString(0);
            return getDateTime(l);
        }
        return new Date(System.currentTimeMillis()) ;
    }




    public String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
    public Date getDateTime(String str){
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

    public Cursor getAllRowsFromTable(String tableName){
        if(DB==null || !DB.isOpen()) //Open connection to sqlite database if not already opened
            DB = this.getReadableDatabase();
        return DB.rawQuery("SELECT * FROM ?;", new String[]{tableName});
    }

    /**
     * Get update or insert and return the number of rows that affected or the new row number in the table
     * @param tableName - table name
     * @param CV - values to insert or update
     * @param Where - use just on update
     * @param WhereValues - use just on update
     * @return return the number of rows that affected or the new row number in the table
     */
    private int genericUpsert (String tableName, ContentValues CV, String Where, String[] WhereValues){
        if(DB==null || !DB.isOpen() || DB.isReadOnly()) //Open connection to sqlite database if not already opened
            DB = this.getWritableDatabase();
        //Check if object already exists
        if(Globals.isEmptyOrNull(Where)){
            // Insert new row
            return (int)DB.insert(tableName,null,CV);
        }
        else{
            // Update exists row
            return DB.update(tableName,CV,Where,WhereValues);
        }
    }
    private Cursor genericSelect(String tableName, String[] columnName, String whereStatement,String[] WhereValues){
        if(DB==null || !DB.isOpen()) //Open connection to sqlite database if not already opened
            DB = this.getReadableDatabase();
        SQLQuery = "SELECT "+Globals.stringArrayToString(columnName,",")+" FROM "+tableName+" ";
        if(Globals.isEmptyOrNull(whereStatement))
            return DB.rawQuery(SQLQuery+whereStatement+";",WhereValues);
        else return DB.rawQuery(SQLQuery+";",null);
    }
    public void insert(User user){
        genericUpsert(USER,user.getContentValues(),null,null);
    }
    public void insert(Company company){
        genericUpsert(COMPANY,company.getContentValues(),null,null);
    }
    public void insert(Shift shift){
        genericUpsert(SHIFT,shift.getContentValues(),null,null);
    }
    public void update(User user){
        genericUpsert(USER,user.getContentValues(),"SystemID = ?",new String[]{user.getSystemID()});
    }
    public void update(User user, ContentValues CV){
        genericUpsert(USER,CV,"SystemID = ?",new String[]{user.getSystemID()});
    }
    public void update(Company company){
        genericUpsert(COMPANY,company.getContentValues(),"CompanyCode = ?",new String[]{company.getCompanyCode()});
    }
    public void update(Company company, ContentValues CV){
        genericUpsert(COMPANY,CV,"CompanyCode = ?",new String[]{company.getCompanyCode()});
    }
    public void update(Shift shift){
        genericUpsert(SHIFT,shift.getContentValues(),"SystemID = ?",new String[]{shift.getSystemID()});
    }
    public void update(Shift shift, ContentValues CV){
        genericUpsert(SHIFT,CV,"SystemID = ?",new String[]{shift.getSystemID()});
    }
    public Object select(User user){ //Get user object by email
        Cursor c = genericSelect(USER,ALL,"Email = ?",new String[]{user.getEmail()});
        return getStandardUserFromCursor(c);
    }
    public Object getLastLoginUser(){
        String SQLQuery = "SELECT A.* FROM User A JOIN Settings B ON A.SystemID=B.LastUserID";
        if(DB==null || !DB.isOpen()) //Open connection to sqlite database if not already opened
           DB = this.getReadableDatabase();
        Cursor c = DB.rawQuery(SQLQuery,null);
        return getStandardUserFromCursor(c);
    }
    public void setLastLoginUser(User user){
        if(DB==null || !DB.isOpen() || DB.isReadOnly()  ) //Open connection to sqlite database if not already opened
            DB = this.getWritableDatabase();
        DB.execSQL("DELETE FROM "+SETTINGS);
        ContentValues CV = new ContentValues();
        CV.put("LastUserID",user.getSystemID());
        CV.put("LastCompanyCode",user.getCompanyCode());
        genericUpsert(SETTINGS,CV,null,null);
    }
    private Object getStandardUserFromCursor(Cursor c){
        if (c.getCount() > 0) {
            User user = new User();
            c.moveToFirst();
            user.setSystemID(c.getString(0));
            user.setEmail(c.getString(1));
            user.setPassword(c.getString(2));
            user.setFirstName(c.getString(3));
            user.setLastName(c.getString(4));
            user.setUserID(c.getInt(5));
            user.setPhone(c.getString(6));
            if (c.getString(7).equals("Manager")) user.setIsManager();
            else user.setIsWorker();
            user.setCompanyName(c.getString(8));
            user.setCompanyCode(c.getString(9));
            if(c.getInt(10) > 0) user.AutoLoginOn();
            return user;
        } else return false;
    }
    private Object getStandardCompanyFromCursor(Cursor c){
        if (c.getCount() > 0) {
            Company company = new Company();
            c.moveToFirst();
            company.setCompanyCode(c.getString(0));
            company.setCompanyName(c.getString(1));
            company.setManagerID(c.getString(2));
            company.setManagerEmail(c.getString(3));
            company.setCompanyAddress(c.getString(4));
            company.setLocation(new ParseGeoPoint(c.getInt(6),c.getInt(5)));
            if(!Globals.isEmptyOrNull(c.getString(7)))
                company.setCreateDate(getDateTime(c.getString(7)));
            return company;
        } else return false;
    }
    public Object getCompanyByCompanyID(String CompanyID){
        Cursor c = genericSelect(COMPANY,ALL,"CompanyCode = ?",new String[]{CompanyID});
        return getStandardCompanyFromCursor(c);
    }
}
