package info.androidhive.materialtabs.common;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by zinoo on 11/06/2016.
 */
public class Globals {
    public static final String EXTRA_USER = "ExtraUserIntent";
    public static final String EXTRA_COMPANY = "ExtraCompanyIntent";
    public static Context GeoAppContext;

    public static Boolean isEmptyOrNull(String str){
        if((str == null) || str.trim().equals("")) return true;
        return false;
    }
    public static Date getStringToDateTime(String str){
        if(isEmptyOrNull(str)) return null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(str.trim());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return date;
    }
    public static String getDateTimeToString(Date date) {
        if(date == null) return null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }
    public static String stringArrayToString(String[] str, String separator){
        String returnStr = "";
        if(str.length == 1) return str[0];
        for (String s : str) {
            returnStr = returnStr+str+separator;
        }
        return returnStr.substring(0,returnStr.length()-1);
    }
    public static String Now(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(c.getTime());
    }
}
