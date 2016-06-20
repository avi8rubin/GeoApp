package info.androidhive.materialtabs.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseGeoPoint;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import info.androidhive.materialtabs.GeoObjects.Shift;
import info.androidhive.materialtabs.R;
import info.androidhive.materialtabs.common.GeoAppDBHelper;
import info.androidhive.materialtabs.common.Server;
import info.androidhive.materialtabs.common.Status;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private String WorkName = "Avi work";
    private String ActiveEmail,ActivePassword;
    private String provider;
    private LocationManager locationManager;
    private Location location;
    private WifiManager mainWifiObj;
    private CountDownTimer MsgTimer;
    private boolean GPS_Enable=false;
    private boolean go_to_setting=false;
    private int Icon_index;
    private Double lat;
    private Double lng;
    private Button Enter_Button;
    private Button Exit_Button;
    private static Shift currentShift;
    private String[] Icon_Array_list = {"loading_0","loading_1","loading_2","loading_3","loading_4"};
    private CountDownTimer MsgTimer1;
    private SQLiteDatabase myDB;
    private boolean is_Enter_screen;
    public GeoAppDBHelper DB;
    public MapsActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String str = String.valueOf(getIntent().getExtras().get("EnterOrExit"));

        if(str.equals("Enter")) {
            setContentView(R.layout.activity_maps);
            Enter_Button = (Button)findViewById(R.id.enter_good);
            Enter_Button.setEnabled(false);
            is_Enter_screen=true;
        }
        else {
            setContentView(R.layout.activity_maps_exit);
            Exit_Button = (Button) findViewById(R.id.exit_good);
            Exit_Button.setEnabled(false);
            is_Enter_screen=false;
        }



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        GPS_Finder();
        Icon_index=0;

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        if(GPS_Enable)
        {
            Location location = locationManager.getLastKnownLocation(provider);
            if(location != null) {
                Double lat = location.getLatitude();
                Double lng = location.getLongitude();
                mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(WorkName));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lng)));
            }
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
            Dialog();



        }

        if(GPS_Enable == false)
        {
            final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setIcon(R.drawable.loading_0);
            builder.setTitle(R.string.gps_enable);
            builder.setInverseBackgroundForced(true);
            builder.setMessage(R.string.gps_service);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    go_to_setting=true;
                    startActivity(new Intent(action));

                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }

    }

    /**
     * This function open dialog yes\no option
     */
    public void Dialog(){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.find_geo_location_title)
                .setMessage(R.string.find_geo_location_msg) //R.string.find_geo_location_msg
                .setIcon(R.drawable.loading_0);

        dialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
                MsgTimer.cancel();
            }
        });
        final AlertDialog alert = dialog.create();
        //alert.show();

        MsgTimer= new CountDownTimer(65000, 500) {
            @Override
            public void onTick(long millisUntilFinished) {
                int resID = getResources().getIdentifier(get_next_icon() , "drawable", getPackageName());
                alert.setIcon(resID);
                alert.show();
            }

            @Override
            public void onFinish() {
                alert.dismiss();
            }
        }.start();
        // Hide after some seconds
        final Handler handler  = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (alert.isShowing()) {
                    alert.dismiss();
                }
            }
        };
    }

    /***
     * This function get GEO Location and handle screen map
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0, 0), 0));
        if(MsgTimer!=null) {
            MsgTimer.cancel();
            MsgTimer.onFinish();
        }
        if(is_Enter_screen)
            Enter_Button.setEnabled(true);
        else
            Exit_Button.setEnabled(true);

        lat = location.getLatitude();
        lng = location.getLongitude();
        //Log.i("Loc data", "lat:" + lat.toString() + lng.toString());
        //mMap.clear();
        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(WorkName));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 3));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15));

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /**
     * Regiser new GPS lisnter
     */
    @Override
    protected void onResume() {
        super.onResume();
        DB = new GeoAppDBHelper(getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        GPS_Finder();
        if(go_to_setting)
        {
            Dialog();
            go_to_setting=false;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
    }

    /**
     * delete GPS listner
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        locationManager.removeUpdates(this);
    }
    private void GPS_Finder() {

        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            provider = locationManager.getBestProvider(new Criteria(), false);
            location = locationManager.getLastKnownLocation(provider);
            GPS_Enable=true;
            // Toast.makeText(getApplicationContext(),"GPS OK!",Toast.LENGTH_SHORT).show();
        }
        else {
            //  Toast.makeText(getApplicationContext(), "GPS no OK!", Toast.LENGTH_SHORT).show();
            if (mainWifiObj.isWifiEnabled())
            {
                // Toast.makeText(getApplicationContext(), "WIFI OK!", Toast.LENGTH_SHORT).show();
                provider = LocationManager.NETWORK_PROVIDER;
                location = locationManager.getLastKnownLocation(provider);

            } else {
                // Toast.makeText(getApplicationContext(), "WIFI no OK!", Toast.LENGTH_SHORT).show();
                // GoToMainScreen();
            }
        }

    }
    public String get_next_icon(){
        Icon_index=(Icon_index+1)%Icon_Array_list.length;
        return Icon_Array_list[Icon_index];
    }

    /**
     * This function can 'ENTER' only when there is GPS location
     * save to DB with lat & lng
     * @param view
     */
    public void Onclick_Enter(View view){
        try {
        currentShift = new Shift();
        currentShift.setEnterTimeNow();
        currentShift.setUserEmail(getCurrentActiveEmailUser());
        currentShift.setUserID(getCurrentActiveIDUser());
        currentShift.setCompanyCode(getCurrentActiveCompanycodeUser());
        currentShift.setEnterLocation(new ParseGeoPoint(lat, lng));
        currentShift.setShiftStatus(Status.ENTER);
        currentShift = (Shift) Server.setShiftStatus(currentShift);
            DB.EnterNewShift(lat, lng, currentShift.getSystemID());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        startActivity(new Intent(this, IconTabsActivity.class));
    }

    /**
     * THis function 'Exit' with lat & lng
     * and save to DB
     * @param view
     */
    public void Onclicl_Enter_anyway(View view){
        try{
        currentShift = new Shift();
        currentShift.setEnterTimeNow();
        currentShift.setUserEmail(getCurrentActiveEmailUser());
        currentShift.setUserID(getCurrentActiveIDUser());
        currentShift.setCompanyCode(getCurrentActiveCompanycodeUser());
        currentShift.setEnterLocation(new ParseGeoPoint(lat, lng));
        currentShift.setShiftStatus(Status.ENTER);
        currentShift = (Shift) Server.setShiftStatus(currentShift);

        Server.setShiftStatus(currentShift);
        DB.EnterNewShift(currentShift.getSystemID());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        startActivity(new Intent(this, IconTabsActivity.class));
    }

    /**
     * This function can only 'Exit' when GPS location
     * save to DB
     * @param view
     */
    public void Onclick_Exit(View view){

        try {
            currentShift = new Shift();
            currentShift.setEnterTime(DB.getShiftEnterTime());
            currentShift.setExitTimeNow();
            currentShift.setUserEmail(getCurrentActiveEmailUser());
            currentShift.setUserID(getCurrentActiveIDUser());
            currentShift.setCompanyCode(getCurrentActiveCompanycodeUser());
            currentShift.setShiftStatus(Status.EXIT);
            String str = getShiftObjectIDFromParse();
            currentShift.setSystemID(str);
            Server.setShiftStatus(currentShift);
            DB.ExitShift(lat, lng);
            startActivity(new Intent(this,IconTabsActivity.class));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * This function exit without GPS location
     * @param view
     */
    public void Onclick_Exit_anyway(View view){

        try
        {
            currentShift = new Shift();
            currentShift.setEnterTime(DB.getShiftEnterTime());
            currentShift.setExitTimeNow();
            currentShift.setUserEmail(getCurrentActiveEmailUser());
            currentShift.setUserID(getCurrentActiveIDUser());
            currentShift.setCompanyCode(getCurrentActiveCompanycodeUser());
            currentShift.setShiftStatus(Status.EXIT);
            String str = getShiftObjectIDFromParse();
            currentShift.setSystemID(str);
            Server.setShiftStatus(currentShift);
            DB.ExitShift();
            startActivity(new Intent(this, IconTabsActivity.class));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * return DATATIME type
     * @return
     */
    public String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }


    public void get_new_current_user(){

        if (myDB == null)
            myDB = this.openOrCreateDatabase("GeoDB", MODE_PRIVATE, null);
        Cursor resultSet = myDB.rawQuery("SELECT Email,User_Password FROM Setting WHERE User_active=1;", null);
        int x= resultSet.getCount();
        if (x > 0) {
            resultSet.moveToNext();
            String s1 = resultSet.getString(0);
            String s2 = resultSet.getString(1);
            ActiveEmail=s1;
            ActivePassword=s2;
        }

    }

    public String getShiftObjectIDFromParse() {
        try {
            myDB = this.openOrCreateDatabase("GeoDB", MODE_PRIVATE, null);
            Cursor resultSet = myDB.rawQuery("SELECT SystemID FROM ShiftBuffer WHERE Shift_status=1;", null);
            int x = resultSet.getCount();
            if (x > 0) {
                resultSet.moveToNext();
                String s1 = resultSet.getString(0);
                return s1;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }
    public String getCurrentActiveEmailUser(){
        if (myDB == null)
            myDB = this.openOrCreateDatabase("GeoDB", MODE_PRIVATE, null);
        Cursor resultSet = myDB.rawQuery("SELECT Email,User_Password FROM Setting WHERE User_active=1;", null);
        int x= resultSet.getCount();
        if (x > 0) {
            resultSet.moveToNext();
            String s1 = resultSet.getString(0);
            return s1;
        }
        return "";
    }
    public String getCurrentActiveIDUser(){
        if (myDB == null)
            myDB = this.openOrCreateDatabase("GeoDB", MODE_PRIVATE, null);
        Cursor resultSet = myDB.rawQuery("SELECT User_TZ FROM Setting WHERE User_active=1;", null);
        int x= resultSet.getCount();
        if (x > 0) {
            resultSet.moveToNext();
            int id = resultSet.getInt(0);
            String s1 = resultSet.getString(0);
            return String.valueOf(id);
        }
        return "";
    }
    public String getCurrentActiveCompanycodeUser(){
        if (myDB == null)
            myDB = this.openOrCreateDatabase("GeoDB", MODE_PRIVATE, null);
        Cursor resultSet = myDB.rawQuery("SELECT User_Company_code FROM Setting WHERE User_active=1;", null);
        int x= resultSet.getCount();
        if (x > 0) {
            resultSet.moveToNext();
            String s1 = resultSet.getString(0);
            return s1;
        }
        return "";
    }

}
