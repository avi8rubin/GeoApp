package info.androidhive.materialtabs.common;

import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.materialtabs.GeoObjects.Company;
import info.androidhive.materialtabs.GeoObjects.Shift;
import info.androidhive.materialtabs.GeoObjects.User;

public class Server {
    private static String USERS = "Users";
    private static String COMPANY = "Company";
    private static String SHIFTS = "Users_shifts";
    private static String TAG = "Parse status: ";
    private static Object lockObj = new Object();
    private static GeoAppDBHelper DB = new GeoAppDBHelper(Globals.GeoAppContext);

    public static Object CreateNewUser(User user) {
        List<ParseObject> result;
        ParseQuery<ParseObject> query;
        //check if user already exists
        String email = user.getEmail();
        query = ParseQuery.getQuery(USERS);
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
        //Check if was save in the local DB
        Object LocalUser;
        if((LocalUser = DB.select(user)) instanceof User)
            return (User) LocalUser;
        //Check if exists in the server
        query = ParseQuery.getQuery(USERS);
        query.whereEqualTo("email", email.trim());
        try {
            result = query.find();
            if (!result.isEmpty()) {
                user.setParseObject(result.get(0));
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
        query = ParseQuery.getQuery(COMPANY);
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
        //Update manager connection to company
        updateCompanyToUser(user, company);
        return company;

    }
    public static Object setShiftStatus(Shift shift) {
        List<ParseObject> result;
        ParseQuery query = new ParseQuery(SHIFTS);
        Status status = shift.getShiftStatus();
        if (status == Status.ENTER) {
            DB.CloseAllOpenShifts(); //Close in the local DB
            CloseAllOpenShifts(); //Close on server DB
            if(!DB.allShiftsExists()) {
                User u = new User();
                u.setSystemID(shift.getUserID());
                new AsyncTaskDownloadAllUserShifts().execute(u);
            }
            //Create new shift record
            shift.setSystemID(saveAndGetObjectID(shift));
            DB.insert(shift);
        } else if (status == Status.EXIT) {
            if (shift.getSystemID().equals("")) return false;
            shift.setShiftStatus(Status.CLOSE);
            DB.update(shift);
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
    public static Object getOpenShift(){
        List<ParseObject> result = null;
        Object localShift;
        if((localShift = DB.getOpenShift()) instanceof Shift)
            return (Shift) localShift;
        ParseQuery query = new ParseQuery(SHIFTS);
        query.whereEqualTo("ShiftStatus", 1);
        try {
            //Get 'ENTER' shift
            result = query.find();
            if(result.isEmpty()) return false;
            }
        catch(ParseException e) {
            e.printStackTrace();
        }
        return new Shift(result.get(0));
    }
    private static void CloseAllOpenShifts() {
        List<ParseObject> result = null;
        ParseQuery query = new ParseQuery(SHIFTS);
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
        ParseQuery query = new ParseQuery(USERS);
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
            users.add(new User(result.get(i)));
        }
        return users;
    }
    public static ArrayList<User> getCompanyWorkers(User user) {
        List<ParseObject> result = null;
        ArrayList<User> users = new ArrayList<User>();
        //If the current user is not a manager
        if (user.getRole() == Status.WORKER)
            return users;
        ParseQuery query = new ParseQuery(USERS);
        query.whereEqualTo("CompanyCode", user.getCompanyCode());
        query.addAscendingOrder("first_name");
        try {
            //Get all users that work in relevant company
            result = query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Close all those shifts, status 'CLOSE'
        for (int i = 0; result != null && i < result.size(); i++) {
            if (result.get(i).getString("user_role").equals("Manager")) continue;
            users.add(new User(result.get(i)));
        }
        return users;
    }
    public static User updateCompanyToUser(User user, Company company) {
        ParseQuery query = new ParseQuery(USERS);
        try {
            ParseObject o = query.get(user.getSystemID());
            o.put("CompanyName", company.getCompanyName());
            o.put("CompanyCode", company.getCompanyCode());
            o.saveInBackground();
        } catch (ParseException e) {
            e.printStackTrace();
            return user;
        }
        DB.updateCompanyToUser(company,user);
        user.setCompanyName(company.getCompanyName());
        user.setCompanyCode(company.getCompanyCode());
        return user;
    }
    public static User getUserShifts(User user) {
        ParseQuery query = new ParseQuery(SHIFTS);
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
        final ParseObject po = user.getParseObject();
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
        final ParseObject po = company.getParseObject();
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
    public boolean isCompanyCodeValid(String company) {
        List<ParseObject> result;
        ParseQuery<ParseObject> query;
        query = ParseQuery.getQuery(USERS);
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
    public static Company getManagerCompany(User user){
        if(Globals.isEmptyOrNull(user.getCompanyCode())) return null;
        //Check if company exists in the local DB
        Object companyLocal;
        if((companyLocal = DB.select(new Company(user.getCompanyCode(),user.getCompanyName()))) instanceof Company)
            return (Company) companyLocal;
        //Get company from server
        List<ParseObject> result = null;
        ParseQuery query = new ParseQuery(COMPANY);
        query.whereEqualTo("objectId", user.getCompanyCode());
        try {
            result = query.find();
            if (result.isEmpty()) return null;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        Company company = new Company(result.get(0));
        DB.insert(company); //The company not exists in the local DB
        return company;
    }

    private static class AsyncTaskDownloadAllUserShifts extends AsyncTask<User, Void, Void> {
        @Override
        protected Void doInBackground(User... params) {
            User user;
            user = getUserShifts(params[0]);
            ArrayList<Shift> userShifts = user.getUserShifts();
            for (Shift s : userShifts) {
                DB.insert(s);
            }
            return null;
        }
    }
}
