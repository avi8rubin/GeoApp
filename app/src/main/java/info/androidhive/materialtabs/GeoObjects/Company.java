package info.androidhive.materialtabs.GeoObjects;

import android.content.ContentValues;

import com.parse.ParseObject;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import info.androidhive.materialtabs.common.Globals;

/**
 * Created by zinoo on 31/05/2016.
 */
public class Company implements Serializable {
    private String CompanyCode;
    private String CompanyName;
    private String ManagerID;
    private String ManagerEmail;
    private String CompanyAddress;
    private Double Location_LAT;
    private Double Location_LNG;
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

    public Double[] getLocation() {
        return new Double[]{Location_LAT,Location_LNG};
    }

    public void setLocation(Double location_LAT, Double location_LNG) {
        Location_LAT = location_LAT;
        Location_LNG = location_LNG;
    }

    public Date getCreateDate() {
        return CreateDate;
    }
    public void setCreateDate(Date createDate){ CreateDate = createDate;}

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
        po.put("CompanyAddress",CompanyAddress);
        po.put("Location_LAT",Location_LAT);
        po.put("Location_LNG",Location_LNG);
        po.put("Manager_ID",ManagerID);
        return po;
    }
    public void setParseObject(ParseObject po){
        CompanyCode = po.getObjectId();
        ManagerEmail = po.getString("Company_manager_email");
        CompanyName = po.getString("Company_name");
        CompanyAddress = po.getString("CompanyAddress");
        Location_LAT = po.getDouble("Location_LAT");
        Location_LNG = po.getDouble("Location_LNG");
        ManagerID = po.getString("Manager_ID");
        CreateDate = po.getCreatedAt();
    }

    public String getCompanyAddress() {
        return CompanyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        CompanyAddress = companyAddress.trim();
    }
    public ContentValues getContentValues(){
        ContentValues CV = new ContentValues();
        CV.put("CompanyCode",CompanyCode);
        CV.put("CompanyName",CompanyName);
        CV.put("ManagerID",ManagerID);
        CV.put("ManagerEmail",ManagerEmail);
        CV.put("CompanyAddress",CompanyAddress);
        CV.put("Location_LAT",Location_LAT);
        CV.put("Location_LNG",Location_LNG);
        CV.put("CreateDate", Globals.getDateTimeToString(CreateDate));
        return CV;
    }

}
