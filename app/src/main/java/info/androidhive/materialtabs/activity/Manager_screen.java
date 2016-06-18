package info.androidhive.materialtabs.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import info.androidhive.materialtabs.GeoObjects.Company;
import info.androidhive.materialtabs.GeoObjects.Shift;
import info.androidhive.materialtabs.GeoObjects.User;
import info.androidhive.materialtabs.R;
import info.androidhive.materialtabs.common.Globals;
import info.androidhive.materialtabs.common.Server;

public class Manager_screen extends AppCompatActivity implements OnItemSelectedListener {

    private int Month_number, Year_number;
    private TextView month, Year;
    private Spinner spinner;
    private User Manager;
    private Company company;
    private Button Logout_btn, Setting_btn;
    private ArrayList<User> Workers;
    private ArrayList<User> WorkersShifts;
    private ArrayList<Shift> CurrentWorkerShifts = null;
    private TableLayout managerTable;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_screen);
        Manager = (User) getIntent().getSerializableExtra(Globals.EXTRA_USER);
        try {
            Workers = new AsyncTaskGetCompanyWorkers().execute(Manager).get();
            new AsyncTaskGetWorkersShifts().execute(Workers);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        init(savedInstanceState);
    }
    @Override
    public void onStart() {
        super.onStart();

    }
    @Override
    public void onStop() {
        super.onStop();
    }
    @Override
    public void onSaveInstanceState(Bundle saveInstanceState){
        saveInstanceState.putInt("Month_number",Month_number);
        saveInstanceState.putInt("Year_number",Year_number);
        saveInstanceState.putSerializable("Manager",Manager);
        saveInstanceState.putSerializable("company",company);
        saveInstanceState.putSerializable("Workers",Workers);
        saveInstanceState.putSerializable("WorkersShifts",WorkersShifts);
        saveInstanceState.putSerializable("CurrentWorkerShifts",CurrentWorkerShifts);
    }
    private void init(Bundle savedInstanceState) {
        Calendar cal = Calendar.getInstance();
        Month_number = cal.get(Calendar.MONTH);
        Year_number = cal.get(Calendar.YEAR);
        month = (TextView) findViewById(R.id.month_manager);
        month.setText((getMonth(Month_number)).toString());
        Year = (TextView) findViewById(R.id.year_manager);
        Year.setText(String.valueOf(Year_number));
        spinner = (Spinner) findViewById(R.id.spinner);
        setSpinnerWorkersNames();
        spinner.setOnItemSelectedListener(this);
        Logout_btn = (Button) findViewById(R.id.manager_logout);
        Logout_btn.setBackgroundResource(R.drawable.button_custom_setting);
        Setting_btn = (Button) findViewById(R.id.manager_setting_btn);
        Setting_btn.setBackgroundResource(R.drawable.button_custom_setting);
        ((TextView) findViewById(R.id.company_code_txt)).setText(Manager.getCompanyCode());
        managerTable = (TableLayout) findViewById(R.id.manager_table);

        Bundle extras = getIntent().getExtras();
        if(extras.containsKey(Globals.EXTRA_COMPANY)) {
            company = (Company) extras.getSerializable(Globals.EXTRA_COMPANY);
        }
        else{
            company = new Company(Manager.getCompanyCode(),Manager.getCompanyName());
        }


        /*
        managerTable.setColumnStretchable(0,true);
        managerTable.setColumnStretchable(1,true);
        managerTable.setColumnStretchable(2,true);
        managerTable.setColumnStretchable(3,true);
        managerTable.setColumnStretchable(4,true);
        managerTable.setColumnStretchable(5,true);
        */
        if(savedInstanceState != null){
            Month_number = savedInstanceState.getInt("Month_number");
            Year_number = savedInstanceState.getInt("Year_number");
            Manager = (User) savedInstanceState.getSerializable("Manager");
            company = (Company) savedInstanceState.getSerializable("company");
            Workers = (ArrayList<User>) savedInstanceState.getSerializable("Workers");
            WorkersShifts = (ArrayList<User>) savedInstanceState.getSerializable("WorkersShifts");
            CurrentWorkerShifts = (ArrayList<Shift>) savedInstanceState.getSerializable("CurrentWorkerShifts");
            SetTable();
        }
    }

    public void Prev_Month(View view) {
        if (Month_number == 0) {
            Year_number--;
            Month_number = 12;
        }
        Month_number--;
        Month_number = Month_number % 12;
        SetTable();
    }
    public void Next_Month(View view) {
        if (Month_number == 11)
            Year_number++;
        Month_number++;
        Month_number = Month_number % 12;
        SetTable();
    }
    public void SetTable() {
        String month_name = getMonth(Month_number);
        month = (TextView) findViewById(R.id.month_manager);
        Year = (TextView) findViewById(R.id.year_manager);
        month.setText(month_name);
        Year.setText(String.valueOf(Year_number));
        // not remove headers
        managerTable.removeViews(1, managerTable.getChildCount() - 1);
        if(CurrentWorkerShifts != null) {
            for (Shift s : CurrentWorkerShifts) {
                //if((getMonth(s.getEnterTime()) == Month_number) && (getYear(s.getEnterTime()) == Year_number)) //Set data for current date
                setShiftToTable(s);
            }
        }

    }
    public String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month];
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            return false; //I have tried here true also
        }
        return super.onKeyDown(keyCode, event);
    }
    public void onClickLogoutManager(View view) {
        startActivity(new Intent(this, Login_Activity.class));
    }
    private int getMonth(Date d){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM");
        return Integer.parseInt(dateFormat.format(d));
    }
    private int getYear(Date d){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        return Integer.parseInt(dateFormat.format(d));
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        managerTable.removeViews(1, managerTable.getChildCount() - 1);
        if(id == 0) {
            CurrentWorkerShifts = null;
            return;
        }
        User worker = WorkersShifts.get((int)id-1);
        ArrayList<Shift> UserShifts = worker.getUserShifts();
        CurrentWorkerShifts = UserShifts;
        for(Shift s : UserShifts){
            //if((getMonth(s.getEnterTime()) == Month_number) && (getYear(s.getEnterTime()) == Year_number)) //Set data for current date
                setShiftToTable(s);
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    private class AsyncTaskGetWorkersShifts extends AsyncTask<List<User>,Void,Void>{
        @Override
        protected Void doInBackground(List<User>... params) {
            List<User> workers = params[0];
                WorkersShifts = Server.getUserShifts(workers);
            return null;
        }
    }
    private class AsyncTaskGetCompanyWorkers extends AsyncTask<User, Void, ArrayList<User>> {
        @Override
        protected ArrayList<User> doInBackground(User... params) {
            Workers = Server.getCompanyWorkers(params[0]);
            return Workers;
        }
    }
    private List<String> getWorkersNames(){
        List<String> Names = new ArrayList<String>();
        Names.add(getString(R.string.select_worker));
        for(User u : Workers){
            Names.add(u.getFirstName()+" "+u.getLastName());
        }
        return Names;
    }
    private void setSpinnerWorkersNames(){
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getWorkersNames());
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }
    public void setShiftToTable(Shift shift){
        TableRow shiftRow = new TableRow(this);
        shiftRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.MATCH_PARENT,1.0f));
        //Create all TextView component

        TextView ShiftDate = new TextView(this);
        TextView enterTime = new TextView(this);
        TextView exitTime = new TextView(this);
        TextView enterLocation = new TextView(this);
        TextView exitLocation = new TextView(this);
        TextView duration = new TextView(this);
