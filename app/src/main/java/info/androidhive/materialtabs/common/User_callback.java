package info.androidhive.materialtabs.common;

import java.io.Serializable;

public class User_callback implements Serializable{
    private String user_ID;
    private String Email;
    private String user_password;
    private String first_name;
    private String last_name;
    private String user_tz;
    private String user_phone;
    private String user_role;

    public  User_callback(){Email="";user_ID="";user_password="";first_name="";last_name="";user_tz="";user_phone="";}
    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }


    public String getUser_ID() {
        return user_ID;
    }

    public void setUser_ID(String user_ID) {
        this.user_ID = user_ID;
    }
    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }
    public String getUser_tz() {
        return user_tz;
    }

    public void setUser_tz(String user_tz) {
        this.user_tz = user_tz;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
    public String getUser_role() {
        return user_role;
    }
    public void setUser_role(String user_role) {
        this.user_role = user_role;
    }
}
