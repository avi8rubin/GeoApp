package info.androidhive.materialtabs.GeoObjects;

import com.parse.ParseObject;

/**
 * Created by zinoo on 20/06/2016.
 */
public class ParseObjects {

    public static ParseObject getParseObject(User user){
        final ParseObject po = new ParseObject("Users");
        po.put("email",user.getEmail());
        po.put("user_password",user.getPassword());
        po.put("first_name",user.getFirstName());
        po.put("last_name",user.getLastName());
        po.put("user_tz",String.valueOf(user.getUserID()));
        po.put("user_phone_number",user.getPhone());
        po.put("user_role",user.getRole());
        po.put("CompanyName",user.getCompanyName());
        po.put("CompanyCode",user.getCompanyCode());
        return po;

    }
    public static ParseObject getParseObject(Company company){
        ParseObject po = new ParseObject("Company");
        po.put("Company_manager_email",company.getManagerEmail());
        po.put("Company_name",company.getCompanyName());
        po.put("CompanyAddress",company.getCompanyAddress());
        po.put("Location_LAT",company.getLocation()[0]);
        po.put("Location_LNG",company.getLocation()[1]);
        po.put("Manager_ID",company.getManagerID());
        return po;
    }
    public static User setParseObject(ParseObject po, User user){
        user.setSystemID(po.getObjectId());
        user.setEmail(po.getString("email"));
        user.setPassword(po.getString("user_password"));
        user.setFirstName(po.getString("first_name"));
        user.setLastName(po.getString("last_name"));
        user.setUserID(po.getInt("user_tz"));
        user.setPhone(po.getString("user_phone_number"));
        if(po.getString("user_role").equals("Manager"))
            user.setIsManager();
        else user.setIsWorker();
        user.setCompanyName(po.getString("CompanyName"));
        user.setCompanyCode(po.getString("CompanyCode"));
        return user;
    }
    public static Company setParseObject(ParseObject po, Company company){
        company.setCompanyCode(po.getObjectId());
        company.setManagerEmail(po.getString("Company_manager_email"));
        company.setCompanyName(po.getString("Company_name"));
        company.setCompanyAddress(po.getString("CompanyAddress"));
        company.setLocation(po.getDouble("Location_LAT"),po.getDouble("Location_LNG"));
        company.setManagerID(po.getString("Manager_ID"));
        company.setCreateDate(po.getCreatedAt());
        return company;
    }
}
