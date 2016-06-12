package info.androidhive.materialtabs.GeoObjects;

import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import info.androidhive.materialtabs.common.Status;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by zinoo on 31/05/2016.
 */
public class Shift implements Serializable {
    private String SystemID = "";
    private String CompanyCode;
    private String UserID;
    private String UserEmail;
    private Date EnterTime;
    private Date ExitTime;
    private ParseGeoPoint EnterLocation = new ParseGeoPoint();
    private ParseGeoPoint ExitLocation = new ParseGeoPoint();
    private int ShiftStatus; //1=ENTER , 2=EXIT , 3=CLOSE
    private long Duration;

    public Shift(){
        SystemID = "";
        CompanyCode="";
        UserID="";
        UserEmail="";
        EnterTime=new Date(0);
        ExitTime=new Date(0);
        EnterLocation = new ParseGeoPoint();
        ExitLocation = new ParseGeoPoint();
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
        return EnterLocation;
    }
    public void setEnterLocation(ParseGeoPoint location) {
        EnterLocation = location;
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
        po.put("EnterLocation",EnterLocation);
        po.put("ExitLocationLatitude",ExitLocation.getLatitude());
        po.put("ExitLocationLongitude",ExitLocation.getLongitude());
        return po;
    }
    public void setParseObject(ParseObject po){
        SystemID = po.getObjectId();
        CompanyCode = po.getString("company_id");
        UserID = po.getString("User_ID");
        UserEmail = po.getString("user_email");
        EnterLocation = po.getParseGeoPoint("EnterLocation");
        EnterTime = po.getDate("enter_time");
        ExitTime = po.getDate("exit_time");
        ShiftStatus = po.getInt("ShiftStatus");
        Duration = po.getLong("Duration");
        if((po.getDouble("ExitLocationLatitude") != 0) || (po.getDouble("ExitLocationLongitude") != 0)) {
            ExitLocation.setLatitude(po.getDouble("ExitLocationLatitude"));
            ExitLocation.setLongitude(po.getDouble("ExitLocationLongitude"));
        }
        else ExitLocation = null;
    }
    public String getSystemID() {
        return SystemID;
    }
    public void setSystemID(String systemID) { SystemID = systemID.trim(); }
    public ParseGeoPoint getExitLocation() {
        return ExitLocation;
    }

    public void setExitLocation(ParseGeoPoint exitLocation) {
        ExitLocation = exitLocation;
    }
    public void setEnterTimeNow(){
        EnterTime = (Calendar.getInstance()).getTime();
    }
    public void setExitTimeNow(){
        ExitTime = (Calendar.getInstance()).getTime();
    }
}
