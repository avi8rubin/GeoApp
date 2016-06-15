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

import info.androidhive.materialtabs.R;

import info.androidhive.materialtabs.common.GeoAppDBHelper;
import info.androidhive.materialtabs.common.ReadFile;
import info.androidhive.materialtabs.common.Server;

public class MainActivity extends Activity {
    public GeoAppDBHelper  DB;
    public MainActivity() {
        DB = null;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Parse.initialize(this, "WNbaXzzXNTyvDefzEFKoaIxXcdfDUtuac2g8ZgDC", "Sa4HLlt21b4wfycwoPgnR9aKOXfAQonhloO1zWUS");
        DB = new GeoAppDBHelper(getApplicationContext());
        this.deleteDatabase(DB.getDatabaseName());
        if(DB.isUserWasAtive()) {
            // startActivity(new Intent(this,Manager_screen.class));
           startActivity(new Intent(this, Login_Activity.class));
            //startActivity(new Intent(this,IconTabsActivity.class));
        }
        else
        {
            //startActivity(new Intent(this,LoginActivity.class));
        }
    }

}