/*
        ShiftNum.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT,1.0f));
        enterTime.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT,1.0f));
        exitTime.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT,1.0f));
        enterLocation.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT,1.0f));
        exitLocation.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT,1.0f));
        duration.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT,1.0f));


        //ShiftNum.setWidth(TableLayout.LayoutParams.MATCH_PARENT);
        enterTime.setWidth(TableLayout.LayoutParams.MATCH_PARENT);
        exitTime.setWidth(TableLayout.LayoutParams.MATCH_PARENT);
        enterLocation.setWidth(TableLayout.LayoutParams.MATCH_PARENT);
        exitLocation.setWidth(TableLayout.LayoutParams.MATCH_PARENT);
        duration.setWidth(TableLayout.LayoutParams.MATCH_PARENT);



        ShiftNum.setGravity(Gravity.CENTER);
        enterTime.setGravity(Gravity.CENTER);
        exitTime.setGravity(Gravity.CENTER);
        enterLocation.setGravity(Gravity.CENTER);
        exitLocation.setGravity(Gravity.CENTER);
        duration.setGravity(Gravity.CENTER);
*/


        String enterL, exitL;

        //Set all component
        ShiftDate.setText(dateFormat.format(shift.getEnterTime()));
        enterTime.setText(timeFormat.format(shift.getEnterTime()));
        exitTime.setText(timeFormat.format(shift.getExitTime()));
        if(shift.getEnterLocation() == null) enterL = "xx/xx";
        else enterL = String.valueOf(((int)shift.getEnterLocation().getLatitude()))+"/"
                +String.valueOf(((int)shift.getEnterLocation().getLongitude()));
        if(shift.getExitLocation() == null) exitL = "xx/xx";
        else exitL = String.valueOf(((int)shift.getExitLocation().getLatitude()))+"/"
                +String.valueOf(((int)shift.getExitLocation().getLongitude()));
        enterLocation.setText(enterL);
        exitLocation.setText(exitL);
        duration.setText(shift.getDuration());
        //Attach components to row object
        shiftRow.addView(ShiftDate);
        shiftRow.addView(enterTime);
        shiftRow.addView(exitTime);
        shiftRow.addView(enterLocation);
        shiftRow.addView(exitLocation);
        shiftRow.addView(duration);
        managerTable.addView(shiftRow);
    }

    public void onClickSettings(View view){
        Intent intent = new Intent(this,ManagerSettings.class);
        startActivity(intent);
    }
}
