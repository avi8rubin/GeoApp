package info.androidhive.materialtabs.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import info.androidhive.materialtabs.GeoObjects.Shift;
import info.androidhive.materialtabs.GeoObjects.User;
import info.androidhive.materialtabs.R;
import info.androidhive.materialtabs.common.GeoAppDBHelper;
import info.androidhive.materialtabs.common.Globals;
import info.androidhive.materialtabs.common.Server;
import info.androidhive.materialtabs.fragments.FourFragment;
import info.androidhive.materialtabs.fragments.OneFragment;
import info.androidhive.materialtabs.fragments.ThreeFragment;
import info.androidhive.materialtabs.fragments.TwoFragment;

public class IconTabsActivity extends AppCompatActivity {


    /*----Setting screen valuse*/
    private Button Company_code_btn;
    private EditText Company_code_input;
    private String Company_Current_code;
    /*-------------------------*/


    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private boolean first_time_flag;
    private boolean shift_status;
    public GeoAppDBHelper DB;
    private int Month_number;
    private int Year_number;
    private User Current_User ;
    private  int[] tabIcons = {R.drawable.loction,R.drawable.history,R.drawable.setting1};
    private  int[] tabHIcons = {R.drawable.h_loction,R.drawable.h_history,R.drawable.h_setting1};
    private SQLiteDatabase myDB;
    private String Shift_start_Time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icon_tabs);

        Current_User =new User();
        Current_User =(User)getIntent().getSerializableExtra(Globals.EXTRA_USER);
        DB = new GeoAppDBHelper(getApplicationContext());

        Calendar cal = Calendar.getInstance();
        Month_number = cal.get(Calendar.MONTH);
        Year_number = cal.get(Calendar.YEAR);


        Object returnobj=Server.getOpenShift();
        if(returnobj instanceof Shift)
        {
            shift_status=true;
            Shift_start_Time= ((Shift) returnobj).getEnterTime().toString();
        }
        else
        {
            shift_status=false;
            Shift_start_Time="";
        }
        first_time_flag=true;
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();


    }
    private void setupTabIcons() {

        if(first_time_flag){
            first_time_flag=false;
            tabLayout.getTabAt(0).setIcon(tabHIcons[0]);
        }
        else{tabLayout.getTabAt(0).setIcon(tabIcons[0]);}
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);

    }
    private  void setupTabHihgIcons(int pos){
        tabLayout.getTabAt(pos).setIcon(tabHIcons[pos]);
    }
    private void setupViewPager(final ViewPager viewPager) {
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        if(shift_status){adapter.addFrag(new FourFragment(),"ONE");}
        else{adapter.addFrag(new OneFragment(),"ONE");}
        adapter.addFrag(new TwoFragment(), "TWO");
        adapter.addFrag(new ThreeFragment(), "THREE");
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }


            @Override
            public void onPageSelected(int position) {
                setupTabIcons();
                setupTabHihgIcons(position);
                SetTable();
                if (position == 0) {
                    Button b = (Button) findViewById(R.id.enter_to_shift_button);
                    if (b != null) {
                        b.setBackgroundResource(R.drawable.button_custom_setting);
                    }

                    Button b2 = (Button) findViewById(R.id.exit_to_shift_button);
                    if (b2 != null)
                        b2.setBackgroundResource(R.drawable.button_custom_setting);
                }


                if (position == 2) {
                    Company_code_btn = (Button) findViewById(R.id.company_code_btn);
                    Company_code_input = (EditText) findViewById(R.id.company_code_input);
                    Company_Current_code = Current_User.getCompanyCode();
                    if (!Company_Current_code.equals("")) {  //Null Pointer
                        Company_code_input.setText(Company_Current_code);
                        Company_code_btn.setText(R.string.edit_comapny_code);
                    } else {
                        Company_code_input.setText("");
                        Company_code_btn.setText(R.string.enter_comapny_code);
                    }
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // return null to display only the icon
            return null;
        }


    }
    public void onclick_enter(View view){
        Intent i = new Intent(this, MapsActivity.class);
        i.putExtra("EnterOrExit", "Enter");
        i.putExtra(Globals.EXTRA_USER, Current_User);
        startActivity(i);

    }
    public void onclick_exit(View view){
        Intent i = new Intent(this, MapsActivity.class);
        i.putExtra("EnterOrExit", "Exit");
        i.putExtra(Globals.EXTRA_USER, Current_User);
        startActivity(i);

    }
    public void SetTable(){
        String month_name = getMonth (Month_number);
        String eday = "";
        TextView month = (TextView)findViewById(R.id.month_manager);
        TextView Year = (TextView)findViewById(R.id.year_manager);
        month.setText(month_name);
        Year.setText(String.valueOf(Year_number));
        TableLayout table = (TableLayout) findViewById(R.id.shift_table);
        // not remove headers
        table.removeViews(1, table.getChildCount() - 1);

        try {
            Cursor resultSet =DB.getShift();
            int number_of_row=resultSet.getCount();
            if (number_of_row> 0) {
                resultSet.moveToNext();
                for(int i=0;i<number_of_row;i++)
                {

                    int id = resultSet.getInt(0);
                    int status = resultSet.getInt(1);
                    String start_time = resultSet.getString(2);
                    String end_time = resultSet.getString(3);
                    String lng_enter = resultSet.getString(4);
                    String lat_enter = resultSet.getString(5);
                    String lng_exit = resultSet.getString(6);
                    String lat_exit = resultSet.getString(7);
                    String sMonth="";
                    String sYear="";
                    if(start_time.equals(null)) start_time = "";
                    else {
                        sYear = start_time.substring(0,4);
                        sMonth = start_time.substring(5,7);
                        eday = start_time.substring(8,10);
                        start_time = start_time.substring(10);
                        start_time = start_time.substring(0, 6);
                    }
                    if(end_time==null) end_time="";
                    else{
                        end_time = end_time.substring(10);
                        end_time = end_time.substring(0,6);
                    }
                    if(end_time==null) end_time="";
                    if(lng_enter==null) lng_enter="";
                    if(lat_enter==null) lat_enter="";
                    if(lng_exit==null) lng_exit="";
                    if(lat_exit==null) lat_exit="";

                    // Display row for this month
                    if(Integer.parseInt(sMonth)==(Month_number+1) && Integer.parseInt(sYear)==Year_number) {
                        //int it;

                        TableRow tr = new TableRow(this);
                        TextView space = new TextView(this);

                        TextView ID = new TextView(this);
                        ID.setText(String.valueOf(eday+"/"+Month_number));
                        ID.setGravity(Gravity.CENTER);

                        TextView Enter_Time = new TextView(this);
                        Enter_Time.setText(String.valueOf(start_time));
                        Enter_Time.setGravity(Gravity.CENTER);

                        TextView Exit_Time = new TextView(this);
                        Exit_Time.setText(String.valueOf(end_time));
                        Exit_Time.setGravity(Gravity.CENTER);

                        TextView Enter_Location = new TextView(this);
                        if(!lng_enter.equals(""))
                            Enter_Location.setText(lng_enter.substring(0, 2) +"/" +lat_enter.substring(0, 2));
                        else
                            Enter_Location.setText("");
                        Enter_Location.setGravity(Gravity.CENTER);

                        TextView Exit_Location = new TextView(this);
                        if(!lng_exit.equals(""))
                            Exit_Location.setText(lng_exit.substring(0, 2) + "/" +lat_exit.substring(0, 2));
                        else
                            Exit_Location.setText("");
                        Exit_Location.setGravity(Gravity.CENTER);

                        tr.addView(space);
                        tr.addView(ID);
                        tr.addView(Enter_Time);
                        tr.addView(Exit_Time);
                        tr.addView(Enter_Location);
                        tr.addView(Exit_Location);

                        table.addView(tr);

                    }
                    resultSet.moveToNext();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month];
    }
    public void Prev_Month(View view){
        if(Month_number == 0) {
            Year_number--;
            Month_number=12;
        }
        Month_number--;
        Month_number=Month_number%12;
        SetTable();
    }
    public void Next_Month(View view){
        if(Month_number == 11)
            Year_number++;
        Month_number++;
        Month_number=Month_number%12;
        SetTable();
    }
    /**
     *  prevent balnk screen
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            return false; //I have tried here true also
        }
        return super.onKeyDown(keyCode, event);
    }
    public void on_click_logout(View view){
        //TODO update exit
        startActivity(new Intent(this, Login_Activity.class));
    }
    public void on_click_company_code(View view){

        //TODO Check Company code if valid!!!
     ////????????????/ if(Server.isComanyCodeValid(Company_code_input.getText().toString());


        if (!Company_Current_code.equals("")) // Edit
        {
            Company_Current_code="";
            Company_code_btn.setText(R.string.enter_comapny_code);
            Company_code_input.setText("");
            if (myDB == null)
                myDB = this.openOrCreateDatabase("GeoDB", MODE_PRIVATE, null);
            // Disatcive all User Actice
            try {
                ContentValues cv = new ContentValues();
                cv.put("Company_active", 0); //These Fields should be your String values of actual column names
                myDB.update("Company", cv, "Company_active=1", null);

            }
            catch (Exception e){
                e.printStackTrace();
            }


        }
        else //Enter new Comapny code
        {

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        Current_User =(User)getIntent().getSerializableExtra(Globals.EXTRA_USER);
    }
}
