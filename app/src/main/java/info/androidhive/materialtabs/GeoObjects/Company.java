package info.androidhive.materialtabs.GeoObjects;

import com.google.android.gms.vision.barcode.Barcode;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by zinoo on 31/05/2016.
 */
public class Company implements Serializable {
    private String CompanyCode;
    private String CompanyName;
    private String ManagerID;
    private String ManagerEmail;
    private String CompanyAddress;
    private ParseGeoPoint Location;
    private Date CreateDate;

    public Company(){}
    public Company(String CompanyCode, String CompanyName){
        this.CompanyCode = CompanyCode;
        this.CompanyName = CompanyName;
    }
    public Company(ParseObject po){
        setParseObject(po);
    }

    public String getCompanyCode() {
        return CompanyCode;
    }

    public void setCompanyCode(String companyCode) {
        CompanyCode = companyCode.trim();
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String companyName) {
        CompanyName = companyName.trim();
    }

    public String getManagerID() {
        return ManagerID;
    }

    public void setManagerID(String managerID) {
        ManagerID = managerID.trim();
    }

    public ParseGeoPoint getLocation() {
        return Location;
    }

    public void setLocation(ParseGeoPoint location) {
        Location = location;
    }

    public Date getCreateDate() {
        return CreateDate;
    }

    public String getManagerEmail() {
        return ManagerEmail;
    }

    public void setManagerEmail(String managerEmail) {
        ManagerEmail = managerEmail.trim();
    }

    public ParseObject getParseObject(){
        ParseObject po = new ParseObject("Company");
        po.put("Company_manager_email",ManagerEmail);
        po.put("Company_name",CompanyName);
        po.put("CompantAddress",CompanyAddress);
        if(Location != null) po.put("Company_Geo_Point",Location);
        po.put("Manager_ID",ManagerID);
        return po;
    }
    public void setParseObject(ParseObject po){
        CompanyCode = po.getObjectId();
        ManagerEmail = po.getString("Company_manager_email");
        CompanyName = po.getString("Company_name");
        CompanyAddress = po.getString("CompantAddress");
        Location = po.getParseGeoPoint("Company_Geo_Point");
        ManagerID = po.getString("Manager_ID");
        CreateDate = po.getCreatedAt();
    }

    public String getCompanyAddress() {
        return CompanyAddress;
    }

    public void setCompanyAddress(String compantAddress) {
        CompanyAddress = compantAddress.trim();
    }
}
