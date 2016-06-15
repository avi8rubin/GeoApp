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

import info.androidhive.materialtabs.common.ReadFile;
import info.androidhive.materialtabs.common.Server;

public class MainActivity extends Activity {

    private static boolean firstRun = true;
    private SQLiteDatabase myDB;
    private BroadcastReceiver mReceiver;
    boolean isBound = false;//
    BroadcastReceiver receiver;
    private MainActivity thisActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Parse.initialize(this, "WNbaXzzXNTyvDefzEFKoaIxXcdfDUtuac2g8ZgDC", "Sa4HLlt21b4wfycwoPgnR9aKOXfAQonhloO1zWUS");
        //Avi rubin Update

        //------DB Start------///
        if (firstRun) {
            start_DB();
            firstRun = false;
        }
        init();


    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
       // init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ///init();
    }
    public void start_DB() {

        try {
            myDB = this.openOrCreateDatabase("GeoDB", MODE_PRIVATE, null);
            String sql_script = ReadFile.getStringFromFile(getResources().openRawResource(R.raw.sqlite_db_geoapp));
            String[] query_array = sql_script.split(";");
            for (int i = 0; i < query_array.length; i++) {
                // One SQL per Time
                myDB.execSQL(query_array[i]);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() {
        //TODO Check if there is allready user loged? Yes-Check shift status  else-Login screen;

        //User Was Active - Send To App Screen & Get Shift info
        if(User_Was_Active())
        {
            myDB.close();
           // startActivity(new Intent(this,Manager_screen.class));
            startActivity(new Intent(this,Login_Activity.class));
            //startActivity(new Intent(this,IconTabsActivity.class));

        }
        else
        {
           //startActivity(new Intent(this,LoginActivity.class));
        }
    }

    public boolean User_Was_Active()
    {
        if(myDB==null)
            myDB = this.openOrCreateDatabase("GeoDB", MODE_PRIVATE, null);
        Cursor resultSet= myDB.rawQuery("SELECT * FROM Setting WHERE User_active = 1;", null);
        return resultSet.getCount()>0;
    }


}
