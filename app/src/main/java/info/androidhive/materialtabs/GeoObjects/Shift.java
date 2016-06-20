package info.androidhive.materialtabs.GeoObjects;

import android.content.ContentValues;

import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import info.androidhive.materialtabs.common.Globals;
import info.androidhive.materialtabs.common.Status;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by zinoo on 31/05/2016.
 */
public class Shift implements Serializable {
    private String SystemID = "";
    private String CompanyCode;
    private String UserID;
    private String UserEmail;
    private Date EnterTime;
    private Date ExitTime = null;
    private Double EnterLocation_LAT;
    private Double EnterLocation_LNG;
    private Double ExitLocation_LAT;
    private Double ExitLocation_LNG;
    //private ParseGeoPoint EnterLocation = new ParseGeoPoint();
    //private ParseGeoPoint ExitLocation = new ParseGeoPoint();
    private int ShiftStatus; //1=ENTER , 2=EXIT , 3=CLOSE
    private long Duration;

    public Shift(){
        SystemID = "";
        CompanyCode="";
        UserID="";
        UserEmail="";
        EnterTime=new Date(0);
        ExitTime=new Date(0);
        EnterLocation_LAT = 0.0;
        EnterLocation_LNG = 0.0;
        ExitLocation_LAT = 0.0;
        ExitLocation_LNG = 0.0;
        //EnterLocation = new ParseGeoPoint();
        //ExitLocation = new ParseGeoPoint();
        ShiftStatus=0; //1=ENTER , 2=EXIT , 3=CLOSE
        Duration=0;
    }
    public Shift(ParseObject po){
        setParseObject(po);
    }
    public int getParseShiftStatus() {
        return ShiftStatus;
    }
    public Status getShiftStatus() {
        switch (ShiftStatus){
            case 1: return Status.ENTER;
            case 2: return Status.EXIT;
            default: return Status.CLOSE;
        }
    }
    public String getDuration (){
        if(Duration == 0) return "xx:xx";
        int Hours = (int)Duration/(1000 * 60 * 60);
        int Mins = (60 % ((int)Duration / (1000*60)));
        return Hours + ":" + Mins;
    }
    public void setShiftStatus(Status shiftStatus) {
        switch (shiftStatus){
            case ENTER: ShiftStatus = 1; break;
            case EXIT: ShiftStatus = 2; break;
            default: ShiftStatus = 3;
        }
    }

    public String getCompanyCode() {
        return CompanyCode;
    }
    public void setCompanyCode(String companyCode) {
        CompanyCode = companyCode.trim();
    }
    public String getUserID() {
        return UserID;
    }
    public void setUserID(String userID) {
        UserID = userID.trim();
    }
    public String getUserEmail() {
        return UserEmail;
    }
    public void setUserEmail(String userEmail) {
        UserEmail = userEmail.trim();
    }
    public Date getEnterTime() {
        return EnterTime;
    }
    public void setEnterTime(Date enterTime) {
        EnterTime = enterTime;
    }
    public Date getExitTime() {
        return ExitTime;
    }
    public void setExitTime(Date exitTime) {
        ExitTime = exitTime;
        Duration = exitTime.getTime() - EnterTime.getTime();
    }
    public ParseGeoPoint getEnterLocation() {
        return new ParseGeoPoint(EnterLocation_LAT,EnterLocation_LNG);
    }
    public void setEnterLocation(ParseGeoPoint location) {
        EnterLocation_LAT = location.getLatitude();
        EnterLocation_LNG = location.getLongitude();
    }
    public ParseObject getParseObject(){
        ParseObject po = new ParseObject("Users_shifts");
        po.put("company_id",CompanyCode);
        po.put("User_ID",UserID);
        po.put("user_email",UserEmail);
        po.put("enter_time",EnterTime);
        po.put("exit_time",ExitTime);
        po.put("ShiftStatus",ShiftStatus);
        po.put("Duration",Duration);
        po.put("EnterLocation_LAT",EnterLocation_LAT);
        po.put("EnterLocation_LNG",EnterLocation_LNG);
        po.put("ExitLocationLatitude",ExitLocation_LAT);
        po.put("ExitLocationLongitude",ExitLocation_LNG);
        return po;
    }
    public void setParseObject(ParseObject po){
        SystemID = po.getObjectId();
        CompanyCode = po.getString("company_id");
        UserID = po.getString("User_ID");
        UserEmail = po.getString("user_email");
        ParseGeoPoint EnterLocation = po.getParseGeoPoint("EnterLocation");
        if(EnterLocation != null){
            EnterLocation_LAT = EnterLocation.getLatitude();
            EnterLocation_LNG = EnterLocation.getLongitude();
        }
        EnterTime = po.getDate("enter_time");
        ExitTime = po.getDate("exit_time");
        ShiftStatus = po.getInt("ShiftStatus");
        Duration = po.getLong("Duration");
        if((po.getDouble("ExitLocationLatitude") != 0) || (po.getDouble("ExitLocationLongitude") != 0)) {
            ExitLocation_LAT = po.getDouble("ExitLocationLatitude");
            ExitLocation_LNG = po.getDouble("ExitLocationLongitude");
        }
        else {
            ExitLocation_LAT = ExitLocation_LNG = 0.0;
        }
    }
    public String getSystemID() {
        return SystemID;
    }
    public void setSystemID(String systemID) { SystemID = systemID.trim(); }
    public ParseGeoPoint getExitLocation() {
        return new ParseGeoPoint(ExitLocation_LAT,ExitLocation_LNG);
    }

    public void setExitLocation(ParseGeoPoint exitLocation) {
        ExitLocation_LAT = exitLocation.getLatitude();
        ExitLocation_LNG = exitLocation.getLongitude();
    }
    public void setEnterTimeNow(){
        EnterTime = (Calendar.getInstance()).getTime();
    }
    public void setExitTimeNow(){
        ExitTime = (Calendar.getInstance()).getTime();
        Duration = ExitTime.getTime() - EnterTime.getTime();
    }


    public ContentValues getContentValues(){
        ContentValues CV = new ContentValues();
        CV.put("SystemID",SystemID);
        CV.put("CompanyCode",CompanyCode);
        CV.put("UserID",UserID);
        CV.put("UserEmail",UserEmail);
        CV.put("EnterTime", Globals.getDateTimeToString(EnterTime));
        CV.put("ExitTime",Globals.getDateTimeToString(ExitTime));
        CV.put("EnterLocation_LAT",EnterLocation_LAT);
        CV.put("EnterLocation_LNG",EnterLocation_LNG);
        CV.put("ExitLocation_LAT",ExitLocation_LAT);
        CV.put("ExitLocation_LNG",ExitLocation_LNG);
        CV.put("ShiftStatus",ShiftStatus);
        CV.put("Duration",Duration);
        return CV;
    }
}
