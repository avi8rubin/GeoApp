package info.androidhive.materialtabs.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import info.androidhive.materialtabs.R;
import info.androidhive.materialtabs.GeoObjects.User;
import info.androidhive.materialtabs.common.Globals;
import info.androidhive.materialtabs.common.Server;
import info.androidhive.materialtabs.common.User_callback;

public class Login_Activity extends AppCompatActivity {


    private EditText Email,Password;
    private TextView Error;
    private SQLiteDatabase myDB;
    private Button Login_button,Sign_button;
    private ImageView Logo;
    private User currentUser = new User();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);
        Login_button = (Button)findViewById(R.id.login_btn);
        Login_button.setBackgroundResource(R.drawable.button_custom_setting);
        Sign_button = (Button) findViewById(R.id.signin_btn);
        Sign_button.setBackgroundResource(R.drawable.button_custom_setting);
        init();

    }


    public void on_click_login_btn(View view){

        Error.setText("");
        hideKeyBoard();                             //---hide the Android Soft Keyboard---//
        //Logo.animate().rotation(360f*6).setDuration(10000);
        currentUser.setEmail(Email.getText().toString().trim());
        currentUser.setPassword(Password.getText().toString().trim());
        Object returnObject = Server.getUserDetails(currentUser); //---Send To Server Parse---//
        if(returnObject instanceof String)
            Error.setText((String)returnObject);
        else {
            currentUser = (User) returnObject;
            set_new_current_user(currentUser);
            Intent ActivityByRole;
            if (currentUser.isWorker())
                ActivityByRole = new Intent(this, IconTabsActivity.class);
            else {
                if(currentUser.getCompanyCode().equals(""))
                    ActivityByRole = new Intent(this, createNewCompany.class);
                else
                    ActivityByRole = new Intent(this, Manager_screen.class);
            }
            ActivityByRole.putExtra(Globals.EXTRA_USER, currentUser);
            myDB.close();
            startActivity(ActivityByRole);
        }

    }

    /**
     * This function hide Keyboard
     */
    private void hideKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    public void  on_click_signin_btn(View view){
        startActivity(new Intent(this, SignIn_Activity.class));
    }
    @Override
    /**
     * This function cancel return button
     */
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            return false; //I have tried here true also
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Reset Email & Password & Error
     */
    public void init(){
        Email = (EditText)findViewById(R.id.login_email);
        Password = (EditText)findViewById(R.id.login_password);
        Error = (TextView)findViewById(R.id.error_text);
        Error.setText("");
        Logo = (ImageView) findViewById(R.id.imageView8);
        get_new_current_user();

        // Set user details if cane from signin activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if(extras.containsKey(Globals.EXTRA_USER)) {
                User user = (User) extras.getSerializable(Globals.EXTRA_USER);
                Email.setText(user.getEmail());
                Password.setText(user.getPassword());
            }

        }
    }

    /**
     * This function set to DB user
     * if User is new - insert
     * if User not new - update
     * else do nothing
     * @param user - User CallBack
     */
    public void set_new_current_user(User user){
        if (myDB == null)
            myDB = this.openOrCreateDatabase("GeoDB", MODE_PRIVATE, null);
        // Disatcive all User Actice
        try {
            ContentValues cv = new ContentValues();
            cv.put("User_active", 0); //These Fields should be your String values of actual column names
            myDB.update("Setting", cv, "User_active=1", null);
           // myDB.rawQuery("UPDATE Setting SET User_active=0 WHERE User_active=1;", null);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        // if user exist - active
        Cursor resultSet = myDB.rawQuery("SELECT * FROM Setting WHERE Email='"+user.getEmail()+"';", null);
        if (resultSet.getCount() > 0) {
            resultSet.moveToNext();
            int i = resultSet.getInt(7);
            try {
                ContentValues cv = new ContentValues();
                cv.put("User_active",1); //These Fields should be your String values of actual column names
                cv.put("User_Password", user.getPassword().toString()); //These Fields should be your String values of actual column names
                myDB.update("Setting", cv, "Email='" + user.getEmail() + "'", null);
            }

            catch (Exception e){
                e.printStackTrace();
            }
        }
        // user not exist - insert & update
        else {
            //get all user info
           try {
               ContentValues values = new ContentValues();
               values.put("User_ID", currentUser.getSystemID());
               values.put("Email", currentUser.getEmail());
               values.put("User_Password", currentUser.getPassword());
               values.put("First_Name", currentUser.getFirstName());
               values.put("Last_Name", currentUser.getLastName());
               values.put("User_TZ", currentUser.getUserID());
               values.put("User_Phone_Number", currentUser.getPhone());
               values.put("User_active", 1);

              myDB.execSQL("INSERT INTO Setting VALUES" +
                       "(NULL,'" + currentUser.getEmail() + "'" +
                       ",'" + currentUser.getPassword() + "'" +
                       ",'" + currentUser.getFirstName() + "'" +
                       ",'" + currentUser.getLastName() + "'" +
                       "," + currentUser.getUserID() + "" +
                       ",'" + currentUser.getPhone() + "'" +
                       ",1," +
                       "'"+currentUser.getCompanyCode()+"'" +
                       ",1);;");
           }
           catch (Exception e)
           {
               e.printStackTrace();
           }
        }
    }

    /**
     * This fuctnion get from DB how was last active
     */
    public void get_new_current_user(){

        if (myDB == null)
            myDB = this.openOrCreateDatabase("GeoDB", MODE_PRIVATE, null);
        Cursor resultSet = myDB.rawQuery("SELECT Email,User_Password FROM Setting WHERE User_active=1;", null);
        int x= resultSet.getCount();
        if (x > 0) {
            resultSet.moveToNext();
            String s1 = resultSet.getString(0);
            String s2 = resultSet.getString(1);
            Email.setText(s1);
            Password.setText(s2);
         }
        else {
            Email.setText("");
            Password.setText("");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }
}
