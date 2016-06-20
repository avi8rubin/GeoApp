package info.androidhive.materialtabs.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.content.ComponentName;
import android.content.Context;

import android.content.ServiceConnection;
import android.os.AsyncTask;

import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import info.androidhive.materialtabs.GeoObjects.Company;
import info.androidhive.materialtabs.GeoObjects.User;
import info.androidhive.materialtabs.R;

import info.androidhive.materialtabs.common.GeoAppDBHelper;
import info.androidhive.materialtabs.common.Globals;
import info.androidhive.materialtabs.common.ReadFile;
import info.androidhive.materialtabs.common.Server;

public class MainActivity extends Activity {
    private GeoAppDBHelper  DB;
    public MainActivity() {
        DB = null;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Parse.initialize(this, "WNbaXzzXNTyvDefzEFKoaIxXcdfDUtuac2g8ZgDC", "Sa4HLlt21b4wfycwoPgnR9aKOXfAQonhloO1zWUS");
        DB = new GeoAppDBHelper(getApplicationContext());
        Globals.GeoAppContext = getApplicationContext();
        Object returnVal = DB.getLastLoginUser();
        if(returnVal instanceof User){  //Get user object back
            Intent intent;
            User user = (User) returnVal;
            if(user.getAutoLogin() && user.isManager()) { //User is manager and have auto login settings and
                intent = new Intent(this, Manager_screen.class);
                intent.putExtra(Globals.EXTRA_USER,user);
                Object ManagerCompany;
                if((ManagerCompany = DB.getCompanyByCompanyID(user.getCompanyCode())) instanceof Company)
                    intent.putExtra(Globals.EXTRA_COMPANY,(Company)ManagerCompany);
                startActivity(intent);
            }
            else if (user.getAutoLogin() && user.isWorker()) { //User is worker and have auto login settings and
                intent = new Intent(this, IconTabsActivity.class);
                intent.putExtra(Globals.EXTRA_USER,user);
                startActivity(intent);
            }
            else {//User not check auto login in settings screen
                intent = new Intent(this, Login_Activity.class);
                intent.putExtra(Globals.EXTRA_USER,user);
                startActivity(intent);
            }
        }
        else startActivity(new Intent(this,Login_Activity.class)); // User was not login in the past

    }

}
