package info.androidhive.materialtabs.common;

import android.util.Log;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadFactory;

import info.androidhive.materialtabs.GeoObjects.Company;
import info.androidhive.materialtabs.GeoObjects.ParseObjects;
import info.androidhive.materialtabs.GeoObjects.Shift;
import info.androidhive.materialtabs.GeoObjects.User;

public class Server {
    private static String Users = "Users";
    private static String Company = "Company";
    private static String Shifts = "Users_shifts";
    private static String TAG = "Parse status: ";
    private static Object lockObj = new Object();

    public static Object CreateNewUser(User user) {
        List<ParseObject> result;
        ParseQuery<ParseObject> query;
        //check if user already exists
        String email = user.getEmail();
        query = ParseQuery.getQuery(Users);
        query.whereEqualTo("email", email);
        try {
            result = query.find();
            if (!result.isEmpty()) return "User already exists";

        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Create new user, save it in parse and get the id
        String userID = saveAndGetObjectID(user);
        user.setSystemID(userID);
        return user;

    }

    public static Object getUserDetails(User user) {
        List<ParseObject> result;
        ParseQuery<ParseObject> query;
        String password = user.getPassword();
        String email = user.getEmail();
        query = ParseQuery.getQuery(Users);
        query.whereEqualTo("email", email.trim());
        try {
            result = query.find();
            if (!result.isEmpty()) {
                ParseObjects.setParseObject(result.get(0),user);
                //user.setParseObject(result.get(0));
                if (user.getPassword().equals(password)) return user;
                else return "Wrong Password";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "User Not Exists";
    }

    public static Object addNewCompany(User user, Company company) {
        List<ParseObject> result;
        ParseQuery<ParseObject> query;
        //Check if user is manager
        if (user.isWorker()) return "User role is not a manager in the system.";
        //Check if company already exists
        query = ParseQuery.getQuery(Company);
        query.whereEqualTo("Company_manager_email", user.getEmail());
        query.whereEqualTo("Company_name", company.getCompanyName());
        try {
            result = query.find();
            if (!result.isEmpty()) return "Company already exists";

        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Create new company
        company.setManagerID(user.getSystemID());
        company.setManagerEmail(user.getEmail());
        company.setCompanyCode(saveAndGetObjectID(company));

        /*
        try {
            company.ParseObjects().save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Get company ID and return company object
        query = ParseQuery.getQuery(Company);
        query.whereEqualTo("Company_manager_email", company.getManagerEmail());
        query.whereEqualTo("Company_name", company.getCompanyName());
        try {
            result = query.find();
            company.setCompanyCode(result.get(0).getString("objectId"));

        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        //Update manager connection to company
        updateCompanyToUser(user, company);
        return company;

    }

    public static Object setShiftStatus(Shift shift) {
        List<ParseObject> result;
        ParseQuery query = new ParseQuery(Shifts);
        Status status = shift.getShiftStatus();
        if (status == Status.ENTER) {
            CloseAllOpenShifts();
            //Create new shift record
            shift.setSystemID(saveAndGetObjectID(shift));
        } else if (status == Status.EXIT) {
            if (shift.getSystemID().equals("")) return false;
            shift.setShiftStatus(Status.CLOSE);
            //Update exit time in current shift
            try {
                ParseObject o = query.get(shift.getSystemID());
                o.put("exit_time", shift.getExitTime());
                o.save();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (status == Status.UPDATE) {
            //For future upgrades
        } else { //(status == Status.CLOSE)

        }
        return shift;
    }

    private static void CloseAllOpenShifts() {
        List<ParseObject> result = null;
        ParseQuery query = new ParseQuery(Shifts);
        query.whereEqualTo("ShiftStatus", 1);
        try {
            //Get all shifts are in status 'ENTER'
            result = query.find();
            //Close all those shifts, status 'CLOSE'
            for (int i = 0; result != null && i < result.size(); i++) {
                Shift s = new Shift(result.get(i));
                ParseObject o = query.get(s.getSystemID());
                o.put("ShiftStatus", 3);
                o.saveInBackground();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<User> getCompanyWorkers(Company company) {
        List<ParseObject> result = null;
        ArrayList<User> users = new ArrayList<User>();
        ParseQuery query = new ParseQuery(Users);
        query.whereEqualTo("CompanyCode", company.getCompanyCode());
        query.whereEqualTo("user_role", "Worker");
        try {
            //Get all users that work in relevant company
            result = query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Close all those shifts, status 'CLOSE'
        for (int i = 0; result != null && i < result.size(); i++) {
            User user = new User();
            users.add(ParseObjects.setParseObject(result.get(i),user));
            //users.add(new User(result.get(i)));
        }
        return users;
    }

    public static ArrayList<User> getCompanyWorkers(User user) {
        List<ParseObject> result = null;
        ArrayList<User> users = new ArrayList<User>();
        //If the current user is not a manager
        if (user.getRole() == Status.WORKER)
            return users;
        ParseQuery query = new ParseQuery(Users);
        query.whereEqualTo("CompanyCode", user.getCompanyCode());
        query.addAscendingOrder("first_name");
        //query.whereEqualTo("user_role","Worker");
        try {
            //Get all users that work in relevant company
            result = query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Close all those shifts, status 'CLOSE'
        for (int i = 0; result != null && i < result.size(); i++) {
            if (result.get(i).getString("user_role").equals("Manager")) continue;
            User userBack = new User();
            users.add(ParseObjects.setParseObject(result.get(i),userBack));
            //users.add(new User(result.get(i)));
        }
        return users;
    }

    public static User updateCompanyToUser(User user, Company company) {
        ParseQuery query = new ParseQuery(Users);
        try {
            ParseObject o = query.get(user.getSystemID());
            o.put("CompanyName", company.getCompanyName());
            o.put("CompanyCode", company.getCompanyCode());
            o.saveInBackground();
        } catch (ParseException e) {
            e.printStackTrace();
            return user;
        }
        user.setCompanyName(company.getCompanyName());
        user.setCompanyCode(company.getCompanyCode());
        return user;
    }

    public static User getUserShifts(User user) {
        ParseQuery query = new ParseQuery(Shifts);
        List<ParseObject> result = null;
        query.whereEqualTo("User_ID", user.getSystemID());
        query.addAscendingOrder("enter_time");
        try {
            result = query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Get all user history shifts
        for (int i = 0; result != null && i < result.size(); i++)
            user.addShift(new Shift(result.get(i)));
        return user;
    }

    public static ArrayList<User> getUserShifts(ArrayList<User> user) {
        ArrayList<User> UsersShifts = new ArrayList<User>();
        for (int i = 0; i < user.size(); i++) {
            UsersShifts.add(getUserShifts(user.get(i)));
        }
        return UsersShifts;
    }

    public static ArrayList<User> getUserShifts(List<User> user) {
        ArrayList<User> UsersShifts = new ArrayList<User>();
        for (int i = 0; i < user.size(); i++) {
            UsersShifts.add(getUserShifts(user.get(i)));
        }
        return UsersShifts;
    }

    private static String saveAndGetObjectID(User user) {
        //final ParseObject po = user.getParseObject();
        final ParseObject po = ParseObjects.getParseObject(user);
        //wait until objectID return
        synchronized (lockObj) {
            po.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        // Saved successfully.
                        Log.d(TAG, "User update saved!");
                        String id = po.getObjectId();
                        Log.d(TAG, "The object id is: " + id);
                    } else {
                        // The save failed.
                        Log.d(TAG, "User update error: " + e);
                    }
                }
            });
        }
        //set objectID to user object
        synchronized (lockObj) {
            while (po.getObjectId() == null) sleep((long) 100);
            if (po.getObjectId() != null)
                return po.getObjectId();
            else return null;

        }
    }

    private static String saveAndGetObjectID(Company company) {
        //final ParseObject po = company.getParseObject();
        final ParseObject po = ParseObjects.getParseObject(company);
        //wait until objectID return
        synchronized (lockObj) {
            po.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        // Saved successfully.
                        Log.d(TAG, "Company update saved!");
                        String id = po.getObjectId();
                        Log.d(TAG, "The object id is: " + id);
                    } else {
                        // The save failed.
                        Log.d(TAG, "Company update error: " + e);
                    }
                }
            });
        }
        //set objectID to user object
        synchronized (lockObj) {
            while (po.getObjectId() == null) sleep((long) 100);
            if (po.getObjectId() != null)
                return po.getObjectId();
            else return null;
        }
    }

    private static String saveAndGetObjectID(Shift shift) {
        final ParseObject po = shift.getParseObject();
        //wait until objectID return
        synchronized (lockObj) {
            po.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        // Saved successfully.
                        Log.d(TAG, "Shift update saved!");
                        String id = po.getObjectId();
                        Log.d(TAG, "The object id is: " + id);
                    } else {
                        // The save failed.
                        Log.d(TAG, "Shift update error: " + e);
                    }
                }
            });
        }
        //set objectID to user object
        synchronized (lockObj) {
            while (po.getObjectId() == null) sleep((long) 100);
            if (po.getObjectId() != null)
                return po.getObjectId();
            else return null;
        }
    }

    public static Object getLockObj() {
        return lockObj;
    }

    private static void sleep(Long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isComanyCodeValid(String company) {
        List<ParseObject> result;
        ParseQuery<ParseObject> query;
        query = ParseQuery.getQuery(Users);
        query.whereEqualTo("ComapanyCode", company.toString());
        try {
            result = query.find();
            if (!result.isEmpty()) {
                return true;
            } else return false;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}
