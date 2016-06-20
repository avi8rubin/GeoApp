package info.androidhive.materialtabs.GeoObjects;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.internal.ParcelableSparseArray;

import info.androidhive.materialtabs.common.Status;

import com.parse.ParseObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by zinoo on 31/05/2016.
 */
public class User implements Serializable {
    private int UserID;
    private String SystemID = "";
    private String Email;
    private String Password;
    private String FirstName;
    private String LastName;
    private String Phone;
    private String Role;
    private String CompanyName = "";
    private String CompanyCode = "";
    private Boolean AutoLogin = false;
    private ArrayList<Shift> UserShifts= new ArrayList<Shift>();

    public User(){
    }
    public User(ParseObject po){
        setParseObject(po);
    }
    public void addShift(Shift shift){
        UserShifts.add(shift);
    }
    public ArrayList<Shift> getUserShifts(){
        return UserShifts;
    }
    public int getUserID() {
        return UserID;
    }
    public void setUserID(int userID) {
        UserID = userID;
    }
    public void setUserID(String userID) {
        UserID = Integer.parseInt(userID.trim());
    }
    public String getEmail() {
        return Email;
    }
    public void setEmail(String email) {
        Email = email.trim();
    }
    public String getPassword() {
        return Password;
    }
    public void setPassword(String password) {
        Password = password.trim();
    }
    public String getFirstName() {
        return FirstName;
    }
    public void setFirstName(String firstName) {
        FirstName = firstName.trim();
    }
    public String getLastName() {
        return LastName;
    }
    public void setLastName(String lastName) {
        LastName = lastName.trim();
    }
    public String getPhone() {
        return Phone;
    }
    public void setPhone(String phone) {
        Phone = phone.trim();
    }
    public Status getRole() {
        if(Role.equals("Manager"))
            return Status.MANAGER;
        return Status.WORKER;
    }
    public void setRole(Status myRole) {
        if(Status.MANAGER == myRole) {
            Role = "Manager";
        }
        else Role = "Worker";
    }
    public String getSystemID() {
        return SystemID;
    }
    public ParseObject getParseObject(){
        final ParseObject po = new ParseObject("Users");
        po.put("email",Email);
        po.put("user_password",Password);
        po.put("first_name",FirstName);
        po.put("last_name",LastName);
        po.put("user_tz",String.valueOf(UserID));
        po.put("user_phone_number",Phone);
        po.put("user_role",Role);
        po.put("CompanyName",CompanyName);
        po.put("CompanyCode",CompanyCode);
        return po;
    }
    public void setParseObject(ParseObject po){
        SystemID = po.getObjectId();
        Email = po.getString("email");
        Password = po.getString("user_password");
        FirstName = po.getString("first_name");
        LastName = po.getString("last_name");
        UserID = po.getInt("user_tz");
        Phone = po.getString("user_phone_number");
        Role = po.getString("user_role");
        CompanyName = po.getString("CompanyName");
        CompanyCode = po.getString("CompanyCode");
    }
    public String getCompanyName() {
        return CompanyName;
    }
    public void setCompanyName(String companyName) {
        CompanyName = companyName.trim();
    }
    public String getCompanyCode() {
        return CompanyCode;
    }
    public void setCompanyCode(String companyCode) {
        CompanyCode = companyCode.trim();
    }
    public Boolean isManager(){
        return Role.equals("Manager");
    }
    public Boolean isWorker(){ return Role.equals("Worker"); }
    public void setIsManager(){Role = "Manager";}
    public void setIsWorker(){Role = "Worker";}
    public void setSystemID(String systemID){
        SystemID = systemID.trim();
    }
    public Boolean hasID(){
        return !SystemID.equals("");
    }
    public void AutoLoginOn(){AutoLogin = true;}
    public void AutoLoginOff(){AutoLogin = false;}
    public Boolean getAutoLogin(){return AutoLogin;}

    public ContentValues getContentValues(){
        ContentValues CV = new ContentValues();
        CV.put("SystemID",SystemID);
        CV.put("Email",Email);
        CV.put("Password",Password);
        CV.put("FirstName",FirstName);
        CV.put("LastName",LastName);
        CV.put("UserTZ",UserID);
        CV.put("Phone",Phone);
        CV.put("Role",Role);
        CV.put("CompanyName",CompanyName);
        CV.put("CompanyCode",CompanyCode);
        return CV;
    }

 /*   @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(SystemID);
        dest.writeString(Email);
        dest.writeString(Password);
        dest.writeString(FirstName);
        dest.writeString(LastName);
        dest.writeInt(UserID);
        dest.writeString(Phone);
        dest.writeString(Role);
        dest.writeString(CompanyName);
        dest.writeString(CompanyCode);
    }
    private User(Parcel in){
        SystemID = in.readString();
        Email = in.readString();
        Password = in.readString();
        FirstName = in.readString();
        LastName = in.readString();
        UserID = in.readInt();
        Phone = in.readString();
        Role = in.readString();
        CompanyName = in.readString();
        CompanyCode = in.readString();
    }
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };*/
}
